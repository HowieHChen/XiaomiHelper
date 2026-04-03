import os
import json
import subprocess
import sys
import html

def format_html_caption(text):
    """
    Converts basic Markdown (like ``` code blocks) into Telegram-supported HTML.
    This completely avoids MarkdownV2 parsing errors which cause the server
    to abruptly disconnect the upload connection, resulting in curl error 26.
    """
    if not text:
        return ""
    parts = text.split('```')
    for i in range(len(parts)):
        if i % 2 == 1:
            # Inside the code block
            parts[i] = f'<pre>{html.escape(parts[i].strip())}</pre>'
        else:
            # Outside the code block
            parts[i] = html.escape(parts[i])
    return "".join(parts)

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
        f'[https://api.telegram.org/bot](https://api.telegram.org/bot){bot_token}/sendDocument',
        '-F', f'chat_id={channel_id}',
        '-F', f'document=@{file_path}',
        '-F', 'parse_mode=HTML'
    ]
    if caption:
        cmd.extend(['-F', f'caption={caption}'])

    try:
        res = subprocess.run(cmd, capture_output=True, text=True, check=True)
        if '"ok":false' in res.stdout:
            print(f"ERROR: Telegram API rejected the request: {res.stdout}")
        else:
            print(f"INFO: Sent successfully: {os.path.basename(file_path)}")
    except subprocess.CalledProcessError as e:
        print(f"ERROR: Send failed (exit code {e.returncode})")
        print(f"ERROR Details: {e.stderr}")

def main():
    bot_token = os.environ.get('BOT_TOKEN')
    channel_id = os.environ.get('CHANNEL_ID')
    raw_caption = os.environ.get('COMMIT_MESSAGE', 'No changelog provided')

    if not bot_token or not channel_id:
        print("ERROR: Invalid BOT_TOKEN or CHANNEL_ID")
        sys.exit(1)

    # Convert the raw markdown to safe HTML to prevent parsing aborts
    safe_caption = format_html_caption(raw_caption)

    max_single_size = 50 * 1024 * 1024  # 50MB
    max_request_size = 49 * 1024 * 1024 # 49MB (leaving room for multipart headers/JSON overhead)

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

    if len(valid_uploads) == 1:
        label = list(valid_uploads.keys())[0]
        send_document(bot_token, channel_id, valid_uploads[label], safe_caption)
    else:
        media = [
            {"type": "document", "media": "attach://release", "parse_mode": "HTML", "caption": safe_caption},
            {"type": "document", "media": "attach://debug"}
        ]
        cmd = [
            'curl', '-s',
            f'[https://api.telegram.org/bot](https://api.telegram.org/bot){bot_token}/sendMediaGroup',
            '-F', f'chat_id={channel_id}',
            '-F', f'media={json.dumps(media)}',
            '-F', f'release=@{valid_uploads["release"]}',
            '-F', f'debug=@{valid_uploads["debug"]}'
        ]
        try:
            res = subprocess.run(cmd, capture_output=True, text=True, check=True)
            if '"ok":false' in res.stdout:
                print(f"ERROR: Telegram API rejected the request: {res.stdout}")
            else:
                print("INFO: MediaGroup sent successfully")
        except subprocess.CalledProcessError as e:
            print(f"ERROR: Send MediaGroup failed (exit code {e.returncode})")
            print(f"ERROR Details: {e.stderr}")

            # Fallback mechanism
            print("INFO: Falling back to single file upload for Release package...")
            if 'release' in valid_uploads:
                send_document(bot_token, channel_id, valid_uploads['release'], safe_caption)

if __name__ == '__main__':
    main()