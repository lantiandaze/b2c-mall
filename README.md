# B2C Mall Backend

基于Java 8、Spring Boot 2.7的多模块商城后端学习项目。本仓库仅包含核心业务源码、Maven配置和空库建表脚本，不包含测试代码、测试数据、实际数据库或密钥。

## 模块

| 模块 | 端口 | 功能 |
| --- | --- | --- |
| common | — | 统一响应、异常、JWT |
| gateway | 8080 | 路由与白名单、认证、日志责任链 |
| shop | 8081 | 新旧注册适配、异步初始化与欢迎消息 |
| product | 8082 | 实物/虚拟商品模板方法、事务日志 |
| order | 8083 | 订单快照、模拟支付策略、LiteFlow与Spring状态机 |
| employee | 8084 | BCrypt、JWT、Redis会话及登录审计 |

订单支持WAIT_PAY → PAID → SENT → COMPLETED。支付和发货为MOCK课堂流程，不接入真实交易/物流；未实现前端、消费者角色授权、库存锁定扣减、退款或自动取消。

## 配置与启动

需要JDK 8、Maven 3.9、Python 3、Redis、Nacos。所有进程从项目根目录启动。

通过终端环境变量或IDE运行配置提供：

- INTERNAL_SERVICE_TOKEN：Shop、Employee和Gateway使用同一个随机密钥。
- JWT_TOKEN_KEY：独立的随机JWT签名密钥。
- MOCK_CALLBACK_KEY：独立的模拟回调密钥。
- NACOS_SERVER_ADDR：默认127.0.0.1:8848；NACOS_NAMESPACE默认b2c，需创建对应命名空间。
- REDIS_HOST/REDIS_PORT：默认127.0.0.1:6379。
- EMPLOYEE_DB_URL、SHOP_DB_URL、PRODUCT_DB_URL、ORDER_DB_URL：可选，默认项目database目录中的SQLite文件。

密钥没有默认值，请独立生成至少32字节随机值并安全保存，不提交到Git。不要把内部密钥当成用户Authorization令牌。

初始化空库及编译：

    python database/init_databases.py
    mvn clean package -DskipTests

按Employee、Shop、Product、Order、Gateway顺序启动各模块Application或target下Jar。初始化SQL不导入账号、订单或样例类目；商品创建前需由运营方配置自己的tb_category记录。

主要接口（通过网关）：

- POST /api/shop/register 或 /api/shop/v2/register
- POST /api/employee/login
- POST /api/product/create
- POST /api/order/create、/api/order/pay
- POST /api/order/mock/callback
- POST /api/order/sent、/api/order/complete

受保护接口使用Authorization请求头。模拟支付响应中的模拟签名仅供本项目回调演示。

## 来源

课堂参考：[liuxinsi/b2c_mall_demo](https://gitee.com/liuxinsi/b2c_mall_demo)。本项目结合课程练习扩展了数据隔离、幂等和事务处理。未附加开源许可证；进一步分发前请核对参考代码许可。
