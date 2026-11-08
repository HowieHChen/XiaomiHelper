package dev.lackluster.mihelper.hook.base

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.reflect.Executable
import java.lang.reflect.Proxy
import org.junit.Assert.*
import org.junit.Test

class HookSessionTest {
    private val method = Target::class.java.getDeclaredMethod("call")
    private val host = HookSession.HostRecord("test", ClassLoader.getSystemClassLoader().parent,
        HookParam("test", "test", false))

    @Test fun legacyBridgeAndInitAreCalledOnce() {
        val events = mutableListOf<String>()
        val hooker = object : BaseHooker() {
            override fun onInit() { events += "init" }
            @Suppress("OVERRIDE_DEPRECATION")
            override fun onHook() { events += "legacy" }
        }
        val session = FakeFramework().session()
        session.attachRoot(hooker, host)
        session.attachRoot(hooker, host)
        assertEquals(listOf("init", "legacy"), events)
        assertEquals(StateUpdateResult.RESTART_REQUIRED, hooker.updateSelfState(false))
        assertTrue(hooker.isRunning)
    }

    @Test fun stopAndDisposeOwnDifferentResourcesAndCanRestart() {
        val events = mutableListOf<String>()
        val hooker = object : BaseHooker() {
            override val supportsRuntimeToggle = true
            override fun onInit() {
                events += "init"
                registerDisposeCleanup { events += "subscription" }
            }
            override fun onStart() {
                events += "start"
                registerStopCleanup { events += "job1" }
                registerStopCleanup { events += "job2" }
            }
            override fun onStop() { events += "stop" }
            override fun onDispose() { events += "dispose" }
        }
        val session = FakeFramework().session()
        session.attachRoot(hooker, host)
        assertEquals(StateUpdateResult.APPLIED, hooker.updateSelfState(false))
        assertEquals(listOf("init", "start", "stop", "job2", "job1"), events)
        hooker.updateSelfState(true)
        session.dispose()
        session.dispose()
        assertEquals(listOf("init", "start", "stop", "job2", "job1", "start", "stop", "job2", "job1", "dispose", "subscription"), events)
        assertEquals(StateUpdateResult.DISPOSED, hooker.updateSelfState(true))
    }

    @Test fun initiallyDisabledFeatureRequiresOptInForLateStart() {
        val hooker = object : BaseHooker() {
            override fun onInit() { updateSelfState(false) }
            override fun onStart() { fail("Must not start") }
        }
        FakeFramework().session().attachRoot(hooker, host)
        assertFalse(hooker.isRunning)
        assertEquals(StateUpdateResult.RESTART_REQUIRED, hooker.updateSelfState(true))
    }

    @Test fun parentTogglePreflightsTheWholeSubtree() {
        val child = object : BaseHooker() {}
        val parent = object : BaseHooker() {
            override val supportsRuntimeToggle = true
            override fun onInit() { attach(child) }
        }
        FakeFramework().session().attachRoot(parent, host)
        assertEquals(StateUpdateResult.RESTART_REQUIRED, parent.updateSelfState(false))
        assertTrue(parent.isRunning)
        assertTrue(child.isRunning)
    }

    @Test fun cleanupFailureDoesNotSkipOtherResources() {
        var cleaned = false
        var disposed = false
        val hooker = object : BaseHooker() {
            override fun onStart() {
                registerStopCleanup { cleaned = true }
                registerStopCleanup { error("cleanup failure") }
            }
            override fun onDispose() { disposed = true }
        }
        val session = FakeFramework().session()
        session.attachRoot(hooker, host)
        assertThrows(IllegalStateException::class.java) { session.dispose() }
        assertTrue(cleaned)
        assertTrue(disposed)
        session.dispose()
    }

