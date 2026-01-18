package org.sxi.dao;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 配置参数数据控制类，负责读取配置文件的参数并根据其键值对关系放进集合
 */
public class PropertiesDataCore {

    private static Map<String, Object> propertiesDataCore = new HashMap<>();
    // 配置文件路径 - 使用user.home目录下的配置文件
    private static final String CONFIG_FILE_NAME = "blogeditor.properties"; 
    private static final String CONFIG_FILE_PATH = System.getProperty("user.home") + File.separator + CONFIG_FILE_NAME;

    static {
        initPropertiesDataCore();
    }

    /**
     * 初始化配置数据，从配置文件中读取所有参数并放入集合
     */
    private static void initPropertiesDataCore() {
        Properties properties = new Properties();
        File configFile = new File(CONFIG_FILE_PATH);
        
        try {
            // 检查用户目录下是否存在配置文件
            if (configFile.exists()) {
                // 从用户目录加载配置
                try (InputStream input = new FileInputStream(configFile)) {
                    properties.load(input);
                    System.out.println("从用户目录加载配置文件成功: " + CONFIG_FILE_PATH);
                }
            } else {
                // 如果用户目录下没有配置文件，则从默认位置加载
                try (InputStream input = PropertiesDataCore.class.getClassLoader().getResourceAsStream("file.properties")) {
                    if (input == null) {
                        System.err.println("无法找到默认配置文件: file.properties");
                        return;
                    }
                    properties.load(input);
                    System.out.println("从默认位置加载配置文件成功");
                    
                    // 将默认配置保存到用户目录
                    saveProperties(properties);
                    System.out.println("默认配置已保存到用户目录: " + CONFIG_FILE_PATH);
                }
            }
            
            // 将配置项添加到集合中
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                propertiesDataCore.put(key, value);
                System.out.println(key + ": " + value);
            }
            
            System.out.println("配置文件加载成功，共读取 " + propertiesDataCore.size() + " 个配置项");
            
        } catch (IOException e) {
            System.err.println("加载配置文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 根据键获取配置值
     * @param key 配置键
     * @return 配置值
     */
    public static Object getProperty(String key) {
        return propertiesDataCore.get(key);
    }
    
    /**
     * 根据键获取配置值（字符串类型）
     * @param key 配置键
     * @return 字符串类型的配置值
     */
    public static String getStringProperty(String key) {
        Object value = propertiesDataCore.get(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * 获取所有配置项
     * @return 所有配置项的键值对集合
     */
    public static Map<String, Object> getAllProperties() {
        return new HashMap<>(propertiesDataCore);
    }
    
    /**
     * 检查配置项是否存在
     * @param key 配置键
     * @return 配置项是否存在
     */
    public static boolean containsKey(String key) {
        return propertiesDataCore.containsKey(key);
    }
    
    /**
     * 保存配置到文件
     * @param properties 要保存的配置
     */
    private static void saveProperties(Properties properties) {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE_PATH)) {
            properties.store(output, "BlogEditor Configuration");
        } catch (IOException e) {
            System.err.println("保存配置文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 更新单个配置项
     * @param key 配置键
     * @param value 配置值
     */
    public static void updateProperty(String key, String value) {
        propertiesDataCore.put(key, value);
        
        // 保存到文件
        Properties properties = new Properties();
        for (Map.Entry<String, Object> entry : propertiesDataCore.entrySet()) {
            properties.put(entry.getKey(), entry.getValue().toString());
        }
        saveProperties(properties);
        System.out.println("配置已更新: " + key + " = " + value);
    }
    
    /**
     * 更新多个配置项
     * @param propertiesMap 配置键值对
     */
    public static void updateProperties(Map<String, String> propertiesMap) {
        propertiesMap.forEach(propertiesDataCore::put);
        
        // 保存到文件
        Properties properties = new Properties();
        for (Map.Entry<String, Object> entry : propertiesDataCore.entrySet()) {
            properties.put(entry.getKey(), entry.getValue().toString());
        }
        saveProperties(properties);
        System.out.println("批量配置已更新: " + propertiesMap.size() + " 项");
    }
}
