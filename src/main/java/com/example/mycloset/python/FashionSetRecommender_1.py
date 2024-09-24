# Data 통계와 Algorithm 기반 추천 시스템
# 전체 데이터셋을 입력해야 함

# ~~~~완성해야 하는 함수 목록~~~~
# 대분류 조합에서 유효한 세트목록을 얻는 get_valid_combi_set 함수
# Beam Search 중 score가 0이 나오는 상황에서 처리하는 _ 함수
# Beam Search 마지막 과정에서 첫 대분류를 잘라내고 남은 id 조합에 따라 중복 제거하는 _ 함수

from mlxtend.frequent_patterns import fpgrowth
from KFDataset import *
import os
import warnings
warnings.filterwarnings("ignore", category=DeprecationWarning)
style = "클래식"
json_file_path = os.path.join(os.path.dirname(__file__), 'COMBI_STATIC.json')
csv_file_path = os.path.join(os.path.dirname(__file__), f'{style}_dataset.csv')
# 대분류 탐색순서(우선도)
CATEGORY_PRIORITY = ['아우터', '상의', '원피스', '하의']

def get_valid_combi_set(style, len=1):
    with open(json_file_path, 'r', encoding='UTF8') as jopen:
        COMBI_STATIC = json.load(jopen)

        # 해당 style에서 많이 조합된 옷 대분류 조합
    combi_static = COMBI_STATIC[style]['combi_static']
    total = COMBI_STATIC[style]['total']

    combi_set = []
    combi_cnt = []
    cnt = 0
    for key, val in combi_static.items():
        combi_set.append(key)
        combi_cnt.append(val)
        cnt += 1
        if cnt >= len:
            break

    return combi_set, combi_cnt, total

def get_init_item(df, user_df, init_CAT, top_n=10):
    top_cat_list = df[f'{init_CAT}_카테고리'].value_counts().head(top_n)
    top_clr_list = df[f'{init_CAT}_색상'].value_counts().head(top_n)
    init_cat_item = None
    init_clr_item = None

    # print(f'페미닌에서 많이 채택된 {init_CAT}_카테고리 아이템')
    # print(top_cat_list)
    # print(f'페미닌에서 많이 채택된 {init_CAT}_색상 아이템')
    # print(top_clr_list)

    for cat in top_cat_list.index:
        match = user_df[user_df[f'{init_CAT}_카테고리'] == cat]
        if not match.empty:
            attr_cols = [col for col in user_df.columns if col.startswith(f'{init_CAT}_')]
            init_cat_item = match.iloc[0][attr_cols].to_dict()
            init_cat_item['ID'] = match.iloc[0]['ID']
            break

    for clr in top_clr_list.index:
        match = user_df[user_df[f'{init_CAT}_색상'] == clr]
        if not match.empty:
            attr_cols = [col for col in user_df.columns if col.startswith(f'{init_CAT}_')]
            init_clr_item = match.iloc[0][attr_cols].to_dict()
            init_clr_item['ID'] = match.iloc[0]['ID']

            if init_cat_item and init_cat_item['ID'] == init_clr_item['ID']:
                continue
            break

    return init_cat_item, init_clr_item

# 카테고리와 색상을 따로 고려하니 안좋은 시작을 하는 경우가 생김
# 시작이 안좋으면 결과도 좋지 않았음.
def get_init_item2(df, user_df, init_CAT, top_n=5):
    fpgrowth_dict = get_fpgrowth_score(df, label1=f'{init_CAT}_카테고리', label2=f'{init_CAT}_색상')
    init_items = []
    user_df = pd.DataFrame(user_df)
    for _, row in user_df.iterrows():
        cat_value = row[f'{init_CAT}_카테고리']
        color_value = row[f'{init_CAT}_색상']

        pair = (f'{init_CAT}_카테고리_{cat_value}', f'{init_CAT}_색상_{color_value}')

        if pair in fpgrowth_dict:
            score = fpgrowth_dict[pair]
            item = {col: row[col] for col in user_df.columns if col.startswith(f'{init_CAT}_')}

            item['ID'] = row['ID']
            item['초기점수'] = score
            init_items.append(item)
    init_items_sorted = sorted(init_items, key=lambda x: x['초기점수'], reverse=True)
    # print(init_items_sorted)
    return init_items_sorted[:top_n]