    @Test fun initializationHooksSurviveFeatureStopButNotDisposal() {
        val framework = FakeFramework()
        val hooker = object : BaseHooker() {
            override val supportsRuntimeToggle = true
            override fun onInit() { method.hook(id = "discovery") { result("discovery") } }
            override fun onStart() {
                method.hook(id = "business") { result("business") }
                method.hook(managed = false, id = "manual") { result("manual") }
            }
        }
        val session = framework.session()
        session.attachRoot(hooker, host)
        hooker.updateSelfState(false)
        assertEquals(setOf("discovery", "manual"), framework.active().map { it.id }.toSet())
        assertEquals("discovery", framework.invoke("discovery"))
        assertEquals("original", framework.invoke("manual")) // Unmanaged business callback is gated.
        session.dispose()
        assertTrue(framework.active().isEmpty())
    }

    @Test fun replacingAHandleKeepsTheNewHandleManaged() {
        val framework = FakeFramework()
        lateinit var handle: XposedInterface.HookHandle
        val hooker = object : BaseHooker() {
            override val supportsRuntimeToggle = true
            override fun onStart() { handle = method.hook(id = "test") { result("old") } }
        }
        framework.session().attachRoot(hooker, host)
        val old = handle
        handle = old.replaceHook { "new" }
        old.unhook() // Invalidated wrapper cannot remove its replacement.
        assertEquals("new", framework.invoke("test"))
        hooker.updateSelfState(false)
        assertTrue(framework.active().isEmpty())
    }

    @Test fun duplicateIdInvalidatesCurrentWrapperWithoutUnhookingReplacement() {
        val framework = FakeFramework()
        lateinit var old: XposedInterface.HookHandle
        val hooker = object : BaseHooker() {
            override fun onStart() {
                old = method.hook(id = "same") { result("old") }
                method.hook(id = "same") { result("new") }
            }
        }
        val session = framework.session()
        session.attachRoot(hooker, host)
        old.unhook()
        assertEquals("new", framework.invoke("same"))
        session.dispose()
        assertTrue(framework.active().isEmpty())
    }

    @Test fun reloadReplacesStableHooksAndRemovesAnonymousAndObsoleteHooks() {
        val framework = FakeFramework()
        fun node(version: String) = object : BaseHooker() {
            override val supportsHotReload = true
            override val hookerId = "feature"
            override fun onStart() {
                method.hook(id = "stable") { result(version) }
                if (version == "v1") {
                    method.hook(id = "removed") { result("obsolete") }
                    method.hook { result("anonymous") }
                }
            }
        }
        val oldSession = framework.session()
        oldSession.recordHost(host)
        oldSession.attachRoot(node("v1"), host)
        var snapshot: Any? = null
        assertTrue(oldSession.retireForReload { snapshot = it })
        assertEquals("original", framework.invoke("stable")) // Retired code is transparent.
        val next = framework.session()
        next.beginReload(snapshot, framework.active())
        assertTrue(framework.active().none { it.id == null })
        next.attachRoot(node("v2"), next.recordedHosts().single())
        next.finishReload()
        assertEquals(listOf("stable"), framework.active().map { it.id })
        assertEquals("v2", framework.invoke("stable"))
        next.dispose()
        assertTrue(framework.active().isEmpty())
    }

    @Test fun stateIsRestoredBeforeInitAndReadyRunsAfterStart() {
        val framework = FakeFramework()
        val first = object : BaseHooker() {
            override val supportsHotReload = true
            override val hookerId = "stateful"
            override fun onSaveReloadState(): Any = hashMapOf("count" to 42)
        }
        val old = framework.session()
        old.recordHost(host)
        old.attachRoot(first, host)
        var snapshot: Any? = null
        old.retireForReload { snapshot = it }
        val events = mutableListOf<String>()
        val next = framework.session()
        next.beginReload(snapshot, emptyList())
        val second = object : BaseHooker() {
            override val hookerId = "stateful"
            override fun onRestoreReloadState(state: Any?) {
                assertEquals(42, (state as Map<*, *>)["count"])
                events += "restore"
            }
            override fun onInit() { events += "init" }
            override fun onStart() { events += "start" }
            override fun onReloadReady() { assertTrue(isRunning); events += "ready" }
        }
        next.attachRoot(second, next.recordedHosts().single())
        assertEquals(listOf("restore", "init", "start", "ready"), events)
    }

