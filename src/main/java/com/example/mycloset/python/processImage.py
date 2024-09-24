import os
from rembg import remove
from PIL import Image
from flask import Flask, request, jsonify, send_from_directory
from flask_cors import CORS
import firebase_admin
from firebase_admin import credentials, firestore

from FashionSetRecommender import RecSys

app = Flask(__name__)
CORS(app)  # CORS 허용

# Firebase Admin SDK 초기화
cred = credentials.Certificate('my-mini-closets-firebase-adminsdk-b2zqb-5ee3b962e7.json')  # 서비스 계정 키 경로
firebase_admin.initialize_app(cred)

# Firestore 인스턴스 가져오기
db = firestore.client()

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
    # Firestore 컬렉션에서 userId로 문서 필터링
    try:
        user_docs = db.collection('users').where('userId', '==', userId).stream()
        user_data_list = []
        doc_ids = []

        for doc in user_docs:
            user_data = doc.to_dict()

            # Firestore에서 가져온 데이터와 styleCategory, season 비교
            if user_data.get('weather') == season:
                user_data_list.append(user_data)

        doc_ids = RecSys(styleCategory, user_data_list)

        # 만약 일치하는 docId가 없다면 기본 더미 데이터를 반환
        if not doc_ids:
            return ['1727119999999', '1727118888888']  # 기본 더미 docId

        return doc_ids
    except Exception as e:
        print(f"Error fetching Firestore data: {e}")
        return ['1727119999999', '1727118888888']  # 오류 발생 시 기본 docId 반환

@app.route('/get-doc-ids', methods=['POST'])
def get_doc_ids():
    # 요청 본문에서 데이터를 가져옴
    data = request.get_json()

    if not data or 'userId' not in data or 'styleCategory' not in data or 'season' not in data:
        return jsonify({'error': 'Missing required parameters'}), 400

    userId = data['userId']
    styleCategory = data['styleCategory']
    season = data['season']

    # Firebase에서 데이터를 가져와서 docId 리스트 생성
    doc_ids = generate_doc_ids(userId, styleCategory, season)

    # docId 리스트를 응답으로 반환
    return jsonify({'docIds': doc_ids})

# 처리된 이미지를 제공하는 엔드포인트
@app.route('/images/<filename>')
def get_processed_image(filename):
    return send_from_directory(PROCESSED_DIR, filename)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
