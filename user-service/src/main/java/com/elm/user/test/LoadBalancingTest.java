package com.elm.user.test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 负载均衡测试客户端
 * 用于测试请求是否被均匀分配到各个user-service实例
 */
public class LoadBalancingTest {

    private static final String GATEWAY_URL = "http://localhost:9000/user/instance-info";
    private static final int TEST_COUNT = 100;
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    
    public static void main(String[] args) {
        Map<String, Integer> portDistribution = new HashMap<>();
        
        System.out.println("开始测试负载均衡...");
        System.out.println("发送 " + TEST_COUNT + " 个请求到 " + GATEWAY_URL);
        System.out.println("=============================================");
        
        for (int i = 1; i <= TEST_COUNT; i++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(GATEWAY_URL))
                        .GET()
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                // 解析端口信息 (简单解析，实际项目中可使用JSON库)
                String responseBody = response.body();
                String portInfo = extractPortInfo(responseBody);
                
                // 统计端口分布
                portDistribution.put(portInfo, portDistribution.getOrDefault(portInfo, 0) + 1);
                
                System.out.printf("请求 %3d: 实例端口 = %s\n", i, portInfo);
                
                // 稍微延迟一下，避免请求过快
                TimeUnit.MILLISECONDS.sleep(100);
                
            } catch (Exception e) {
                System.err.println("请求失败: " + e.getMessage());
            }
        }
        
        // 打印统计结果
        System.out.println("\n=============================================");
        System.out.println("负载均衡测试结果:");
        portDistribution.forEach((port, count) -> {
            double percentage = (count * 100.0) / TEST_COUNT;
            System.out.printf("端口 %s: %d 次 (%.1f%%)\n", port, count, percentage);
        });
    }
    
    // 从响应中提取端口信息
    private static String extractPortInfo(String responseBody) {
        // 简单解析，在实际项目中应使用JSON库
        if (responseBody.contains("\"port\":")) {
            int startIndex = responseBody.indexOf("\"port\":") + 7;
            int endIndex = responseBody.indexOf(",", startIndex);
            if (endIndex == -1) {
                endIndex = responseBody.indexOf("}", startIndex);
            }
            if (endIndex != -1) {
                return responseBody.substring(startIndex, endIndex).trim().replace("\"", "");
            }
        }
        return "未知";
    }
} 