    @Test fun emptyGenerationCanReloadAndPreserveHosts() {
        val old = FakeFramework().session()
        old.recordHost(host)
        var snapshot: Any? = null
        assertTrue(old.retireForReload { snapshot = it })
        val next = FakeFramework().session()
        next.beginReload(snapshot, emptyList())
        assertEquals(listOf(host), next.recordedHosts())
        next.finishReload()
    }

    @Test fun disabledInitializedNodesStillBlockReload() {
        val hooker = object : BaseHooker() { override fun onInit() { updateSelfState(false) } }
        val session = FakeFramework().session()
        session.attachRoot(hooker, host)
        assertFalse(session.retireForReload { fail("Must not save") })
        assertTrue(session.isActive)
        assertEquals(1, session.reloadBlockers().size)
    }

    @Test fun unsafeSavedStateIsRejectedBeforeDisposal() {
        var disposed = false
        val hooker = object : BaseHooker() {
            override val supportsHotReload = true
            override fun onSaveReloadState(): Any = listOf(this)
            override fun onDispose() { disposed = true }
        }
        val session = FakeFramework().session()
        session.attachRoot(hooker, host)
        assertThrows(IllegalArgumentException::class.java) { session.retireForReload {} }
        assertTrue(session.isActive)
        assertTrue(hooker.isRunning)
        assertFalse(disposed)
    }

    @Test fun failedStartCleansPartialResourcesAndHooks() {
        val framework = FakeFramework()
        var cleaned = false
        val hooker = object : BaseHooker() {
            override fun onStart() {
                registerStopCleanup { cleaned = true }
                method.hook { result("partial") }
                error("start failure")
            }
        }
        assertThrows(IllegalStateException::class.java) { framework.session().attachRoot(hooker, host) }
        assertTrue(cleaned)
        assertFalse(hooker.isRunning)
        assertTrue(framework.active().isEmpty())
    }

    @Test fun childCleanupFailureStillStopsParentAndSibling() {
        val events = mutableListOf<String>()
        fun child(id: String, fail: Boolean) = object : BaseHooker() {
            override val hookerId = id
            override val supportsRuntimeToggle = true
            override fun onStop() { events += id; if (fail) error("stop failed") }
        }
        val parent = object : BaseHooker() {
            override val supportsRuntimeToggle = true
            override fun onInit() { attach(child("first", false)); attach(child("second", true)) }
            override fun onStop() { events += "parent" }
        }
        FakeFramework().session().attachRoot(parent, host)
        assertThrows(IllegalStateException::class.java) { parent.updateSelfState(false) }
        assertEquals(listOf("second", "first", "parent"), events)
        assertFalse(parent.isRunning)
    }

    @Test fun reentrantToggleDuringStartIsRejected() {
        val hooker = object : BaseHooker() {
            override val supportsRuntimeToggle = true
            override fun onStart() { assertEquals(StateUpdateResult.BUSY, updateSelfState(false)) }
        }
        FakeFramework().session().attachRoot(hooker, host)
        assertTrue(hooker.isRunning)
    }

    @Test fun oldActivationCallbacksDoNotReviveAfterRestart() {
        var token = 0L
        var stillCurrent: (() -> Boolean)? = null
        var activation = 0
        val framework = FakeFramework()
        val hooker = object : BaseHooker() {
            override val supportsRuntimeToggle = true
            override fun onStart() {
                activation++
                if (activation == 1) {
                    token = captureActivation()
                    stillCurrent = { isCurrentActivation(token) }
                    method.hook(managed = false, id = "old") { result("stale") }
                }
            }
        }
        framework.session().attachRoot(hooker, host)
        val oldActivationIsCurrent = requireNotNull(stillCurrent)
        assertTrue(oldActivationIsCurrent())
        hooker.updateSelfState(false)
        hooker.updateSelfState(true)
        assertFalse(oldActivationIsCurrent())
        assertEquals("original", framework.invoke("old"))
    }

