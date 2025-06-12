package com.elm.user.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 高级负载均衡分析工具
 * 用于分析和可视化请求分布情况
 */
public class LoadBalanceAnalyzer {

    private static final String GATEWAY_URL = "http://localhost:9000/user/instance-info";
    private static final int DEFAULT_TEST_COUNT = 100;
    private static final int REPORT_BAR_WIDTH = 50; // 控制台柱状图宽度
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    
    public static void main(String[] args) {
        int testCount = DEFAULT_TEST_COUNT;
        if (args.length > 0) {
            try {
                testCount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("参数必须是整数，使用默认值: " + DEFAULT_TEST_COUNT);
            }
        }
        
        runLoadBalanceTest(testCount);
    }
    
    public static void runLoadBalanceTest(int testCount) {
        Map<String, Integer> portDistribution = new HashMap<>();
        List<RequestResult> allResults = new ArrayList<>();
        Map<String, Long> responseTimeByPort = new HashMap<>();
        
        System.out.println("\n===== 负载均衡测试开始 =====");
        System.out.println("发送 " + testCount + " 个请求到 " + GATEWAY_URL);
        System.out.println("时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=============================\n");
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 1; i <= testCount; i++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(GATEWAY_URL))
                        .GET()
                        .build();
                
                long requestStart = System.currentTimeMillis();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                long requestEnd = System.currentTimeMillis();
                long responseTime = requestEnd - requestStart;
                
                String responseBody = response.body();
                String portInfo = extractPortInfo(responseBody);
                
                // 记录结果
                RequestResult result = new RequestResult(i, portInfo, responseTime, responseBody);
                allResults.add(result);
                
                // 更新端口分布统计
                portDistribution.put(portInfo, portDistribution.getOrDefault(portInfo, 0) + 1);
                
                // 更新响应时间统计
                long totalTime = responseTimeByPort.getOrDefault(portInfo, 0L) + responseTime;
                responseTimeByPort.put(portInfo, totalTime);
                
                System.out.printf("请求 %3d: 实例端口 = %-6s 响应时间 = %4d ms\n", 
                        i, portInfo, responseTime);
                