def sort_by_priority(itemset):
    return tuple(sorted(itemset, key=lambda x: CATEGORY_PRIORITY.index(x.split('_')[0])))

# fpgrowth 계산해서 dict 리턴
def get_fpgrowth_score(df, label1='', label2=''):
    df_upper_lower = pd.get_dummies(df[[f'{label1}', f'{label2}']])
    itemsets = fpgrowth(df_upper_lower, min_support=0.01, use_colnames=True)
    f_itemsets = itemsets[itemsets['itemsets'].apply(lambda  x: len(x) > 1)]
    f_itemsets = f_itemsets.sort_values(by='support', ascending=False)
    f_itemsets['itemsets'] = f_itemsets['itemsets'].apply(lambda x: sort_by_priority(x))

    itemset_counts = df_upper_lower.sum(axis=0)
    # support에 count를 결합시켜 점수변환
    def cal_score(row):
        itemset = row['itemsets']
        count = sum([itemset_counts[item] for item in itemset])
        score = row['support'] * count
        return score
    f_itemsets['score'] = f_itemsets.apply(cal_score, axis=1)

    # print('~~fpgrowth_score_itemsets~~')
    # print(f_itemsets, '\n')
    return dict(zip(f_itemsets['itemsets'], f_itemsets['score']))

# 이전 아이템과 fpgrowth dict로 현재 아이템 삽입
def cal_fpgrowth_score(pre_item, fpgrowth_dict, user_df, L1, L2, top_n=3, rev=False):
    top_scores = []
    for _, row in user_df.iterrows():
        if not rev:
            bot_cat = row[L2]
            pair = (f'{L1}_{pre_item[L1]}', f'{L2}_{bot_cat}')
        else:
            bot_cat = row[L1]
            pair = (f'{L1}_{bot_cat}', f'{L2}_{pre_item[L2]}')

        if pair in fpgrowth_dict:
            score = fpgrowth_dict[pair]
            top_scores.append((score, row))

    top_scores_sorted = sorted(top_scores, key=lambda x: x[0], reverse=True)
    print(f'{L1}-{L2}', '관계로 정렬된 top score ', len(top_scores_sorted), '개')
    top_n_items = top_scores_sorted[:top_n]
    # print(top_n_items)
    return top_n_items

# 현재 아이템에서 fpgrowth dict 점수로 카테고리-색상 점수 리턴
def get_fpgrowth_support(cur_item, cur_item_CAT, fpgrowth_dict):
    cur_cat = cur_item[f'{cur_item_CAT}_카테고리']
    cur_clr = cur_item[f'{cur_item_CAT}_색상']
    pair = (f'{cur_item_CAT}_카테고리_{cur_cat}', f'{cur_item_CAT}_색상_{cur_clr}')
    score = fpgrowth_dict.get(pair, None)
    if score is not None:
        return score
    else:
        return 0

def remove_beam_CAT(beams, CAT):
    removed_beams = []
    for beam in beams:
        beam.pop(CAT, None)
        removed_beams.append(beam)
    return removed_beams