    @Test fun failedNewGenerationCanRemoveAllRemainingOldHooks() {
        val framework = FakeFramework()
        val first = object : BaseHooker() {
            override val supportsHotReload = true
            override fun onStart() { method.hook(id = "old") { result("old") } }
        }
        val old = framework.session()
        old.attachRoot(first, host)
        var snapshot: Any? = null
        old.retireForReload { snapshot = it }
        val next = framework.session()
        assertThrows(IllegalStateException::class.java) { next.beginReload(hashMapOf("version" to -1), framework.active()) }
        next.dispose()
        assertTrue(framework.active().isEmpty())
        assertNotNull(snapshot)
    }

    @Test fun oneInstanceCannotBeReboundToAnotherClassLoader() {
        val session = FakeFramework().session()
        val hooker = object : BaseHooker() {}
        session.attachRoot(hooker, host)
        val anotherLoader = object : ClassLoader(host.classLoader) {}
        assertThrows(IllegalStateException::class.java) { session.attachRoot(hooker, host.copy(classLoader = anotherLoader)) }
        assertTrue(hooker.isRunning)
    }

    @Test fun detachDisposesSubtreeAndAllHookLifetimesWithoutStoppingParent() {
        val events = mutableListOf<String>()
        val framework = FakeFramework()
        val grandchild = object : BaseHooker() {
            override fun onInit() {
                method.hook(id = "grandchild-init") { result("init") }
                registerDisposeCleanup { events += "grandchild-subscription" }
            }
            override fun onStart() {
                method.hook(managed = false, id = "grandchild-manual") { result("manual") }
                registerStopCleanup { events += "grandchild-job" }
            }
            override fun onStop() { events += "grandchild-stop" }
            override fun onDispose() { events += "grandchild-dispose" }
        }
        val child = object : BaseHooker() {
            override fun onInit() {
                attach(grandchild)
                registerDisposeCleanup { events += "child-subscription" }
            }
            override fun onStart() {
                method.hook(id = "child-start") { result("child") }
                registerStopCleanup { events += "child-job" }
            }
            override fun onStop() { events += "child-stop" }
            override fun onDispose() { events += "child-dispose" }
        }
        val parent = object : BaseHooker() {
            override val supportsHotReload = true
            override fun onInit() { attach(child) }
            override fun onStart() { method.hook(id = "parent") { result("parent") } }
        }
        val session = framework.session()
        session.attachRoot(parent, host)
        assertTrue(parent.detach(child)) // Does not require runtime-toggle opt-in.
        assertFalse(parent.detach(child))
        assertEquals(listOf("grandchild-stop", "grandchild-job", "grandchild-dispose", "grandchild-subscription",
            "child-stop", "child-job", "child-dispose", "child-subscription"), events)
        assertEquals(listOf("parent"), framework.active().map { it.id })
        assertTrue(parent.isRunning)
        assertFalse(child.isActive)
        assertFalse(grandchild.isActive)
        assertTrue(session.reloadBlockers().isEmpty()) // Detached nodes leave the lifecycle tree.
        session.dispose()
        assertEquals(8, events.size) // No repeated child cleanup on parent disposal.
    }

