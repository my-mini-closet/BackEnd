import numpy as np
import pandas as pd
import json
import os
import concurrent.futures
from tqdm import tqdm

from KFDataset import *
from Labels import *

fjson_file_path = os.path.join(os.path.dirname(__file__), 'COMBI_STATIC.json')


def tuple2str(t):
    return ', '.join(t)

def check_mix_count(df):
    category_counts = df['mix_clothing_categories'].value_counts()

    REVERSE_MIX_CLOTHING_CATEGORIES = {v: k for k, v in MIX_CLOTHING_CATEGORIES.items()}

    category_names = [tuple2str(REVERSE_MIX_CLOTHING_CATEGORIES[i]) for i in category_counts.index]
    category_counts_named = pd.Series(category_counts.values, index=category_names)

    # print(category_counts_named)
    return category_counts_named

def convert_int64(obj):
    if isinstance(obj, (np.int64, np.float64)):
        return int(obj)
    elif isinstance(obj, dict):
        return {k: convert_int64(v) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [convert_int64(i) for i in obj]
    else:
        return obj

if __name__ == '__main__':
    COMBI_STATIC = {}
    for style in STYLE_CATEGORIES:
        print(style, 'Data')
        styleDataset = StyleDataset(phase='Training', style=style)
        df = dataset2dataframe_parallel_processing(styleDataset)
        print(excepted, 'excepted')

        # 해당 style에서 많이 조합된 옷 대분류 조합
        combi_static = check_mix_count(df)
        total = combi_static.values.sum()

        combi_static_dict = convert_int64(combi_static.to_dict())
        total_converted = convert_int64(total)
        print(f'{style} - 많이 조합된 {total_converted}개 조합')
        print(combi_static_dict)

        COMBI_STATIC[style] = {'style':style, 'total':total_converted, 'combi_static':combi_static_dict}

    with open(fjson_file_path, 'w', encoding='UTF8') as f:
        json.dump(COMBI_STATIC, f, ensure_ascii=False, indent=4)
