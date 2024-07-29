-- 테스트용 sql 파일

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP,
    nick_name VARCHAR(255),
    password VARCHAR(255),
    personal_color VARCHAR(255),
    updated_at TIMESTAMP,
    user_email VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS boards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP,
    like_count BIGINT,
    text VARCHAR(255),
    title VARCHAR(255),
    unlike_count BIGINT,
    updated_at TIMESTAMP,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    path VARCHAR(500) NOT NULL,
    url VARCHAR(500) NOT NULL,
    board BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (board) REFERENCES boards(id)
);