    @Test fun detachAllowsFreshSameIdInstanceOnNewLoaderButNeverRebindsOldInstance() {
        var creations = 0
        fun child() = object : BaseHooker() {
            override val hookerId = "plugin"
            override fun onInit() { creations++ }
            override fun onStart() { method.hook(id = "plugin-hook") { result("plugin") } }
        }
        val framework = FakeFramework()
        val old = child()
        val parent = object : BaseHooker() { override fun onInit() { attach(old) } }
        framework.session().attachRoot(parent, host)
        val newLoader = object : ClassLoader(host.classLoader) {}
        assertThrows(IllegalStateException::class.java) { parent.attach(old, newLoader) }
        assertTrue(parent.detach(old))
        assertTrue(framework.active().isEmpty())
        assertThrows(IllegalStateException::class.java) { parent.attach(old) }
        assertThrows(IllegalStateException::class.java) { parent.attach(old, newLoader) }
        val next = child()
        parent.attach(next, newLoader)
        assertSame(host.classLoader, old.classLoader)
        assertSame(newLoader, next.classLoader)
        assertEquals(2, creations)
        assertTrue(next.isRunning)
        assertEquals(1, framework.active().size)
    }

    @Test fun detachNonChildDoesNotDisposeItOrTouchAnotherParentsChild() {
        var disposed = false
        val child = object : BaseHooker() { override fun onDispose() { disposed = true } }
        val owner = object : BaseHooker() { override fun onInit() { attach(child) } }
        val other = object : BaseHooker() {}
        val session = FakeFramework().session()
        session.attachRoot(owner, host)
        session.attachRoot(other, host)
        assertFalse(other.detach(child))
        assertFalse(other.detach(object : BaseHooker() {}))
        assertFalse(disposed)
        assertTrue(child.isRunning)
        assertTrue(owner.detach(child))
        assertTrue(disposed)
    }

    @Test fun detachInitiallyDisabledChildCleansInitResourcesWithoutStop() {
        val events = mutableListOf<String>()
        val child = object : BaseHooker() {
            override fun onInit() {
                updateSelfState(false)
                registerDisposeCleanup { events += "unsubscribe" }
            }
            override fun onStart() { fail("Disabled child must not start") }
            override fun onStop() { fail("Never started") }
            override fun onDispose() { events += "dispose" }
        }
        val parent = object : BaseHooker() { override fun onInit() { attach(child) } }
        FakeFramework().session().attachRoot(parent, host)
        assertTrue(parent.detach(child))
        assertEquals(listOf("dispose", "unsubscribe"), events)
    }

    @Test fun detachCleanupFailureStillRemovesHooksAndReleasesTheChildId() {
        val events = mutableListOf<String>()
        val framework = FakeFramework()
        val child = object : BaseHooker() {
            override val hookerId = "plugin"
            override fun onInit() {
                method.hook(id = "init") { result("init") }
                registerDisposeCleanup { events += "subscription" }
            }
            override fun onStart() {
                method.hook(managed = false, id = "manual") { result("manual") }
                registerStopCleanup { events += "job" }
            }
            override fun onStop() { error("stop failed") }
            override fun onDispose() { events += "dispose" }
        }
        val parent = object : BaseHooker() { override fun onInit() { attach(child) } }
        framework.session().attachRoot(parent, host)
        assertThrows(IllegalStateException::class.java) { parent.detach(child) }
        assertEquals(listOf("job", "dispose", "subscription"), events)
        assertTrue(framework.active().isEmpty())
        assertFalse(parent.detach(child))
        val next = object : BaseHooker() { override val hookerId = "plugin" }
        parent.attach(next)
        assertTrue(next.isRunning)
    }

    @Test fun detachDuringStartIsRejectedWithoutDestroyingTheStartingChild() {
        lateinit var parent: BaseHooker
        val child = object : BaseHooker() {
            override fun onStart() {
                assertThrows(IllegalStateException::class.java) { parent.detach(this) }
            }
        }
        parent = object : BaseHooker() { override fun onInit() { attach(child) } }
        FakeFramework().session().attachRoot(parent, host)
        assertTrue(child.isRunning)
        assertTrue(parent.detach(child))
    }

