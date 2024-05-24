CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS web_authn_credential (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    credential_id VARCHAR(255) NOT NULL,
    public_key VARCHAR(255) NOT NULL,
    counter BIGINT NOT NULL,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);
