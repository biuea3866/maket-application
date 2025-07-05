-- 판매자 테이블
CREATE TABLE sellers (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         seller_id VARCHAR(50) UNIQUE NOT NULL,
                         kakao_account_id VARCHAR(100) UNIQUE,
                         business_name VARCHAR(200) NOT NULL,
                         business_number VARCHAR(20) NOT NULL,
                         representative_name VARCHAR(100) NOT NULL,
                         email VARCHAR(200) NOT NULL,
                         phone VARCHAR(20) NOT NULL,
                         status ENUM('PENDING', 'APPROVED', 'REJECTED', 'SUSPENDED') DEFAULT 'PENDING',
                         access_token VARCHAR(500),
                         refresh_token VARCHAR(500),
                         api_key VARCHAR(255) NOT NULL,
                         api_secret VARCHAR(255) NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 상품 카테고리 테이블
CREATE TABLE categories (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            category_id VARCHAR(50) UNIQUE NOT NULL,
                            name VARCHAR(200) NOT NULL,
                            parent_id VARCHAR(50),
                            level INT NOT NULL,
                            display_order INT DEFAULT 0,
                            is_active BOOLEAN DEFAULT TRUE,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 상품 테이블
CREATE TABLE products (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          product_id VARCHAR(50) UNIQUE NOT NULL,
                          seller_id VARCHAR(50) NOT NULL,
                          name VARCHAR(500) NOT NULL,
                          category_id VARCHAR(50) NOT NULL,
                          brand VARCHAR(200),
                          price DECIMAL(12, 0) NOT NULL,
                          sale_price DECIMAL(12, 0),
                          stock_quantity INT DEFAULT 0,
                          status ENUM('SALE', 'SUSPEND', 'OUTOFSTOCK', 'CLOSE') DEFAULT 'SALE',
                          description TEXT,
                          detail_content TEXT,
                          main_image VARCHAR(500),
                          is_kakao_pay BOOLEAN DEFAULT TRUE,
                          shipping_type ENUM('FREE', 'CONDITIONAL_FREE', 'PAID') DEFAULT 'PAID',
                          shipping_fee DECIMAL(10, 0) DEFAULT 3000,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (seller_id) REFERENCES sellers(seller_id),
                          INDEX idx_kakao_products_seller (seller_id),
                          INDEX idx_kakao_products_category (category_id),
                          INDEX idx_kakao_products_status (status)
);

-- 상품 이미지 테이블
CREATE TABLE product_images (
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                product_id VARCHAR(50) NOT NULL,
                                image_url VARCHAR(500) NOT NULL,
                                image_order INT DEFAULT 0,
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

-- 상품 옵션 테이블
CREATE TABLE product_options (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 option_id VARCHAR(50) UNIQUE NOT NULL,
                                 product_id VARCHAR(50) NOT NULL,
                                 option_name VARCHAR(200) NOT NULL,
                                 option_value VARCHAR(200) NOT NULL,
                                 additional_price DECIMAL(10, 0) DEFAULT 0,
                                 stock_quantity INT DEFAULT 0,
                                 use_yn BOOLEAN DEFAULT TRUE,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

-- 주문 테이블
CREATE TABLE orders (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        order_id VARCHAR(50) UNIQUE NOT NULL,
                        seller_id VARCHAR(50) NOT NULL,
                        kakao_order_id VARCHAR(100),
                        order_date DATETIME NOT NULL,
                        buyer_name VARCHAR(100) NOT NULL,
                        buyer_email VARCHAR(200),
                        buyer_phone VARCHAR(20) NOT NULL,
                        buyer_kakao_id VARCHAR(100),
                        receiver_name VARCHAR(100) NOT NULL,
                        receiver_phone VARCHAR(20) NOT NULL,
                        receiver_zipcode VARCHAR(10) NOT NULL,
                        receiver_address VARCHAR(500) NOT NULL,
                        receiver_address_detail VARCHAR(200),
                        payment_method ENUM('KAKAO_PAY', 'CARD', 'BANK_TRANSFER', 'PHONE') DEFAULT 'KAKAO_PAY',
                        total_amount DECIMAL(12, 0) NOT NULL,
                        delivery_message VARCHAR(500),
                        order_status ENUM('PAYMENT_WAITING', 'PAYED', 'DELIVERING', 'DELIVERED', 'PURCHASE_DECIDED', 'EXCHANGED', 'CANCELED', 'RETURNED') DEFAULT 'PAYMENT_WAITING',
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (seller_id) REFERENCES sellers(seller_id),
                        INDEX idx_kakao_orders_seller (seller_id),
                        INDEX idx_kakao_orders_status (order_status),
                        INDEX idx_kakao_orders_date (order_date)
);

-- 주문 상품 테이블
CREATE TABLE order_items (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             order_id VARCHAR(50) NOT NULL,
                             product_id VARCHAR(50) NOT NULL,
                             option_id VARCHAR(50),
                             quantity INT NOT NULL,
                             price DECIMAL(10, 0) NOT NULL,
                             status ENUM('PAYMENT_WAITING', 'PAYED', 'DELIVERING', 'DELIVERED', 'PURCHASE_DECIDED', 'EXCHANGED', 'CANCELED', 'RETURNED') DEFAULT 'PAYMENT_WAITING',
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
                             FOREIGN KEY (product_id) REFERENCES products(product_id),
                             FOREIGN KEY (option_id) REFERENCES product_options(option_id)
);

-- 장바구니 테이블 (카카오 특화)
CREATE TABLE carts (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       cart_id VARCHAR(50) UNIQUE NOT NULL,
                       buyer_kakao_id VARCHAR(100),
                       product_id VARCHAR(50) NOT NULL,
                       option_id VARCHAR(50),
                       quantity INT NOT NULL,
                       is_selected BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       FOREIGN KEY (product_id) REFERENCES products(product_id),
                       FOREIGN KEY (option_id) REFERENCES product_options(option_id),
                       INDEX idx_kakao_cart_buyer (buyer_kakao_id)
);

-- 찜 목록 테이블 (카카오 특화)
CREATE TABLE wishlists (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           buyer_kakao_id VARCHAR(100) NOT NULL,
                           product_id VARCHAR(50) NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           UNIQUE KEY unique_wishlist (buyer_kakao_id, product_id),
                           FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- 리뷰 테이블 (카카오 특화)
CREATE TABLE reviews (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         review_id VARCHAR(50) UNIQUE NOT NULL,
                         product_id VARCHAR(50) NOT NULL,
                         order_id VARCHAR(50) NOT NULL,
                         buyer_kakao_id VARCHAR(100) NOT NULL,
                         buyer_name VARCHAR(100) NOT NULL,
                         rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                         content TEXT,
                         is_photo_review BOOLEAN DEFAULT FALSE,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (product_id) REFERENCES products(product_id),
                         FOREIGN KEY (order_id) REFERENCES orders(order_id),
                         INDEX idx_kakao_review_product (product_id),
                         INDEX idx_kakao_review_rating (rating)
);

-- 리뷰 이미지 테이블
CREATE TABLE review_images (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               review_id VARCHAR(50) NOT NULL,
                               image_url VARCHAR(500) NOT NULL,
                               image_order INT DEFAULT 0,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (review_id) REFERENCES reviews(review_id) ON DELETE CASCADE
);

-- 정산 테이블
CREATE TABLE settlements (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             settlement_id VARCHAR(50) UNIQUE NOT NULL,
                             seller_id VARCHAR(50) NOT NULL,
                             settlement_date DATE NOT NULL,
                             start_date DATE NOT NULL,
                             end_date DATE NOT NULL,
                             total_sales_amount DECIMAL(12, 0) NOT NULL,
                             kakao_pay_fee DECIMAL(10, 0) DEFAULT 0,
                             commission_amount DECIMAL(10, 0) NOT NULL,
                             settlement_amount DECIMAL(12, 0) NOT NULL,
                             status ENUM('PENDING', 'COMPLETED', 'HOLD') DEFAULT 'PENDING',
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (seller_id) REFERENCES sellers(seller_id)
);

-- 알림톡 발송 이력 테이블 (카카오 특화)
CREATE TABLE kakao_notifications (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     notification_id VARCHAR(50) UNIQUE NOT NULL,
                                     order_id VARCHAR(50),
                                     receiver_phone VARCHAR(20) NOT NULL,
                                     template_code VARCHAR(50) NOT NULL,
                                     message_content TEXT NOT NULL,
                                     send_status ENUM('PENDING', 'SUCCESS', 'FAILED') DEFAULT 'PENDING',
                                     sent_at TIMESTAMP NULL,
                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (order_id) REFERENCES orders(order_id)
);