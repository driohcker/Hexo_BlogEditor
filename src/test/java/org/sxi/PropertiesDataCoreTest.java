package org.sxi;

import org.junit.jupiter.api.Test;
import org.sxi.dao.PropertiesDataCore;

import java.util.Map;

/**
 * 配置参数数据控制类测试
 */

public class PropertiesDataCoreTest {

    @Test
    public void test01() {
        System.out.println("=== PropertiesDataCore 配置类测试 ===");
        
        try {
            // 测试获取单个配置项
            System.out.println("1. 测试获取单个配置项...");
            String postRootPath = PropertiesDataCore.getStringProperty("post.root.path");
            System.out.println("post.root.path = " + postRootPath);
            
            // 测试获取不存在的配置项
            System.out.println("\n2. 测试获取不存在的配置项...");
            String nonExistent = PropertiesDataCore.getStringProperty("non.existent.key");
            System.out.println("non.existent.key = " + nonExistent);
            
            // 测试检查配置项是否存在
            System.out.println("\n3. 测试检查配置项是否存在...");
            boolean exists = PropertiesDataCore.containsKey("post.root.path");
            System.out.println("post.root.path 存在: " + exists);
            
            // 测试获取所有配置项
            System.out.println("\n4. 测试获取所有配置项...");
            Map<String, Object> allProperties = PropertiesDataCore.getAllProperties();
            System.out.println("配置项数量: " + allProperties.size());
            System.out.println("所有配置项:");
            for (Map.Entry<String, Object> entry : allProperties.entrySet()) {
                System.out.println("  - " + entry.getKey() + " = " + entry.getValue());
            }
            
            System.out.println("\n=== 测试完成 ===");
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}