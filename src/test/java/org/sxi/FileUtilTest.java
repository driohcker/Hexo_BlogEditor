package org.sxi;

import org.junit.jupiter.api.Test;
import org.sxi.util.FileUtil;

public class FileUtilTest {

    @Test
    public void test01(){
        System.out.println("=== FileUtil 工具类测试 ===");

        // 创建FileUtil实例
        FileUtil fileUtil = new FileUtil();

        // 测试创建文件
        System.out.println("\n1. 测试创建文件...");
        //fileUtil.createFile("test.txt");

        // 测试写入文件
        System.out.println("\n2. 测试写入文件...");
        //fileUtil.writeFile("test.txt", "这是测试文件内容\n第二行内容\n中文测试");

        // 测试读取文件
        System.out.println("\n3. 测试读取文件...");
        //String content = fileUtil.readFile("test.txt");
        //System.out.println("文件内容：\n" + content);

        // 测试列出文件
        System.out.println("\n4. 测试列出文件...");
        //fileUtil.listFiles();

        // 测试删除文件
        System.out.println("\n5. 测试删除文件...");
        //fileUtil.deleteFile("test.txt");

        // 再次列出文件
        System.out.println("\n6. 再次列出文件...");
        //fileUtil.listFiles();

        System.out.println("\n=== 测试完成 ===");
    }
}
