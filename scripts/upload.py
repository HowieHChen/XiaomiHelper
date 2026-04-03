import os
import json
import subprocess
import sys
import html

def format_html_caption(text):
    if not text:
        return ""
    parts = text.split('```')
    for i in range(len(parts)):
        if i % 2 == 1:
            parts[i] = f'<pre>{html.escape(parts[i].strip())}</pre>'
        else:
            parts[i] = html.escape(parts[i])
    return "".join(parts)

def get_apk(base_dir):
    if not os.path.exists(base_dir):
        return None
    for root, dirs, files in os.walk(base_dir):
        for f in files:
            if f.endswith('.apk'):
                return os.path.abspath(os.path.join(root, f))
    return None

def send_text_message(bot_token, channel_id, text):
    if not text:
        return
    cmd = [
        'curl', '-sS', '-g',
        f'https://api.telegram.org/bot{bot_token}/sendMessage',
        '--form-string', f'chat_id={channel_id}',
        '--form-string', f'text={text}',
        '--form-string', 'parse_mode=HTML'
    ]
    try:
        res = subprocess.run(cmd, capture_output=True, text=True, check=True)
        if '"ok":false' in res.stdout:
            print(f"WARNING: Changelog message rejected: {res.stdout}")
        else:
            print("INFO: Changelog message sent successfully.")
    except subprocess.CalledProcessError as e:
        print(f"WARNING: Failed to send changelog message. {e.stderr.strip()}")

def send_document(bot_token, channel_id, file_path):
    print(f"INFO: Uploading pure APK file: {os.path.basename(file_path)}")
    cmd = [
        'curl', '-v', '-sS', '-g',
        f'https://api.telegram.org/bot{bot_token}/sendDocument',
        '--form-string', f'chat_id={channel_id}',
        '-F', f'document=@{file_path}'
    ]
    try:
        res = subprocess.run(cmd, capture_output=True, text=True, check=True)
        if '"ok":false' in res.stdout:
            print(f"ERROR: Telegram API rejected the file: {res.stdout}")
            return False
        else:
            print(f"INFO: File uploaded successfully: {os.path.basename(file_path)}")
            return True
    except subprocess.CalledProcessError as e:
        print(f"ERROR: File upload failed (exit code {e.returncode})")
        print("--- START CURL DEBUG LOG ---")
        print(e.stderr.strip())
        print("--- END CURL DEBUG LOG ---")
        return False

def main():
    bot_token = os.environ.get('BOT_TOKEN', '').strip()
    channel_id = os.environ.get('CHANNEL_ID', '').strip()
    raw_caption = os.environ.get('COMMIT_MESSAGE', 'No changelog provided').strip()

    if not bot_token or not channel_id:
        print("ERROR: Invalid BOT_TOKEN or CHANNEL_ID")
        sys.exit(1)

    safe_caption = format_html_caption(raw_caption)

    max_single_size = 50 * 1024 * 1024
    max_request_size = 49 * 1024 * 1024

    paths = {
        'release': get_apk('./app/build/outputs/apk/release'),
        'debug': get_apk('./app/build/outputs/apk/debug')
    }

    valid_uploads = {}

    for label, path in paths.items():
        if path:
            size = os.path.getsize(path)
            if size <= max_single_size:
                valid_uploads[label] = path
            else:
                print(f"WARNING: APK file for {label.capitalize()} exceeds 50MB ({size/1024/1024:.2f}MB) - SKIPPED")
        else:
            print(f"WARNING: APK file for {label} not found")

    if not valid_uploads:
        print("WARNING: No APK matching the criteria can be uploaded.")
        return

    print(f"INFO: Checking total file size for {list(valid_uploads.keys())}")

    if len(valid_uploads) == 2:
        total_size = os.path.getsize(valid_uploads['release']) + os.path.getsize(valid_uploads['debug'])
        if total_size > max_request_size:
            print(f"WARNING: Total file size ({total_size/1024/1024:.2f}MB) exceeds the 49MB safe upload limit")
            print("INFO: Discarding debug package to ensure release package is uploaded")
            del valid_uploads['debug']

    print(f"INFO: Preparing to upload {list(valid_uploads.keys())}")

    upload_success = False

    if len(valid_uploads) == 1:
        label = list(valid_uploads.keys())[0]
        upload_success = send_document(bot_token, channel_id, valid_uploads[label])
    else:
        media = [
            {"type": "document", "media": "attach://release"},
            {"type": "document", "media": "attach://debug"}
        ]
        cmd = [
            'curl', '-v', '-sS', '-g',
            f'https://api.telegram.org/bot{bot_token}/sendMediaGroup',
            '--form-string', f'chat_id={channel_id}',
            '--form-string', f'media={json.dumps(media)}',
            '-F', f'release=@{valid_uploads["release"]}',
            '-F', f'debug=@{valid_uploads["debug"]}'
        ]
        try:
            res = subprocess.run(cmd, capture_output=True, text=True, check=True)
            if '"ok":false' in res.stdout:
                print(f"ERROR: Telegram API rejected MediaGroup: {res.stdout}")
            else:
                print("INFO: MediaGroup uploaded successfully")
                upload_success = True
        except subprocess.CalledProcessError as e:
            print(f"ERROR: Send MediaGroup failed (exit code {e.returncode})")
            print("--- START CURL DEBUG LOG ---")
            print(e.stderr.strip())
            print("--- END CURL DEBUG LOG ---")

            print("INFO: Falling back to single file upload for Release package...")
            if 'release' in valid_uploads:
                upload_success = send_document(bot_token, channel_id, valid_uploads['release'])

    if upload_success and safe_caption:
        print("INFO: File uploaded. Now sending changelog message...")
        send_text_message(bot_token, channel_id, safe_caption)

if __name__ == '__main__':
    main()