    @Test fun nestedPackageCallbackCannotRebindOrOverwriteTheInitialHost() {
        val session = FakeFramework().session()
        val original = host.copy(param = host.param.copy(isFirstPackage = true))
        val nested = host.copy(classLoader = object : ClassLoader(host.classLoader) {})
        var initializations = 0
        var starts = 0
        lateinit var root: BaseHooker
        fun onPackageLoaded(record: HookSession.HostRecord) {
            if (!session.recordHost(record)) return
            session.attachRoot(root, record)
        }
        root = object : BaseHooker() {
            override fun onInit() {
                initializations++
                // 模拟 DexKit.createPackageContext 在外层初始化结束前回调同一个包。
                onPackageLoaded(nested)
            }
            override fun onStart() { starts++ }
        }
        onPackageLoaded(original)
        onPackageLoaded(nested)
        assertEquals(1, initializations)
        assertEquals(1, starts)
        assertSame(original, session.recordedHosts().single())
        assertSame(original.classLoader, root.classLoader)
        assertTrue(root.isRunning)
    }

    @Test fun sameBindingCanReenterAttachRootBeforeInitCompletes() {
        val session = FakeFramework().session()
        var initializations = 0
        var starts = 0
        val root = object : BaseHooker() {
            override fun onInit() {
                initializations++
                session.attachRoot(this, host)
            }
            override fun onStart() { starts++ }
        }
        session.attachRoot(root, host)
        assertEquals(1, initializations)
        assertEquals(1, starts)
        assertThrows(IllegalStateException::class.java) {
            session.attachRoot(root, host.copy(param = host.param.copy(isFirstPackage = true)))
        }
    }

    @Test fun nestedReadyCannotOverwriteCanonicalLoaderOrPackageFlags() {
        val session = FakeFramework().session()
        val original = host.copy(param = host.param.copy(isFirstPackage = true))
        session.recordHost(original)
        val nestedLoader = object : ClassLoader(host.classLoader) {}
        assertFalse(session.markHostReady(host.key, nestedLoader, nestedLoader, true))
        assertFalse(session.markHostReady(host.key, host.classLoader, nestedLoader, false))
        assertSame(original, session.recordedHosts().single())
        val finalLoader = object : ClassLoader(host.classLoader) {}
        assertTrue(session.markHostReady(host.key, host.classLoader, finalLoader, true))
        val ready = session.recordedHosts().single()
        assertSame(host.classLoader, ready.classLoader)
        assertSame(finalLoader, ready.readyClassLoader)
        assertTrue(ready.param.isFirstPackage)
        assertTrue(ready.param.isPackageReady)
        assertFalse(session.markHostReady(host.key, host.classLoader, nestedLoader, true))
        assertSame(ready, session.recordedHosts().single())
    }

    @Test fun readyWithoutLoadedDoesNotClaimHostFromNestedContext() {
        val session = FakeFramework().session()
        assertFalse(session.markHostReady(host.key, host.classLoader, host.classLoader, false))
        assertTrue(session.recordedHosts().isEmpty())
        assertTrue(session.recordHost(host))
    }

    @Test fun packageClaimIsRetainedWhenInitialRootFails() {
        val session = FakeFramework().session()
        val root = object : BaseHooker() { override fun onInit() { error("failed initialization") } }
        assertTrue(session.recordHost(host))
        assertThrows(IllegalStateException::class.java) { session.attachRoot(root, host) }
        // 失败节点已经 dispose，后续同包通知不能再重试这个单例。
        assertFalse(session.recordHost(host.copy(param = host.param.copy(isFirstPackage = true))))
        assertFalse(root.isActive)
        assertSame(host, session.recordedHosts().single())
    }

