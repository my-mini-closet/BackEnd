import os
from rembg import remove
from PIL import Image
from flask import Flask, request, jsonify, send_from_directory

app = Flask(__name__)

# 절대 경로 설정
BASE_DIR = os.path.abspath(os.path.dirname(__file__))
PROCESSED_DIR = os.path.join(BASE_DIR, 'processed_images')

# processed_images 폴더가 없으면 생성
if not os.path.exists(PROCESSED_DIR):
    os.makedirs(PROCESSED_DIR)

@app.route('/process-image', methods=['POST'])
def process_image():
    if 'image' not in request.files:
        return jsonify({'error': 'No image provided'}), 400

    # 이미지 파일을 받음
    file = request.files['image']
    image = Image.open(file)

    # 배경 제거
    output = remove(image)

    # 처리된 이미지 저장
    processed_image_path = os.path.join(PROCESSED_DIR, file.filename)
    
    if output.mode == 'RGBA':
        output = output.convert('RGB')  # JPEG는 RGBA를 지원하지 않으므로 RGB로 변환

    output.save(processed_image_path, format='JPEG')

    # 처리된 파일이 존재하는지 확인
    if not os.path.exists(processed_image_path):
        return jsonify({'error': 'Processed file does not exist'}), 500

    # 처리된 이미지 URL 반환 (여기서 your-server-ip는 실제 Python 서버의 IP 또는 도메인)
    return jsonify({
        'processed_image_url': f'http://54.180.224.157:5000/images/{file.filename}'
    })

# 처리된 이미지를 제공하는 엔드포인트
@app.route('/images/<filename>')
def get_processed_image(filename):
    return send_from_directory(PROCESSED_DIR, filename)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
