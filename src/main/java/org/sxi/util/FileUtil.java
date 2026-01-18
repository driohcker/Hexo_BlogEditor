package org.sxi.util;

import org.sxi.vo.Result;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

/**
 * 文件工具类，用于根据配置文件指定的路径进行文件的增删查改操作
 */
public class FileUtil {

    /**
     * 创建文件
     * @param fileName 文件名
     * @return 创建的文件对象
     */
    public static File createFile(String fileName, String rootPath) {
        try {
            File file = new File(rootPath + File.separator + fileName);
            if (file.createNewFile()) {
                System.out.println("文件创建成功：" + file.getAbsolutePath());
                return file;
            } else {
                System.out.println("文件已存在：" + file.getAbsolutePath());
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("创建文件失败", e);
        }
    }

    /**
     * 写入文件内容
     * @param fileName 文件名
     * @param content 文件内容
     */
    public static void writeFile(String fileName, String content, String rootPath) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(rootPath + File.separator + fileName), StandardCharsets.UTF_8))) {
            writer.write(content);
            System.out.println("文件写入成功：" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("写入文件失败", e);
        }
    }

    /**
     * 读取文件内容
     * @param fileName 文件名
     * @return 文件内容
     */
    public static String readFile(String fileName, String rootPath) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(rootPath + File.separator + fileName), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("读取文件失败", e);
        }
    }

    /**
     * 删除文件
     * @param fileName 文件名
     * @return 是否删除成功
     */
    public static boolean deleteFile(String fileName, String rootPath) {
        File file = new File(rootPath + fileName);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("文件删除成功：" + fileName);
            } else {
                System.out.println("文件删除失败：" + fileName);
            }
            return deleted;
        } else {
            System.out.println("文件不存在：" + fileName);
            return false;
        }
    }

    /**
     * 列出目录下的所有文件
     * @param rootPath 根目录路径
     * @return 文件列表（永远不会返回null）
     */
    public static String[] listFiles(String rootPath) {
        if (rootPath == null || rootPath.trim().isEmpty()) {
            System.out.println("目录路径为空，返回空列表");
            return new String[0];
        }
        
        File rootDir = new File(rootPath);
        if (!rootDir.exists()) {
            System.out.println("目录不存在：" + rootPath + "，返回空列表");
            return new String[0];
        }
        
        if (!rootDir.isDirectory()) {
            System.out.println("路径不是目录：" + rootPath + "，返回空列表");
            return new String[0];
        }
        
        String[] files = rootDir.list();
        if (files == null) {
            System.out.println("获取目录内容失败：" + rootPath + "，返回空列表");
            return new String[0];
        }
        
        String[] markdownFiles = Arrays.stream(files)
                .filter(file -> file.endsWith(".md"))
                .toArray(String[]::new);
        
        System.out.println("目录下markdown文件数量：" + markdownFiles.length);
        for (String file : markdownFiles) {
            System.out.println("- " + file);
        }
        
        return markdownFiles;
    }
}