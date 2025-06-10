-- Create database if not exists
CREATE DATABASE IF NOT EXISTS elm DEFAULT CHARSET utf8;

USE elm;

-- Drop tables if they exist
DROP TABLE IF EXISTS order_detail;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS food;
DROP TABLE IF EXISTS delivery_address;
DROP TABLE IF EXISTS business;
DROP TABLE IF EXISTS food_category;
DROP TABLE IF EXISTS user;

-- Create User table
CREATE TABLE user (
    userId INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    userSex VARCHAR(10),
    userImg VARCHAR(255),
    delTag INT DEFAULT 1
);

-- Create FoodCategory table
CREATE TABLE food_category (
    categoryId INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    imgUrl VARCHAR(255) NOT NULL
);

-- Create Business table
CREATE TABLE business (
    businessId INT PRIMARY KEY AUTO_INCREMENT,
    businessName VARCHAR(100) NOT NULL,
    businessAddress VARCHAR(255),
    businessExplain VARCHAR(255),
    businessImg VARCHAR(255),
    orderTypeId INT,
    starPrice DECIMAL(10,2) DEFAULT 0.0,
    deliveryPrice DECIMAL(10,2) DEFAULT 0.0,
    remarks VARCHAR(255)
);

-- Create DeliveryAddress table
CREATE TABLE delivery_address (
    daId INT PRIMARY KEY AUTO_INCREMENT,
    userId INT,
    contactName VARCHAR(100) NOT NULL,
    contactSex VARCHAR(10),
    contactTel VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    FOREIGN KEY (userId) REFERENCES user(userId)
);

-- Create Food table
CREATE TABLE food (
    foodId INT PRIMARY KEY AUTO_INCREMENT,
    foodName VARCHAR(100) NOT NULL,
    foodExplain VARCHAR(255),
    foodImg VARCHAR(255),
    foodPrice DECIMAL(10,2) NOT NULL,
    businessId INT,
    FOREIGN KEY (businessId) REFERENCES business(businessId)
);

-- Create Cart table
CREATE TABLE cart (
    cartId INT PRIMARY KEY AUTO_INCREMENT,
    foodId INT,
    businessId INT,
    userId INT,
    quantity INT DEFAULT 1,
    FOREIGN KEY (foodId) REFERENCES food(foodId),
    FOREIGN KEY (businessId) REFERENCES business(businessId),
    FOREIGN KEY (userId) REFERENCES user(userId)
);

-- Create Order table
CREATE TABLE orders (
    orderId INT PRIMARY KEY AUTO_INCREMENT,
    userId INT,
    businessId INT,
    orderDate VARCHAR(20),
    orderTotal DECIMAL(10,2) DEFAULT 0.0,
    daId INT,
    orderState INT DEFAULT 0, -- 0: Unpaid, 1: Paid, 2: Delivered, 3: Completed
    FOREIGN KEY (userId) REFERENCES user(userId),
    FOREIGN KEY (businessId) REFERENCES business(businessId),
    FOREIGN KEY (daId) REFERENCES delivery_address(daId)
);

-- Create OrderDetail table
CREATE TABLE order_detail (
    odId INT PRIMARY KEY AUTO_INCREMENT,
    orderId INT,
    foodId INT,
    quantity INT DEFAULT 1,
    FOREIGN KEY (orderId) REFERENCES orders(orderId),
    FOREIGN KEY (foodId) REFERENCES food(foodId)
);

-- Insert sample data
-- User
INSERT INTO user (username, password, phone, userSex, userImg)
VALUES ('test', '123456', '13800138000', '男', '');