def cal_beams(beams, pre_item_CAT, cur_item_CAT, user_df, df, beam_width, rev=False):
    cur_beams = []
    # 이전 우선도 아이템과 현재 우선도 아이템에 대해 fp_growth로 카테고리/색상 점수 추출
    for beam in beams:
        COMP_LABELS = [[f'{pre_item_CAT}_카테고리', f'{cur_item_CAT}_카테고리'], [f'{pre_item_CAT}_색상', f'{cur_item_CAT}_색상']]
        for L1, L2 in COMP_LABELS:
            pre_item = beam[pre_item_CAT]
            if rev:
                L1, L2 = L2, L1
            # 카테고리 비교
            fpgrowth_dict = get_fpgrowth_score(df, label1=L1, label2=L2)
            top_n_items = cal_fpgrowth_score(pre_item=pre_item, fpgrowth_dict=fpgrowth_dict, user_df=user_df, L1=L1, L2=L2, rev=rev)

            # score 처리, item type: DF -> dict 변환
            for score, item in top_n_items:
                cur_beam = beam.copy()
                cur_beam['총점수'] = beam['총점수'] + score
                cur_beam[f'{cur_item_CAT}'] = item.dropna().to_dict()
                cur_beams.append(cur_beam)

    # beam 정렬 - 현재아이템의 카테고리-색상 점수 추가
    for cur_beam in cur_beams:
        cur_item = cur_beam[cur_item_CAT]
        fpgrowth_dict_cur_item = get_fpgrowth_score(df, label1=f'{cur_item_CAT}_카테고리', label2=f'{cur_item_CAT}_색상')
        score = get_fpgrowth_support(cur_item, cur_item_CAT, fpgrowth_dict_cur_item)
        cur_beam['총점수'] = cur_beam['총점수'] + score

    print(f'{len(cur_beams)}개 beam, score순 정렬')
    sorted_cur_beams = sorted(cur_beams, key=lambda x: x['총점수'], reverse=True)
    sorted_cur_beams = sorted_cur_beams[:beam_width]
    return sorted_cur_beams

def beam_search(initial_mix, user_df, df, beam_width=10):
    # 탐색할 대분류를 우선도 순으로 정렬
    sorted_CAT_mix = sorted(initial_mix, key=lambda x: CATEGORY_PRIORITY.index(x))
    first_CAT = sorted_CAT_mix[0]
    CAT_len = len(sorted_CAT_mix)
    print('정렬된 대분류 순서: ', sorted_CAT_mix)

    # 초기 아이템 선택
    beams = []
    # init_cat_item, init_clr_item = get_init_item(df=df, user_df=user_df, init_CAT=first_CAT)
    # if init_cat_item is not None:
    #     init_item_beam1 = {'총점수':0, f'{first_CAT}':init_cat_item} # 카테고리에서 높은점수
    #     beams.append(init_item_beam1)
    # if init_clr_item is not None:
    #     init_item_beam2 = {'총점수':0, f'{first_CAT}':init_clr_item} # 색상에서 높은점수
    #     beams.append(init_item_beam2)

    init_items = get_init_item2(df=df, user_df=user_df, init_CAT=first_CAT)
    for init_item in init_items:
        init_item_beam = {'총점수': init_item['초기점수'], f'{first_CAT}':init_item}
        # init_item_beam[first_CAT].pop('초기점수', None)
        beams.append(init_item_beam)
    print('선택된 초기 아이템:')
    print(beams)


    for CAT_idx in range(1, CAT_len):
        pre_item_CAT, cur_item_CAT = sorted_CAT_mix[CAT_idx-1], sorted_CAT_mix[CAT_idx]
        print('cur item CAT: ', sorted_CAT_mix[CAT_idx])
        sorted_cur_beams = cal_beams(beams=beams, pre_item_CAT=pre_item_CAT, cur_item_CAT=cur_item_CAT, user_df=user_df, df=df, beam_width=beam_width)
        beams = sorted_cur_beams.copy()

    # # 초기 선택된 아이템 제거 및 재검사
    # # 제거 후 결과가 안좋아서 일단 주석처리...
    # second_CAT = sorted_CAT_mix[1]
    # beams = remove_beam_CAT(beams=beams, CAT=first_CAT)
    # print('first_cat removed')
    # for beam in beams:
    #     print(beam)
    #
    # beams = cal_beams(beams=beams, pre_item_CAT=second_CAT, cur_item_CAT=first_CAT, user_df=user_df, beam_width=beam_width, rev=True)

    print('빔 계산 끝')
    beams = beams[:beam_width]
    # for beam in beams:
    #     print(beam)
    #     print('\n')
    return beams

