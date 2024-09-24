from KFDataset import *

if __name__ == '__main__':
    STYLE_STATIC = {}
    # STYLE_CATEGORIES = ['페미닌']
    for style in STYLE_CATEGORIES:
        print(style, 'Data')
        styleDataset = StyleDataset(phase='Training', style=style)
        # df에는 어차피 해당 스타일만 들어있음
        df = dataset2dataframe_parallel_processing(styleDataset)

        print(df)
        df.to_csv(f'{style}_dataset.csv', mode='w', encoding='UTF8')


# style에 대해서 각 (아/상/원/하)의 attribute 각각에 많이 쓰인 값


# {스타일이름: {len_스타일데이터: num, 아우터: {len_아투터: num, (att로 들어온):{이름:횟수}}, 상의: {카테고리:{이름:횟수}}}}