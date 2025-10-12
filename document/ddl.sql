-- 재고 관리 시스템 DDL
-- 데이터베이스: inventory

-- 1. 공급업체 테이블
CREATE TABLE supplier
(
    supplier_id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                         VARCHAR(100) NOT NULL,
    business_registration_number VARCHAR(20)  NOT NULL,
    postcode                     VARCHAR(10),
    base_address                 VARCHAR(200),
    detail_address               VARCHAR(200),
    ceo_name                     VARCHAR(50),
    manager_name                 VARCHAR(50),
    manager_contact              VARCHAR(20),
    manager_email                VARCHAR(100),
    active                       BOOLEAN DEFAULT TRUE,
    created_at                   DATETIME     NOT NULL,
    modified_at                  DATETIME     NOT NULL,
    deleted                      BOOLEAN DEFAULT FALSE,
    deleted_at                   DATETIME
);

-- 2. 창고 테이블
CREATE TABLE warehouse
(
    warehouse_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    postcode        VARCHAR(10),
    base_address    VARCHAR(200),
    detail_address  VARCHAR(200),
    manager_name    VARCHAR(50),
    manager_contact VARCHAR(20),
    active          BOOLEAN DEFAULT TRUE,
    created_at      DATETIME     NOT NULL,
    modified_at     DATETIME     NOT NULL,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      DATETIME
);

-- 3. 상품 테이블
CREATE TABLE product
(
    product_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id   BIGINT       NOT NULL,
    product_name  VARCHAR(200) NOT NULL,
    product_code  VARCHAR(50)  NOT NULL,
    unit          VARCHAR(20),
    thumbnail_url VARCHAR(500),
    active        BOOLEAN DEFAULT TRUE,
    created_at    DATETIME     NOT NULL,
    modified_at   DATETIME     NOT NULL,
    deleted       BOOLEAN DEFAULT FALSE,
    deleted_at    DATETIME,
    FOREIGN KEY (supplier_id) REFERENCES supplier (supplier_id)
);

-- 4. 입고 테이블
CREATE TABLE inbound
(
    inbound_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id  BIGINT      NOT NULL,
    supplier_id   BIGINT      NOT NULL,
    expected_date DATE,
    status        VARCHAR(20) NOT NULL,
    created_at    DATETIME    NOT NULL,
    modified_at   DATETIME    NOT NULL,
    deleted       BOOLEAN DEFAULT FALSE,
    deleted_at    DATETIME,
    FOREIGN KEY (warehouse_id) REFERENCES warehouse (warehouse_id),
    FOREIGN KEY (supplier_id) REFERENCES supplier (supplier_id)
);

-- 5. 입고 상품 테이블
CREATE TABLE inbound_product
(
    inbound_product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id         BIGINT NOT NULL,
    inbound_id         BIGINT NOT NULL,
    quantity           INT    NOT NULL,
    deleted            BOOLEAN DEFAULT FALSE,
    deleted_at         DATETIME,
    FOREIGN KEY (product_id) REFERENCES product (product_id),
    FOREIGN KEY (inbound_id) REFERENCES inbound (inbound_id)
);

-- 6. 출고 테이블
CREATE TABLE outbound
(
    outbound_id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id            BIGINT      NOT NULL,
    order_number            VARCHAR(50) NOT NULL,
    recipient_name          VARCHAR(50),
    recipient_contact       VARCHAR(20),
    delivery_postcode       VARCHAR(10),
    delivery_base_address   VARCHAR(200),
    delivery_detail_address VARCHAR(200),
    requested_date          DATE,
    expected_date           DATE,
    shipped_date            DATE,
    delivery_memo           TEXT,
    outbound_status         VARCHAR(20) NOT NULL,
    created_at              DATETIME    NOT NULL,
    modified_at             DATETIME    NOT NULL,
    deleted                 BOOLEAN DEFAULT FALSE,
    deleted_at              DATETIME,
    FOREIGN KEY (warehouse_id) REFERENCES warehouse (warehouse_id)
);

-- 7. 출고 상품 테이블
CREATE TABLE outbound_product
(
    outbound_product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    outbound_id         BIGINT NOT NULL,
    product_id          BIGINT NOT NULL,
    requested_quantity  INT    NOT NULL,
    deleted             BOOLEAN DEFAULT FALSE,
    deleted_at          DATETIME,
    FOREIGN KEY (outbound_id) REFERENCES outbound (outbound_id),
    FOREIGN KEY (product_id) REFERENCES product (product_id)
);

