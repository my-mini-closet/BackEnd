import FashionSetRecommender_1 as Rec1

def RecSys(style, user_data, sysname='default'):
    # user_data 전처리

    # 실제 사용하는 user_data
    # user_df = Rec1.preprocess(user_data)
    # 테스트에서 사용하는 user_data
    user_df = user_data

    # 패션 추천
    recset = {}
    if sysname == 'default':
        reclist = Rec1.Recommender1(style, user_df)
        recset = reclist[0]
    # if sysname == 'BERT':
    #     itemlist = Rec2.Recommender2(style, user_df)
    #     recset = reclist[0]

    print('최종 아이템', recset)
    ids = []
    for cat in recset:
        if cat != '총점수':
            item = recset[cat]
            if 'ID' in item:
                ids.append(item['ID'])
    return ids


if __name__ == '__main__':
    # styleCategory = '페미닌'
    # # dict 형태, 직렬로 들어옴
    # from FashionSetRecommender_1 import *
    # userDataset = StyleDataset(phase='Training', style='소피스트케이티드')
    # user_df = dataset2dataframe_parallel_processing(userDataset)
    # test_user_data = make_attdf(user_df)
    #
    # doc_ids = RecSys(styleCategory, test_user_data)
    #
    # print('카테고리 개수: ', len(doc_ids))
    # print('~~~~ids~~~~')
    # print(doc_ids)

    input_data = [
        {'docId': 1, 'category': '상의', 'subCategory': '블라우스', 'color': '화이트', 'subColor': '블랙', 'material': '우븐'},
        {'docId': 2, 'category': '하의', 'subCategory': '스커트', 'color': '블랙', 'subColor': '화이트', 'material': '데님'},
        {'docId': 3, 'category': '아우터', 'subCategory': '재킷', 'color': '네이비', 'material': '가죽'},
        {'docId': 4, 'category': '원피스', 'subCategory': '미디 원피스', 'color': '레드', 'material': '우븐'}
    ]
    from FashionSetRecommender_1 import preprocess
    df = preprocess(input_data)
    print(df.columns)
    print(df)
    df.to_csv('test.csv', mode='w', encoding='utf-8')