-- Food Categories
INSERT INTO food_category (name, imgUrl) VALUES ('美食', '/images/food_categories/food.png');
INSERT INTO food_category (name, imgUrl) VALUES ('早餐', '/images/food_categories/breakfast.png');
INSERT INTO food_category (name, imgUrl) VALUES ('跑腿代购', '/images/food_categories/purchase.png');
INSERT INTO food_category (name, imgUrl) VALUES ('汉堡披萨', '/images/food_categories/burger.png');
INSERT INTO food_category (name, imgUrl) VALUES ('甜品饮品', '/images/food_categories/dessert.png');
INSERT INTO food_category (name, imgUrl) VALUES ('速食简餐', '/images/food_categories/fastfood.png');
INSERT INTO food_category (name, imgUrl) VALUES ('地方小吃', '/images/food_categories/localfood.png');
INSERT INTO food_category (name, imgUrl) VALUES ('米粉面馆', '/images/food_categories/noodles.png');
INSERT INTO food_category (name, imgUrl) VALUES ('包子粥铺', '/images/food_categories/buns.png');
INSERT INTO food_category (name, imgUrl) VALUES ('烤鸡炸鸡', '/images/food_categories/chicken.png');

-- Business
INSERT INTO business (businessName, businessAddress, businessExplain, businessImg, orderTypeId, starPrice, deliveryPrice)
VALUES ('万家饺子（软件园E18店）', '沈阳市浑南区软件园E18', '各种饺子类菜', '/images/business/wanjiaJiaozi.jpg', 1, 15.00, 3.00);

INSERT INTO business (businessName, businessAddress, businessExplain, businessImg, orderTypeId, starPrice, deliveryPrice)
VALUES ('小锅炖豆腐馆（全运店）', '沈阳市浑南区全运路126号', '特色套餐', '/images/business/xiaoguo.jpg', 1, 15.00, 3.00);

INSERT INTO business (businessName, businessAddress, businessExplain, businessImg, orderTypeId, starPrice, deliveryPrice)
VALUES ('麦当劳麦乐送（全运路店）', '沈阳市浑南区全运路89号', '汉堡套餐', '/images/business/mcdonald.jpg', 4, 15.00, 3.00);

INSERT INTO business (businessName, businessAddress, businessExplain, businessImg, orderTypeId, starPrice, deliveryPrice)
VALUES ('米村拌饭（浑南店）', '沈阳市浑南区浑南二路18号', '各种拌饭', '/images/business/micun.jpg', 8, 15.00, 3.00);

INSERT INTO business (businessName, businessAddress, businessExplain, businessImg, orderTypeId, starPrice, deliveryPrice)
VALUES ('串记串道（中海康城店）', '沈阳市浑南区康平南街3号', '烤串串', '/images/business/chuanji.jpg', 1, 15.00, 3.00);

INSERT INTO business (businessName, businessAddress, businessExplain, businessImg, orderTypeId, starPrice, deliveryPrice)
VALUES ('半亩田田稻米饭', '沈阳市浑南区创新路195号', '米饭小菜', '/images/business/banmu.jpg', 8, 15.00, 3.00);

-- Food
INSERT INTO food (foodName, foodExplain, foodImg, foodPrice, businessId)
VALUES ('纯肉鲜肉（水饺）', '新鲜猪肉', '/images/food/dumplings1.jpg', 15.00, 1);

INSERT INTO food (foodName, foodExplain, foodImg, foodPrice, businessId)
VALUES ('玉米鲜肉（水饺）', '玉米鲜肉', '/images/food/dumplings2.jpg', 16.00, 1);

INSERT INTO food (foodName, foodExplain, foodImg, foodPrice, businessId)
VALUES ('鲜虾三鲜（蒸饺）', '鲜虾三鲜', '/images/food/dumplings3.jpg', 22.00, 1);

INSERT INTO food (foodName, foodExplain, foodImg, foodPrice, businessId)
VALUES ('素三鲜（蒸饺）', '素三鲜', '/images/food/dumplings4.jpg', 15.00, 1);

-- Delivery Address
INSERT INTO delivery_address (userId, contactName, contactSex, contactTel, address)
VALUES (1, '习近平', '男', '13656765432', '沈阳市浑南区智慧四街1-121号');

INSERT INTO delivery_address (userId, contactName, contactSex, contactTel, address)
VALUES (1, '特朗普', '男', '12345672222', '华盛顿市西城区台宣路34-10-56号');

INSERT INTO delivery_address (userId, contactName, contactSex, contactTel, address)
VALUES (1, '梅超风', '女', '12345673333', '杭州市西湖区白花'); 