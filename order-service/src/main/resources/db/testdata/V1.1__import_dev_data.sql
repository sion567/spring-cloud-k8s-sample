-- 插入一条测试数据，user_id 对应你 JWT 里的 sub
INSERT INTO orders (order_no, user_id, amount, status) VALUES ('ORD2025001', 1, 99.00, 'PAID');