                // 稍微延迟，避免请求过快
                TimeUnit.MILLISECONDS.sleep(50);
                
            } catch (Exception e) {
                System.err.println("请求 " + i + " 失败: " + e.getMessage());
                RequestResult result = new RequestResult(i, "ERROR", 0, e.getMessage());
                allResults.add(result);
                portDistribution.put("ERROR", portDistribution.getOrDefault("ERROR", 0) + 1);
            }
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        // 生成报告
        printReport(testCount, portDistribution, responseTimeByPort, totalTime);
        
        // 保存结果到文件
        saveReportToFile(testCount, portDistribution, responseTimeByPort, totalTime, allResults);
    }
    
    private static void printReport(int testCount, Map<String, Integer> distribution, 
                                   Map<String, Long> responseTimes, long totalTime) {
        System.out.println("\n===== 负载均衡测试结果 =====");
        System.out.println("总请求数: " + testCount);
        System.out.println("总耗时: " + totalTime + " ms");
        System.out.println("平均每请求耗时: " + (totalTime / testCount) + " ms");
        System.out.println("-----------------------------");
        
        distribution.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByKey())
            .forEach(entry -> {
                String port = entry.getKey();
                int count = entry.getValue();
                double percentage = (count * 100.0) / testCount;
                long totalResponseTime = responseTimes.getOrDefault(port, 0L);
                long avgResponseTime = count > 0 ? totalResponseTime / count : 0;
                
                // 生成柱状图
                int barLength = (int)(percentage * REPORT_BAR_WIDTH / 100);
                String bar = "#".repeat(barLength);
                
                System.out.printf("端口 %-6s: %3d 次 (%5.1f%%) 平均响应: %4d ms %s\n", 
                        port, count, percentage, avgResponseTime, bar);
            });
        
        System.out.println("\n均衡度分析:");
        analyzeDistribution(distribution, testCount);
        System.out.println("=============================");
    }
    
    private static void analyzeDistribution(Map<String, Integer> distribution, int testCount) {
        int validPorts = (int) distribution.keySet().stream()
                .filter(k -> !k.equals("ERROR") && !k.equals("未知"))
                .count();
        
        if (validPorts == 0) {
            System.out.println("没有有效的实例响应请求");
            return;
        }
        
        // 理想情况下每个端口应该处理的请求数
        double idealCount = testCount / (double) validPorts;
        
        // 计算方差
        double sumSquaredDiff = distribution.entrySet().stream()
                .filter(e -> !e.getKey().equals("ERROR") && !e.getKey().equals("未知"))
                .mapToDouble(e -> Math.pow(e.getValue() - idealCount, 2))
                .sum();
        
        double variance = sumSquaredDiff / validPorts;
        double stdDev = Math.sqrt(variance);
        double coefficientOfVariation = stdDev / idealCount * 100;
        
        System.out.printf("标准差: %.2f (理想情况应为 0)\n", stdDev);
        System.out.printf("变异系数: %.2f%% (越低越好，通常低于 10%% 表示分布良好)\n", coefficientOfVariation);
        
        // 给出评价
        if (coefficientOfVariation < 5) {
            System.out.println("评价: 非常均衡 ★★★★★");
        } else if (coefficientOfVariation < 10) {
            System.out.println("评价: 均衡良好 ★★★★☆");
        } else if (coefficientOfVariation < 20) {
            System.out.println("评价: 基本均衡 ★★★☆☆");
        } else if (coefficientOfVariation < 30) {
            System.out.println("评价: 不太均衡 ★★☆☆☆");
        } else {
            System.out.println("评价: 分布不均 ★☆☆☆☆");
        }
    }
    
    private static void saveReportToFile(int testCount, Map<String, Integer> distribution, 
                                       Map<String, Long> responseTimes, long totalTime, 
                                       List<RequestResult> results) {
        try {
            // 创建reports目录（如果不存在）
            Path reportsDir = Paths.get("reports");
            if (!Files.exists(reportsDir)) {
                Files.createDirectories(reportsDir);
            }
            
            // 创建带时间戳的报告文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String reportFile = "reports/load_balance_report_" + timestamp + ".html";
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
                // HTML报告头部
                writer.write("<!DOCTYPE html>\n");
                writer.write("<html>\n<head>\n");
                writer.write("<title>负载均衡测试报告</title>\n");
                writer.write("<style>\n");
                writer.write("body { font-family: Arial, sans-serif; margin: 20px; }\n");
                writer.write("h1, h2 { color: #333; }\n");
                writer.write(".summary { background-color: #f5f5f5; padding: 15px; border-radius: 5px; }\n");
                writer.write(".bar-chart { margin: 20px 0; }\n");
                writer.write(".bar-container { margin: 5px 0; display: flex; align-items: center; }\n");
                writer.write(".bar-label { width: 100px; }\n");
                writer.write(".bar { height: 25px; background-color: #4CAF50; display: inline-block; }\n");
                writer.write(".bar-value { margin-left: 10px; }\n");
                writer.write("table { border-collapse: collapse; width: 100%; }\n");
                writer.write("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
                writer.write("th { background-color: #f2f2f2; }\n");
                writer.write("tr:nth-child(even) { background-color: #f9f9f9; }\n");
                writer.write("</style>\n");
                writer.write("</head>\n<body>\n");
                
                // 报告标题
                writer.write("<h1>负载均衡测试报告</h1>\n");
                writer.write("<p>生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</p>\n");
                
                // 摘要部分
                writer.write("<div class='summary'>\n");
                writer.write("<h2>测试摘要</h2>\n");
                writer.write("<p>请求URL: " + GATEWAY_URL + "</p>\n");
                writer.write("<p>总请求数: " + testCount + "</p>\n");
                writer.write("<p>总耗时: " + totalTime + " ms</p>\n");
                writer.write("<p>平均每请求耗时: " + (totalTime / testCount) + " ms</p>\n");
                writer.write("</div>\n");
                
                // 分布图表
                writer.write("<h2>请求分布</h2>\n");
                writer.write("<div class='bar-chart'>\n");
                
                distribution.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByKey())
                    .forEach(entry -> {
                        String port = entry.getKey();
                        int count = entry.getValue();
                        double percentage = (count * 100.0) / testCount;
                        long totalResponseTime = responseTimes.getOrDefault(port, 0L);
                        long avgResponseTime = count > 0 ? totalResponseTime / count : 0;
                        
                        try {
                            writer.write("<div class='bar-container'>\n");
                            writer.write("  <div class='bar-label'>端口 " + port + ":</div>\n");
                            writer.write("  <div class='bar' style='width: " + percentage + "%;'></div>\n");
                            writer.write("  <div class='bar-value'>" + count + " 次 (" + String.format("%.1f", percentage) + "%) 平均响应: " + avgResponseTime + " ms</div>\n");
                            writer.write("</div>\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                
                writer.write("</div>\n");
                
                // 均衡度分析
                writer.write("<h2>均衡度分析</h2>\n");
                
                int validPorts = (int) distribution.keySet().stream()
                        .filter(k -> !k.equals("ERROR") && !k.equals("未知"))
                        .count();
                
                if (validPorts > 0) {
                    double idealCount = testCount / (double) validPorts;
                    double sumSquaredDiff = distribution.entrySet().stream()
                            .filter(e -> !e.getKey().equals("ERROR") && !e.getKey().equals("未知"))
                            .mapToDouble(e -> Math.pow(e.getValue() - idealCount, 2))
                            .sum();
                    
                    double variance = sumSquaredDiff / validPorts;
                    double stdDev = Math.sqrt(variance);
                    double coefficientOfVariation = stdDev / idealCount * 100;
                    
                    writer.write("<p>有效实例数: " + validPorts + "</p>\n");
                    writer.write("<p>理想请求数/实例: " + String.format("%.2f", idealCount) + "</p>\n");
                    writer.write("<p>标准差: " + String.format("%.2f", stdDev) + "</p>\n");
                    writer.write("<p>变异系数: " + String.format("%.2f%%", coefficientOfVariation) + "</p>\n");
                    
                    // 评价
                    writer.write("<p>评价: ");
                    if (coefficientOfVariation < 5) {
                        writer.write("非常均衡 ★★★★★");
                    } else if (coefficientOfVariation < 10) {
                        writer.write("均衡良好 ★★★★☆");
                    } else if (coefficientOfVariation < 20) {
                        writer.write("基本均衡 ★★★☆☆");
                    } else if (coefficientOfVariation < 30) {
                        writer.write("不太均衡 ★★☆☆☆");
                    } else {
                        writer.write("分布不均 ★☆☆☆☆");
                    }
                    writer.write("</p>\n");
                } else {
                    writer.write("<p>没有有效的实例响应请求</p>\n");
                }
                
                // 请求详情表格
                writer.write("<h2>请求详情</h2>\n");
                writer.write("<table>\n");
                writer.write("  <tr><th>序号</th><th>端口</th><th>响应时间(ms)</th><th>响应内容</th></tr>\n");
                
                for (RequestResult result : results) {
                    writer.write("  <tr>\n");
                    writer.write("    <td>" + result.requestId + "</td>\n");
                    writer.write("    <td>" + result.port + "</td>\n");
                    writer.write("    <td>" + result.responseTime + "</td>\n");
                    writer.write("    <td>" + escapeHtml(result.response) + "</td>\n");
                    writer.write("  </tr>\n");
                }
                
                writer.write("</table>\n");
                
                // HTML报告尾部
                writer.write("</body>\n</html>");
            }
            
            System.out.println("\n报告已保存到: " + reportFile);
            
        } catch (IOException e) {
            System.err.println("保存报告时出错: " + e.getMessage());
        }
    }
    
    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
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
    
    // 请求结果类
    static class RequestResult {
        final int requestId;
        final String port;
        final long responseTime;
        final String response;
        
        RequestResult(int requestId, String port, long responseTime, String response) {
            this.requestId = requestId;
            this.port = port;
            this.responseTime = responseTime;
            this.response = response;
        }
    }
} 