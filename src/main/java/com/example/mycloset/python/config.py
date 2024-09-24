class config:
    DATA_ROOT = '../datasets/K-Fashion'
    CSV_ROOT = './토큰화_KFashionDataset_Training.csv'
    MODEL_ROOT = './models'

    # BERT-base 기준 encoder layer / attention heads / hidden units
    max_len = 64
    hidden_units = 768  # Embedding size
    hidden_feed_forward = hidden_units * 4
    num_heads = 12  # Multi-head layer의 수 (병렬 처리)
    num_layers = 12  # Block의 개수 ( Encoder layer의 개수 )
    dropout_rate = 0.5  # dropout 비율
    lr = 0.001
    batch_size = 128
    num_epochs = 50
    num_workers = 4
    mask_prob = 0.15

    device = 'cpu'

class Args:
    def __init__(self):
        self.train_dataset = './토큰화_KFashionDataset_Training.csv'
        self.test_dataset = './토큰화_KFashionDataset_Test.csv'
        self.user_dataset = './'
        self.vocab_path = './vocab.pkl'
        self.output_path = './models/checkpoints'

        self.vocab_size = None  #207개
        self.encoding = 'utf-8'
        self.min_freq = 1

        # self.hidden = 256
        # self.layers = 8
        # self.attn_heads = 8
        # self.seq_len = 20
        self.seq_len = 64

        # self.batch_size = 64
        self.batch_size = 128
        self.hidden = 240
        self.attn_heads = 12
        self.layers = 12

        self.epochs = 10
        self.num_workers = 12

        self.with_cuda = True
        self.log_freq = 10
        self.corpus_lines = None
        self.on_memory = True  # 메모리에 적재하고 사용 (일부 코드 False 고려 안했음)

        self.lr = 1e-4
        self.adam_weight_decay = 0.001
        self.adam_beta1 = 0.9
        self.adam_beta2 = 0.999

        self.balanced = False