import cv2
import numpy as np

import torch
import warnings
warnings.filterwarnings('ignore')
device = torch.device('cuda') if torch.cuda.is_available() else torch.device('cpu')
print('Device:', device)


# YOLO 모델로 나온 결과를 통해 이미지 각각을 출력함

from ultralytics import YOLO
yolo_model = YOLO("deepfashion2_yolov8s-seg.pt")

testPath = "./src/bus.jpg"
img = cv2.imread(testPath)
cv2.imshow('src', img)


conf = 0.5
results = yolo_model.predict(img, conf=conf)

print('len:', len(results))
print('type:', type(results))

first = results[0]
print('first len:', len(first))
print('first type:', type(first))
print()
print()

cnt=1
for res in results:
    for result in res:
        print(f'/////////{cnt}///////////')
        t = int(result.boxes.cls[0].item())
        name = result.names[t]
        print('name:', name)
        # print('boxes:', result.boxes)
        # print('masks:', result.masks)

        bbox = result.boxes.xyxy
        if bbox.is_cuda:
            bbox = bbox.cpu()
        bbox = bbox[0].numpy()

        x1, y1, x2, y2 = bbox
        cloth = img[int(y1):int(y2), int(x1):int(x2)]

        x_ratio = x2 - x1
        y_ratio = y2 - y1
        masks = result.masks.xyn # 마스크는 numpy 배열로 주어짐
        for mask in masks:
            x_coords = (mask[:, 0] * x_ratio).astype(np.int32)
            y_coords = (mask[:, 1] * y_ratio).astype(np.int32)
            masked = cloth.copy()

            for x, y in zip(x_coords, y_coords):
                masked[y, x] = [255, 255, 255]

        cv2.imshow(f'Image{cnt} - {name}', masked)

        print()
        cnt+=1


cv2.waitKey()
cv2.destroyAllWindows()