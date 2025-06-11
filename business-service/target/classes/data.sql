USE elm;

-- 添加更多商家数据
INSERT INTO business (businessName, businessAddress, businessExplain, businessImg, orderTypeId, starPrice, deliveryPrice)
VALUES ('北方饺子馆', '沈阳市和平区南京南街155号', '北方特色饺子', '/images/business/north_dumplings.jpg', 1, 20.00, 2.00);

INSERT INTO business (businessName, businessAddress, businessExplain, businessImg, orderTypeId, starPrice, deliveryPrice)
VALUES ('沙县小吃（软件园店）', '沈阳市浑南区软件园C2区', '沙县特色小吃', '/images/business/shaxian.jpg', 7, 12.00, 2.00);

INSERT INTO business (businessName, businessAddress, businessExplain, businessImg, orderTypeId, starPrice, deliveryPrice)
VALUES ('杨铭宇黄焖鸡米饭', '沈阳市浑南区创新路89号', '正宗黄焖鸡', '/images/business/huangmenji.jpg', 6, 18.00, 4.00);

-- 为新商家添加食品
INSERT INTO food (foodName, foodExplain, foodImg, foodPrice, businessId)
VALUES ('酸菜鲜肉水饺', '东北特色酸菜馅', '/images/food/suancai_dumplings.jpg', 18.00, 7);

INSERT INTO food (foodName, foodExplain, foodImg, foodPrice, businessId)
VALUES ('猪肉大葱水饺', '鲜香可口', '/images/food/pork_onion_dumplings.jpg', 16.00, 7);

INSERT INTO food (foodName, foodExplain, foodImg, foodPrice, businessId)
VALUES ('沙县拌面', '特色拌面', '/images/food/shaxian_noodles.jpg', 15.00, 8);

INSERT INTO food (foodName, foodExplain, foodImg, foodPrice, businessId)
VALUES ('沙县小炒肉', '香辣可口', '/images/food/shaxian_pork.jpg', 22.00, 8);

INSERT INTO food (foodName, foodExplain, foodImg, foodPrice, businessId)
VALUES ('黄焖鸡米饭（小）', '经典黄焖鸡', '/images/food/huang_menji_small.jpg', 18.00, 9);

INSERT INTO food (foodName, foodExplain, foodImg, foodPrice, businessId)
VALUES ('黄焖鸡米饭（大）', '大份量更实惠', '/images/food/huang_menji_large.jpg', 25.00, 9); 