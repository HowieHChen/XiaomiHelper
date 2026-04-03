import os
import json
import subprocess
import sys

def get_apk(base_dir):
    if not os.path.exists(base_dir):
        return None
    for root, dirs, files in os.walk(base_dir):
        for f in files:
            if f.endswith('.apk'):
                return os.path.join(root, f)
    return None

def send_document(bot_token, channel_id, file_path, caption=""):
    cmd = [
        'curl', '-s',
        f'https://api.telegram.org/bot{bot_token}/sendDocument',
        '-F', f'chat_id={channel_id}',
        '-F', f'document=@{file_path}',
        '-F', 'parse_mode=MarkdownV2'
    ]
    if caption:
        cmd.extend(['-F', f'caption={caption}'])

    try:
        res = subprocess.run(cmd, capture_output=True, text=True, check=True)
        print(f"INFO: Sent successfully {os.path.basename(file_path)}")
        if '"ok":false' in res.stdout:
            print(f"ERROR: Send failed {res.stdout}")
    except subprocess.CalledProcessError as e:
        print(f"ERROR: Send failed ({e.returncode}) {e.stderr}")

def main():
    bot_token = os.environ.get('BOT_TOKEN')
    channel_id = os.environ.get('CHANNEL_ID')
    caption = os.environ.get('COMMIT_MESSAGE', 'No changelog provided')

    if not bot_token or not channel_id:
        print("ERROR: Invalid BOT_TOKEN / CHANNEL_ID")
        sys.exit(1)

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
                print(f"WARNING: APK file for {label.capitalize()} exceeds 50MB ({size/1024/1024:.2f}MB) SKIPPED")
        else:
            print(f"ERROR: APK file for {label} not found")

    if not valid_uploads:
        print("WARNING: No APK matching the criteria can be uploaded.")
        return

    print(f"INFO: Check the total file size {list(valid_uploads.keys())}")

    if len(valid_uploads) == 2:
        total_size = os.path.getsize(valid_uploads['release']) + os.path.getsize(valid_uploads['debug'])
        if total_size > max_request_size:
            print(f"WARNING: Total file size (){total_size/1024/1024:.2f}MB) exceeds the upload limit")
            print("INFO: Discard debug package")
            del valid_uploads['debug']

    print(f"INFO: Preparing to upload {list(valid_uploads.keys())}")

    if len(valid_uploads) == 1:
        label = list(valid_uploads.keys())[0]
        send_document(bot_token, channel_id, valid_uploads[label], caption)
    else:
        media = [
            {"type": "document", "media": "attach://release", "parse_mode": "MarkdownV2", "caption": caption},
            {"type": "document", "media": "attach://debug"}
        ]
        cmd = [
            'curl', '-s',
            f'https://api.telegram.org/bot{bot_token}/sendMediaGroup',
            '-F', f'chat_id={channel_id}',
            '-F', f'media={json.dumps(media)}',
            '-F', f'release=@{valid_uploads["release"]}',
            '-F', f'debug=@{valid_uploads["debug"]}'
        ]
        try:
            res = subprocess.run(cmd, capture_output=True, text=True, check=True)
            print("INFO: Sent successfully")
            if '"ok":false' in res.stdout:
                print(f"ERROR: Send failed {res.stdout}")
        except subprocess.CalledProcessError as e:
            print(f"ERROR: Send failed ({e.returncode}) {e.stderr}")
            if 'release' in valid_uploads:
                send_document(bot_token, channel_id, valid_uploads['release'], caption)

if __name__ == '__main__':
    main()