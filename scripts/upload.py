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

def main():
    # 从环境变量获取配置
    bot_token = os.environ.get('BOT_TOKEN')
    channel_id = os.environ.get('CHANNEL_ID')
    caption = os.environ.get('COMMIT_MESSAGE', 'No changelog provided')

    if not bot_token or not channel_id:
        print("ERROR: Invalid BOT_TOKEN / CHANNEL_ID")
        sys.exit(1)

    max_size = 50 * 1024 * 1024

    paths = {
        'release': get_apk('./app/build/outputs/apk/release'),
        'debug': get_apk('./app/build/outputs/apk/debug')
    }

    valid_uploads = {}
    for label, path in paths.items():
        if path:
            size = os.path.getsize(path)
            if size <= max_size:
                valid_uploads[label] = path
            else:
                print(f"WARNING: APK file for {label.capitalize()} exceeds 50MB ({size/1024/1024:.2f}MB) SKIPPED")
        else:
            print(f"ERROR: APK file for {label} not found")

    if not valid_uploads:
        print("WARNING: No APK matching the criteria can be uploaded.")
        return

    print(f"INFO: Preparing to upload {list(valid_uploads.keys())}")

    if len(valid_uploads) == 1:
        label = list(valid_uploads.keys())[0]
        cmd = [
            'curl', '-s', '-o', '/dev/null', '-w', '%{http_code}',
            f'https://api.telegram.org/bot{bot_token}/sendDocument',
            '-F', f'chat_id={channel_id}',
            '-F', f'document=@{valid_uploads[label]}',
            '-F', 'parse_mode=MarkdownV2',
            '-F', f'caption={caption}'
        ]
        res = subprocess.check_output(cmd).decode()
        print(f"INFO: Result of single file upload: {res}")
    else:
        media = [
            {"type": "document", "media": "attach://release"},
            {"type": "document", "media": "attach://debug", "parse_mode": "MarkdownV2", "caption": caption}
        ]
        cmd = [
            'curl', '-s', '-o', '/dev/null', '-w', '%{http_code}',
            f'https://api.telegram.org/bot{bot_token}/sendMediaGroup',
            '-F', f'chat_id={channel_id}',
            '-F', f'media={json.dumps(media)}',
            '-F', f'release=@{valid_uploads["release"]}',
            '-F', f'debug=@{valid_uploads["debug"]}'
        ]
        res = subprocess.check_output(cmd).decode()
        print(f"INFO: Results of multiple file uploads: {res}")

if __name__ == '__main__':
    main()