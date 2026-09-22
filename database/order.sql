-- Minimal classroom order/payment schema; not an official teacher SQL export.
-- All amounts are integer cents. This phase validates stock but does not reserve/deduct it.
CREATE TABLE IF NOT EXISTS tb_order (
    order_id TEXT PRIMARY KEY,
    shop_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    request_id TEXT NOT NULL,
    sku_id INTEGER NOT NULL,
    sku_name TEXT NOT NULL,
    sku_type INTEGER NOT NULL CHECK(sku_type IN (1,2)),
    quantity INTEGER NOT NULL CHECK(quantity>0),
    unit_price INTEGER NOT NULL CHECK(unit_price>0),
    total_amount INTEGER NOT NULL CHECK(total_amount>0),
    status TEXT NOT NULL CHECK(status IN ('WAIT_PAY','PAID','SENT','COMPLETED')),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at DATETIME,
    sent_at DATETIME,
    completed_at DATETIME,
    UNIQUE(shop_id,user_id,request_id)
);
CREATE TABLE IF NOT EXISTS tb_payment (
    payment_no TEXT PRIMARY KEY,
    order_id TEXT NOT NULL REFERENCES tb_order(order_id),
    request_id TEXT NOT NULL,
    pay_type TEXT NOT NULL CHECK(pay_type IN ('ALIPAY','WECHAT')),
    amount INTEGER NOT NULL CHECK(amount>0),
    mode TEXT NOT NULL DEFAULT 'MOCK' CHECK(mode='MOCK'),
    status TEXT NOT NULL CHECK(status IN ('PENDING','SUCCESS','FAILED')),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(order_id,request_id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_order_pending_payment ON tb_payment(order_id) WHERE status='PENDING';
CREATE TABLE IF NOT EXISTS tb_order_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id TEXT NOT NULL REFERENCES tb_order(order_id),
    event_key TEXT NOT NULL UNIQUE,
    from_status TEXT,
    to_status TEXT NOT NULL,
    detail TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_order_shop ON tb_order(shop_id,user_id);