    @Test fun restoredHostsDeduplicateNestedCallbacksDuringReloadInitialization() {
        val old = FakeFramework().session()
        old.recordHost(host)
        old.markHostReady(host.key, host.classLoader, host.classLoader, false)
        var saved: Any? = null
        assertTrue(old.retireForReload { saved = it })
        val next = FakeFramework().session()
        next.beginReload(saved, emptyList())
        val restoredHost = next.recordedHosts().single()
        var starts = 0
        val root = object : BaseHooker() {
            override fun onInit() {
                val nested = host.copy(classLoader = object : ClassLoader(host.classLoader) {})
                assertFalse(next.recordHost(nested))
                if (next.recordHost(nested)) next.attachRoot(this, nested)
            }
            override fun onStart() { starts++ }
        }
        next.attachRoot(root, restoredHost)
        next.finishReload()
        assertEquals(1, starts)
        assertSame(restoredHost, next.recordedHosts().single())
        assertTrue(restoredHost.param.isPackageReady)
        assertSame(host.classLoader, restoredHost.readyClassLoader)
    }

    @Test fun packageClaimsDoNotSuppressOtherPackagesOrReplaceDisabledHostRecords() {
        val session = FakeFramework().session()
        assertTrue(session.recordHost(host)) // 模块关闭时也记录，此时没有挂载任何根节点。
        assertFalse(session.recordHost(host.copy(classLoader = object : ClassLoader(host.classLoader) {})))
        val other = host.copy(key = "other", param = host.param.copy(packageName = "other"))
        assertTrue(session.recordHost(other))
        assertEquals(listOf(host, other), session.recordedHosts())
    }

    private class Target { fun call(): String = "original" }

    /** A fake framework for contract tests, not module code calling attachFramework in production. */
    private class FakeFramework {
        private val handles = mutableListOf<Handle>()
        fun session(): HookSession {
            val framework = Proxy.newProxyInstance(XposedInterface::class.java.classLoader,
                arrayOf(XposedInterface::class.java)) { _, method, args ->
                when (method.name) {
                    "hook" -> builder(args!![0] as Executable)
                    "getApiVersion" -> 102
                    else -> error("Unexpected framework API: ${method.name}")
                }
            } as XposedInterface
            val module = object : XposedModule() {}
            module.attachFramework(framework) {}
            return HookSession(module)
        }
        fun active(): List<Handle> = handles.filter { it.valid }
        fun invoke(id: String): Any? {
            val handle = active().single { it.id == id }
            val chain = Proxy.newProxyInstance(XposedInterface.Chain::class.java.classLoader,
                arrayOf(XposedInterface.Chain::class.java)) { _, method, _ ->
                when (method.name) {
                    "proceed" -> "original"
                    "getExecutable" -> handle.executable
                    else -> error("Unexpected chain API: ${method.name}")
                }
            } as XposedInterface.Chain
            return handle.hooker.intercept(chain)
        }
        private fun builder(executable: Executable) = object : XposedInterface.HookBuilder {
            private var id: String? = null
            override fun setId(id: String?): XposedInterface.HookBuilder { this.id = id; return this }
            override fun setPriority(priority: Int): XposedInterface.HookBuilder = this
            override fun setExceptionMode(mode: XposedInterface.ExceptionMode): XposedInterface.HookBuilder = this
            override fun intercept(hooker: XposedInterface.Hooker): XposedInterface.HookHandle = install(executable, id, hooker)
        }
        private fun install(executable: Executable, id: String?, hooker: XposedInterface.Hooker): Handle {
            if (id != null) active().filter { it.executable == executable && it.id == id }.forEach { it.valid = false }
            return Handle(executable, id, hooker).also { handles.add(it) }
        }
        inner class Handle(private val target: Executable, private val key: String?, val hooker: XposedInterface.Hooker) : XposedInterface.HookHandle {
            var valid = true
            override fun getExecutable(): Executable { check(valid); return target }
            override fun getId(): String? { check(valid); return key }
            override fun unhook() { valid = false }
            override fun replaceHook(hooker: XposedInterface.Hooker): XposedInterface.HookHandle {
                check(valid)
                valid = false
                return install(target, key, hooker)
            }
        }
    }
}
