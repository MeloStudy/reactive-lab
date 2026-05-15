CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255),
    product_id VARCHAR(255),
    quantity INT,
    total_price DOUBLE,
    status VARCHAR(50),
    created_at TIMESTAMP
);