######### 조합 추천 알고리즘 ###########
# 옷 조합 종류: 상의+하의
# 점수 측정: fp growth로 support 계산, 각각 (상의카테고리-하의카테고리), (상의카테고리-상의색상), (하의카테고리-하의색상), (상의색상-하의색상)
# 아이템: 사용자가 가진 상의와 하의 정보들로, 개발자의 통계와는 별개
#
# 1. 초기값
# 전체 통계에서 많이 나온 상의 카테고리에 가까운 옷과 상의 색상에 가까운 옷 2개 선택
#
# 2. 탐색
# 초기값에서 support 계산 4종류로 가까운 하의 계산, 총 8가지
#
# 2-1
# 8가지 조합에서 중복을 제거하고 점수대로 나열, top N개만 남김
#
# 2-2
# top N개 조합에서 하의를 고정시키고 상의에 대해 support 계산으로 각각 상위 4개 계산, 총 4N개 조합
#
# 3
# 4N개 조합에서 중복을 제거하고 점수로 나열

# 입력 예시
# [{'category': '원피스', 'collar': '셔츠칼라', 'color': '블랙', 'detail': [], 'docId': '1234', 'fit': "타이트", 'image': "./", 'material': '라운드넥', 'path': 'images/3_12', 'print':[], 'shirtSleeve':"반팔", 'sleeve':'미니', 'subCategory'}, ...]
def preprocess(input_data):
    # outer_data = []
    # upper_data = []
    # onepiece_data = []
    # bottom_data = []
    #
    # # 입력 데이터를 순회하며 각 카테고리에 맞춰 매핑
    # for item in input_data:
    #     category = item.get('category')
    #     prefix = f'{category}_'  # 카테고리별 prefix
    #
    #     # 각 속성을 해당 카테고리로 매핑
    #     row = {f'{prefix}{TOKENS2ATT.get(ATT2TOKENS.get(k))}': v for k, v in item.items()}
    #
    #     if category == '아우터':
    #         outer_data.append(row)
    #     elif category == '상의':
    #         upper_data.append(row)
    #     elif category == '원피스':
    #         onepiece_data.append(row)
    #     elif category == '하의':
    #         bottom_data.append(row)
    #
    # # 각각의 카테고리별 데이터프레임 생성
    # df_outer = pd.DataFrame(outer_data)
    # df_upper = pd.DataFrame(upper_data)
    # df_onepiece = pd.DataFrame(onepiece_data)
    # df_bottom = pd.DataFrame(bottom_data)
    #
    # # 결합: 모든 카테고리 결합
    # df_combined = pd.concat([df_outer, df_upper, df_onepiece, df_bottom], axis=0, ignore_index=True)
    #
    # # 결합 후 NaN 값 처리
    # df_combined.fillna('', inplace=True)
    #
    # cols = ['ID']
    # for category in ['상의', '하의', '원피스', '아우터']:
    #     cols += [f'{category}_{TOKENS2ATT[token]}' for token in ATT2TOKENS.values() if token != 'category']
    # df_combined = df_combined.reindex(columns=cols)

    att2tokens = {v: k for k, v in TOKENS2ATT.items()}
    tokens2att = {v: k for k, v in ATT2TOKENS.items()}
    # 카테고리별 데이터를 담을 리스트 초기화
    combined_data = []

    # 입력 데이터를 순회하며 각 카테고리에 맞춰 매핑
    for item in input_data:
        row = {'ID': item['docId']}  # ID는 항상 포함

        # 각 속성을 카테고리별로 매핑
        category = item['category']
        for k, v in item.items():
            if k != 'docId' and k != 'category':
                # 매핑된 키를 가져오고, 기본값으로 빈 문자열을 사용
                # token_key = ATT2TOKENS.get(k, '')
                # if token_key:  # 유효한 키인 경우에만 추가
                #     row[f'{category}_{TOKENS2ATT[token_key]}'] = v
                row[f'{category}_{tokens2att[att2tokens[k]]}'] = v

        combined_data.append(row)

    # 데이터프레임 생성
    df_combined = pd.DataFrame(combined_data)

    # 결합 후 NaN 값 처리
    df_combined.fillna('', inplace=True)
    print('before')
    print(df_combined)
    # 최종 열 순서 설정
    cols = ['ID'] + [f'{cat}_{tokens2att[token]}' for cat in ['아우터', '상의', '원피스', '하의'] for token in tokens2att.keys()
                     if token != 'A']
    df_combined = df_combined.reindex(columns=cols)

    return df_combined


