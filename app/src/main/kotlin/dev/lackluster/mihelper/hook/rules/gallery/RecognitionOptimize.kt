/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references StarVoyager <https://github.com/hosizoraru/StarVoyager/blob/star/app/src/main/kotlin/star/sky/voyager/hook/hooks/gallery/AlbumOptimize.kt>
 * Copyright (C) 2023 hosizoraru

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lackluster.mihelper.hook.rules.gallery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object RecognitionOptimize : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Gallery.PATH_OPTIM) {
            val albumManagerCls = "com.miui.gallery.provider.album.AlbumManager".toClass()
            val albumDataHelperCls = "com.miui.gallery.model.dto.utils.AlbumDataHelper".toClass()
            albumManagerCls.method {
                name = "getQueryUnionScreenshotsRecordsAlbumSql"
            }.hook {
                replaceTo(
                    " UNION SELECT _id, name, attributes, dateTaken, dateModified, sortInfo, extra, localFlag, serverId, localPath, realDateModified, serverTag, serverStatus, editedColumns, serverStatus,photoCount, size, sortBy,coverId, coverSyncState, coverSize, coverPath, coverSha1,is_manual_set_cover FROM ( SELECT 2147483645 AS _id, 'SCREENSHOTS OR RECORDERS' AS name, (SELECT attributes FROM album WHERE localPath COLLATE NOCASE IN ('DCIM/Screenshots')) AS attributes, 996 AS dateTaken, 996 AS dateModified, 0 AS sortBy, '996' AS sortInfo, NULL AS extra, 0 AS localFlag, 'custom' AS serverStatus, -2147483645 AS serverId, 0 AS realDateModified, NULL AS serverTag, NULL AS editedColumns, NULL AS localPath, _id AS coverId,  CASE WHEN localFlag = 0  THEN 0 WHEN localFlag IN (5, 6, 9) THEN 1 ELSE 3 END  AS coverSyncState, sha1 AS coverSha1, size AS coverSize, ( CASE WHEN (microthumbfile NOT NULL and microthumbfile != '') THEN microthumbfile WHEN (thumbnailFile NOT NULL and thumbnailFile != '') THEN thumbnailFile ELSE localFile END ) AS coverPath, 0 AS is_manual_set_cover, max( mixedDateTime ) AS latest_photo ,count(_id) AS photoCount, sum(size) AS size FROM ( SELECT _id,localFlag,localFile,thumbnailFile,microthumbfile,localGroupId,size,mixedDateTime,dateTaken,dateModified,serverType,sha1,serverStatus,creatorId FROM cloud WHERE (localFlag IS NULL OR localFlag NOT IN (11, 0, -1, 2, 15) OR (localFlag=0 AND (serverStatus='custom' OR serverStatus = 'recovery'))) AND (localGroupId IN (SELECT _id FROM album WHERE localPath COLLATE NOCASE IN ('DCIM/Screenshots', 'DCIM/screenrecorder', 'Pictures/Screenshots', 'Movies/screenrecorder')))) )"
                )
            }
            albumDataHelperCls.method {
                name = "getScreenshotsLocalPath"
            }.hook {
                replaceTo("Pictures/Screenshots")
            }
            albumDataHelperCls.method {
                name = "getScreenRecorderLocalPath"
            }.hook {
                replaceTo("Movies/ScreenRecorder")
            }
        }
    }
}