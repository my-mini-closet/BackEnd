import os
from rembg import remove
from PIL import Image
from flask import Flask, request, jsonify, send_from_directory
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # CORS 허용

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

    # 배경 제거 (투명 배경으로 만듦)
    output = remove(image)

    # 처리된 이미지 저장 (PNG 형식으로 저장)
    processed_image_path = os.path.join(PROCESSED_DIR, f"{file.filename.split('.')[0]}.png")
    
    # 투명 배경을 유지하기 위해 PNG 형식으로 저장
    output.save(processed_image_path, format='PNG')

    # 처리된 파일이 존재하는지 확인
    if not os.path.exists(processed_image_path):
        return jsonify({'error': 'Processed file does not exist'}), 500

    # 처리된 이미지 URL 반환 (여기서 your-server-ip는 실제 Python 서버의 IP 또는 도메인)
    return jsonify({
        'processed_image_url': f'http://192.168.149.136:5000/images/{os.path.basename(processed_image_path)}'
    })

def generate_doc_ids(userId, styleCategory, season):
    # 딥러닝 모델을 이용해 docId를 예측하는 부분
    # 실제로는 이곳에서 딥러닝 모델이나 알고리즘을 사용해 `docId`를 예측하게 됩니다.
    
    # 예시로 사용자, 스타일, 계절에 맞는 임의의 docId 리스트를 반환
    # 이 부분을 실제 딥러닝 모델 예측으로 대체
    print(f"Received userId: {userId}, styleCategory: {styleCategory}, season: {season}")
    if styleCategory == "클래식" and season == "봄":
        return ['1727118010943', '1727117942456']
    elif styleCategory == "캐주얼" and season == "여름":
        return ['1727112345678', '1727117654321']
    else:
        return ['1727119999999', '1727118888888']  # 기본으로 반환하는 더미 데이터

@app.route('/get-doc-ids', methods=['POST'])
def get_doc_ids():
    # 요청 본문에서 데이터를 가져옴
    data = request.get_json()

    if not data or 'userId' not in data or 'styleCategory' not in data or 'season' not in data:
        return jsonify({'error': 'Missing required parameters'}), 400

    userId = data['userId']
    styleCategory = data['styleCategory']
    season = data['season']

    # 딥러닝 모델 또는 다른 알고리즘을 사용해 docId 리스트를 생성
    doc_ids = generate_doc_ids(userId, styleCategory, season)

    # docId 리스트를 응답으로 반환
    return jsonify({'docIds': doc_ids})

# 처리된 이미지를 제공하는 엔드포인트
@app.route('/images/<filename>')
def get_processed_image(filename):
    return send_from_directory(PROCESSED_DIR, filename)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
