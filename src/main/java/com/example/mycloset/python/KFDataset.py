from torch.utils.data import Dataset
import concurrent.futures

import glob
import pandas as pd
import json
from tqdm import tqdm

from Final.Labels import *
import config
CONFIG = config.config

# 일단 att기반 스타일 추천
class StyleDataset(Dataset):
    def __init__(self, phase='Validation', mode='label', style='페미닌'):
        print('~~~~Loading Dataset...~~~~')
        print(f'phase: {phase}, mode: {mode}, style: {style}')
        self.root = CONFIG.DATA_ROOT + "/" + phase
        self.mode = mode
        self.style = style
        self.style_list = []
        self.get_anno()
        print('Dataset Loaded!\n')

    def get_anno(self):
        anno_files = list(glob.glob(self.root + f"/라벨링데이터/{self.style}/*.json"))
        print('Found annotation files:', len(anno_files))
        with concurrent.futures.ThreadPoolExecutor() as executor:
            results = list(tqdm(executor.map(self.preprocess, anno_files), total=len(anno_files)))

        if self.mode == 'label':
            for result in results:
                if result:
                   self.style_list.append(result)
        elif self.mode == 'index':
        # ID, processed = result
        # self.style_list.append(processed)
            return
        elif self.mode == 'onehot':
            return
        else:
            return

    def preprocess(self, jfile):
        with open(jfile, "r", encoding="UTF8") as jopen:
            full_data = json.load(jopen)
        extracted = extract_data(full_data)
        if self.mode == 'label':
            return extracted
        elif self.mode == 'index':
            return
        elif self.mode == 'onehot':
            if extracted is not None:
                processed = extract_to_onehot(extracted)
                return extracted["ID"], processed
        else:
            return None

        # return None

    def __len__(self):
        return len(self.style_list)

    def __getitem__(self, idx):
        item = self.style_list[idx]
        return item


def extract_data(one_labels):
    ID = one_labels["이미지 정보"]["이미지 식별자"]
    labeling = one_labels["데이터셋 정보"]["데이터셋 상세설명"]["라벨링"]

    # 스타일과 서브스타일
    overall_style = labeling["스타일"]
    have_substyle = False
    substyle = "None"
    if len(overall_style[0]) == 0:
        style = "기타"
    else:
        for key, val in overall_style[0].items():
            if key == "스타일":
                style = val
            if key == "서브스타일":
                substyle = val
                have_substyle = True

    # 옷 attribute
    clothing_categories = []
    all_attributes = {}
    for cat in ["아우터", "하의", "원피스", "상의"]:
        if len(labeling[cat][0]) != 0:
            clothing_categories.append(cat)
            attributes = labeling[cat][0]
            all_attributes[cat] = attributes


    obj = {
        "ID": ID,  #string
        "style": style,  #string
        "have_substyle": have_substyle,  #bool
        "substyle": substyle,  #string
        "clothing_categories": clothing_categories,  #list[string]
        "attributes": all_attributes  #dict[dict]
    }
    return obj

def extract_to_onehot(extracted_obj):
    return extracted_obj


########################## Pytorch Dataset to pd.DataFrame ###################################

excepted = 0
def dataset2dataframe(Dataset):
    ID = Dataset["ID"]
    style = Dataset["style"]
    clothing_categories = ", ".join(Dataset["clothing_categories"])
    mix = sorted(Dataset["clothing_categories"])
    try:
        mix_clothing_categories = MIX_CLOTHING_CATEGORIES[tuple(mix)]
    except:  # 존재할 수 없는 옷 조합
        global excepted
        excepted += 1
        return None

    attributes = Dataset["attributes"]

    row = {
        "ID": ID,
        "style": style,
        "clothing_categories": clothing_categories,
        "mix_clothing_categories": mix_clothing_categories
    }

    for category in Dataset["clothing_categories"]:
        if category in attributes:
            for attr_key, attr_val in attributes[category].items():
                row[f"{category}_{attr_key}"] = attr_val

    return row

def dataset2dataframe_parallel_processing(Datasets):
    print('~~~~Convert Dataset to DataFrame Format~~~~')

    with concurrent.futures.ProcessPoolExecutor() as executor:
        df_list = list(tqdm(executor.map(dataset2dataframe, Datasets)))
    df_list = [row for row in df_list if row is not None]
    df = pd.DataFrame(df_list)

    print(f'{excepted} excepted')
    print('Convert Done!\n')
    return df

########################## if main -> print example Dataset ###################################

if __name__ == '__main__':
    styleDataset = StyleDataset(phase='Validation', mode='label')
    print(styleDataset[12345])

########################## Dataset 예시 ###################################
# {'ID': 1181022,
# 'style': '기타',
# 'have_substyle': False,
# 'substyle': 'None',
# 'clothing_categories': ['아우터', '하의', '상의'],
# 'attributes': {'아우터': {'색상': '베이지', '소매기장': '긴팔'},
#                '하의': {'색상': '베이지'},
#                '상의': {'색상': '베이지', '소매기장': '긴팔'}}}

# {'ID': 1235581,
#  'style': '리조트',
#  'have_substyle': False,
#  'substyle': 'None',
#  'clothing_categories': ['하의', '상의'],
#  'attributes': {'하의': {'기장': '미니', '색상': '블랙', '서브색상': '화이트', '카테고리': '팬츠', '소재': ['우븐'], '프린트': ['체크'], '핏': '노멀'},
#                 '상의': {'기장': '롱', '색상': '그레이', '카테고리': '티셔츠', '디테일': ['드롭숄더', '슬릿'], '소매기장': '반팔', '소재': ['저지'], '프린트': ['무지'], '넥라인': '유넥', '핏': '루즈'}}}