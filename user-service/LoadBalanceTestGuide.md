# User-Service 负载均衡测试指南

本指南将帮助您在IntelliJ IDEA中配置和测试多个user-service实例的负载均衡功能。

## 配置多个实例

### 1. 创建多个运行配置

1. 在IDEA右上角，点击运行配置下拉菜单，选择 "Edit Configurations..."
2. 找到现有的 "UserServiceApplication" 配置，复制它两次
3. 分别命名为：
   - UserService-8002（默认端口）
   - UserService-8003（自定义端口8013）
   - UserService-8004（自定义端口8014）

### 2. 配置不同的端口参数

为每个配置添加不同的程序参数：

#### UserService-8002（保持默认配置）
- Program arguments: 留空或使用 `--server.port=8002`

#### UserService-8003
- Program arguments: `--server.port=8013`

#### UserService-8004
- Program arguments: `--server.port=8014`

## 启动服务

1. 确保已启动 Eureka 服务注册中心（eureka-server和eureka-server2）
2. 确保已启动 Gateway 服务网关
3. 依次启动三个 user-service 实例：
   - UserService-8002
   - UserService-8003
   - UserService-8004
4. 在Eureka控制台（http://localhost:8761 或 http://localhost:8762）检查三个实例是否都已注册

## 运行负载均衡测试

使用提供的 `LoadBalancingTest` 类测试负载均衡情况：

1. 在IDEA中打开 `LoadBalancingTest.java` 文件
2. 右键点击文件，选择 "Run LoadBalancingTest.main()"
3. 观察控制台输出，查看请求分布情况

预期结果：在足够多的请求情况下，请求应该大致均匀地分布在三个服务实例之间。

## 手动测试

您也可以通过浏览器或Postman手动访问以下URL来测试：

```
http://localhost:9000/user/instance-info
```

多次刷新页面，观察返回的端口号是否在8002、8013和8014之间变化。

## 注意事项

1. 确保所有服务实例都正确注册到Eureka
2. Gateway的配置中应该使用 `lb://user-service` 作为URI，启用负载均衡
3. 如果遇到问题，请检查日志看是否有注册或连接方面的错误 