-- 8. 창고 재고 테이블
CREATE TABLE warehouse_stock
(
    warehouse_stock_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id       BIGINT   NOT NULL,
    product_id         BIGINT   NOT NULL,
    quantity           INT      NOT NULL DEFAULT 0,
    safety_stock       INT      NOT NULL DEFAULT 0,
    reserved_quantity  INT      NOT NULL DEFAULT 0,
    modified_at        DATETIME NOT NULL,
    UNIQUE KEY uk_warehouse_product (warehouse_id, product_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouse (warehouse_id),
    FOREIGN KEY (product_id) REFERENCES product (product_id)
);

-- 9. 알림 테이블
CREATE TABLE notification
(
    notification_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_name    VARCHAR(50)  NOT NULL,
    recipient_email   VARCHAR(100) NOT NULL,
    notification_type VARCHAR(50)  NOT NULL,
    message           TEXT         NOT NULL,
    send_at           DATETIME     NOT NULL
);


-- 공급업체 테이블 인덱스
CREATE INDEX idx_supplier_name ON supplier (name);
CREATE INDEX idx_supplier_business_registration_number ON supplier (business_registration_number);
CREATE INDEX idx_supplier_active ON supplier (active);
CREATE INDEX idx_supplier_created_at ON supplier (created_at);

-- 창고 테이블 인덱스
CREATE INDEX idx_warehouse_name ON warehouse (name);
CREATE INDEX idx_warehouse_postcode ON warehouse (postcode);
CREATE INDEX idx_warehouse_active ON warehouse (active);
CREATE INDEX idx_warehouse_created_at ON warehouse (created_at);

-- 상품 테이블 인덱스
CREATE INDEX idx_product_supplier_id ON product (supplier_id);
CREATE INDEX idx_product_name ON product (product_name);
CREATE INDEX idx_product_code ON product (product_code);
CREATE INDEX idx_product_active ON product (active);
CREATE INDEX idx_product_created_at ON product (created_at);
CREATE INDEX idx_product_supplier_active ON product (supplier_id, active);

-- 입고 테이블 인덱스
CREATE INDEX idx_inbound_warehouse_id ON inbound (warehouse_id);
CREATE INDEX idx_inbound_supplier_id ON inbound (supplier_id);
CREATE INDEX idx_inbound_status ON inbound (status);
CREATE INDEX idx_inbound_expected_date ON inbound (expected_date);
CREATE INDEX idx_inbound_created_at ON inbound (created_at);
CREATE INDEX idx_inbound_warehouse_status ON inbound (warehouse_id, status);

-- 입고 상품 테이블 인덱스
CREATE INDEX idx_inbound_product_product_id ON inbound_product (product_id);
CREATE INDEX idx_inbound_product_inbound_id ON inbound_product (inbound_id);

-- 출고 테이블 인덱스
CREATE INDEX idx_outbound_warehouse_id ON outbound (warehouse_id);
CREATE INDEX idx_outbound_order_number ON outbound (order_number);
CREATE INDEX idx_outbound_status ON outbound (outbound_status);
CREATE INDEX idx_outbound_requested_date ON outbound (requested_date);
CREATE INDEX idx_outbound_expected_date ON outbound (expected_date);
CREATE INDEX idx_outbound_shipped_date ON outbound (shipped_date);
CREATE INDEX idx_outbound_created_at ON outbound (created_at);
CREATE INDEX idx_outbound_warehouse_status ON outbound (warehouse_id, outbound_status);

-- 출고 상품 테이블 인덱스
CREATE INDEX idx_outbound_product_outbound_id ON outbound_product (outbound_id);
CREATE INDEX idx_outbound_product_product_id ON outbound_product (product_id);

-- 창고 재고 테이블 인덱스
CREATE INDEX idx_warehouse_stock_warehouse_id ON warehouse_stock (warehouse_id);
CREATE INDEX idx_warehouse_stock_product_id ON warehouse_stock (product_id);
CREATE INDEX idx_warehouse_stock_quantity ON warehouse_stock (quantity);
CREATE INDEX idx_warehouse_stock_safety_stock ON warehouse_stock (safety_stock);
CREATE INDEX idx_warehouse_stock_modified_at ON warehouse_stock (modified_at);
CREATE INDEX idx_warehouse_stock_warehouse_quantity ON warehouse_stock (warehouse_id, quantity);

-- 알림 테이블 인덱스
CREATE INDEX idx_notification_recipient_email ON notification (recipient_email);
CREATE INDEX idx_notification_type ON notification (notification_type);
CREATE INDEX idx_notification_send_at ON notification (send_at);