def Recommender1(style, udf):
    mixlist, cntlist, total = get_valid_combi_set(style)
    mix = mixlist[0]

    init_mix = [item.strip() for item in mix.split(',')]

    path = f'./{style}_dataset.csv'
    df = pd.read_csv(csv_file_path, encoding='utf-8', low_memory=False)
    # df = make_attdf(df)
    # print(df)

    reclist = beam_search(initial_mix=init_mix, user_df=udf, df=df, beam_width=10)
    return reclist

def make_attdf(user_df):
    # 테스트용 user_df 전처리 -> 하나의 옷 아이템
    upp_cols = [col for col in user_df.columns if col.startswith('상의_')]
    user_df_upp = user_df[upp_cols]
    user_df_upp = user_df_upp.dropna()
    bot_cols = [col for col in user_df.columns if col.startswith('하의_')]
    user_df_bot = user_df[bot_cols]
    user_df_bot = user_df_bot.dropna()
    user_df_processed = pd.concat([user_df_upp, user_df_bot], axis=0, ignore_index=True)
    user_df_processed['ID'] = user_df_processed.index

    # print('user df 전처리 결과')
    # print(f'총 {user_df_processed.shape[0]}개 패션 아이템')
    print(user_df_processed.columns)
    # print('~~상의 카테고리 top3\n', user_df_processed['상의_카테고리'].value_counts().head(3))
    # print('~~상의 색상 top3\n', user_df_processed['상의_색상'].value_counts().head(3))
    # print('~~하의 카테고리 top3\n', user_df_processed['하의_카테고리'].value_counts().head(3))
    # print('~~하의 색상 top3\n', user_df_processed['하의_색상'].value_counts().head(3))
    return user_df_processed


if __name__ == '__main__':
    with open(json_file_path, 'r', encoding='UTF8') as jopen:
        COMBI_STATIC = json.load(jopen)

    # for style in STYLE_CATEGORIES:
    for style in ['페미닌']:
        styleDataset = StyleDataset(phase='Training', style=style)
        df = dataset2dataframe_parallel_processing(styleDataset)

        # 해당 style에서 많이 조합된 옷 대분류 조합
        combi_static = COMBI_STATIC[style]['combi_static']
        total = COMBI_STATIC[style]['total']
        print(f'{style} - 많이 조합된 {total}개 조합')
        print(combi_static)

        userDataset = StyleDataset(phase='Training', style='소피스트케이티드')
        user_df = dataset2dataframe_parallel_processing(userDataset)

        # 테스트용 user_df 전처리 -> 하나의 옷 아이템
        upp_cols = [col for col in user_df.columns if col.startswith('상의_')]
        user_df_upp = user_df[upp_cols]
        user_df_upp = user_df_upp.dropna()
        bot_cols = [col for col in user_df.columns if col.startswith('하의_')]
        user_df_bot = user_df[bot_cols]
        user_df_bot = user_df_bot.dropna()
        user_df_processed= pd.concat([user_df_upp, user_df_bot], axis=0, ignore_index=True)
        user_df_processed['ID'] = user_df_processed.index

        print('user df 전처리 결과')
        print(f'총 {user_df_processed.shape[0]}개 패션 아이템')
        print(user_df_processed.columns)
        print('~~상의 카테고리 top3\n', user_df_processed['상의_카테고리'].value_counts().head(3))
        print('~~상의 색상 top3\n', user_df_processed['상의_색상'].value_counts().head(3))
        print('~~하의 카테고리 top3\n', user_df_processed['하의_카테고리'].value_counts().head(3))
        print('~~하의 색상 top3\n', user_df_processed['하의_색상'].value_counts().head(3))

        # 어쩌구저쩌구해서 일단 상의랑 하의로 결정되었다고 친다
        init_mix = ['하의', '상의']
        # init_mix_set = get_valid_combi_set(style, total, combi_static)
        beam_search(initial_mix=init_mix, user_df=user_df_processed, df=df, beam_width=10)
