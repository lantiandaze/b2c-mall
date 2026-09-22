-- Classroom implementation reconstructed from partial screenshots, not the teacher's complete SQL.
-- price: integer cents; type: 1 physical, 2 virtual; status: 0 draft, 1 published.
CREATE TABLE IF NOT EXISTS tb_category (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    type INTEGER NOT NULL CHECK(type IN (1,2)),
    shop_id INTEGER,
    status INTEGER NOT NULL DEFAULT 1 CHECK(status IN (0,1))
);
-- Categories are configured by the operator. No sample data is bundled.

CREATE TABLE IF NOT EXISTS tb_sku (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    shop_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL REFERENCES tb_category(id),
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    stock INTEGER NOT NULL CHECK(stock>=0),
    price INTEGER NOT NULL CHECK(price>0),
    status INTEGER NOT NULL DEFAULT 0 CHECK(status IN (0,1)),
    type INTEGER NOT NULL CHECK(type IN (1,2)),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_user_id INTEGER NOT NULL,
    update_user_id INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_shop_id ON tb_sku(shop_id);
CREATE TABLE IF NOT EXISTS tb_sku_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sku_id INTEGER NOT NULL REFERENCES tb_sku(id),
    stock INTEGER NOT NULL,
    price INTEGER NOT NULL,
    status INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_user_id INTEGER NOT NULL,
    update_user_id INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_sku_log_sku_id ON tb_sku_log(sku_id);
