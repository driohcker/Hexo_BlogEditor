package org.sxi.ui;

import org.sxi.dao.PropertiesDataCore;
import org.sxi.biz.PropertyBiz;
import org.sxi.vo.Property;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 设置页面Fragment，模拟Android的Fragment
 */
public class SettingsFragment extends JPanel {
    // UI常量
    private static final int CARD_WIDTH = 1400;
    private static final int CARD_PADDING = 30;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 35;

    JPanel propertiesPanel;
    
    /**
     * 构造方法
     */
    public SettingsFragment() {
        initComponents();
        initEvents();
        initData();
    }
    
    /**
     * 初始化组件
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // 创建中央内容卡片
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING)
        ));
        cardPanel.setPreferredSize(new Dimension(CARD_WIDTH, 1000));
        
        // 创建标题
        JLabel titleLabel = new JLabel("设置");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 30, 30));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        // 创建属性列表面板
        propertiesPanel = new JPanel();
        propertiesPanel.setLayout(new BoxLayout(propertiesPanel, BoxLayout.Y_AXIS));
        propertiesPanel.setBackground(Color.WHITE);
        propertiesPanel.setAlignmentX(LEFT_ALIGNMENT);

        // 保存按钮
        JButton saveButton = createSaveButton();
        saveButton.setAlignmentX(LEFT_ALIGNMENT);
        
        // 添加组件到卡片面板
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(25));
        cardPanel.add(propertiesPanel);
        cardPanel.add(Box.createVerticalStrut(30));
        cardPanel.add(saveButton);

        // 创建包装面板，使卡片居中（顶部对齐）
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.setBackground(new Color(248, 249, 250));

        wrapperPanel.add(cardPanel);          // 先放内容
        wrapperPanel.add(Box.createVerticalGlue()); // 把多余高度推到下面

        // 再套一层用于水平居中
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.setBackground(new Color(248, 249, 250));
        centerPanel.add(Box.createHorizontalGlue());
        centerPanel.add(wrapperPanel);
        centerPanel.add(Box.createHorizontalGlue());

        add(centerPanel, BorderLayout.CENTER);

    }
    
    /**
     * 初始化事件
     */
    private void initEvents() {
        // 暂无特定事件
    }

    private void initData() {
        // 添加示例属性行（模拟数据）
        //propertiesPanel.add(new PropertyRow("post.root.path", "c:\\Users\\User\\IdeaProjects\\maven\\BlogEditor\\files"));

        PropertiesDataCore.getAllProperties().forEach((k,v) -> {
            propertiesPanel.add(new PropertyRow(new Property(k,(String) v)));
        });
    }
    
    /**
     * 创建保存按钮
     * @return 保存按钮
     */
    private JButton createSaveButton() {
        JButton button = new JButton("保存设置");
        button.setPreferredSize(new Dimension(BUTTON_WIDTH + 20, BUTTON_HEIGHT + 5));
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBackground(new Color(66, 133, 244));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 添加悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 108, 204));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(66, 133, 244));
            }
        });
        
        // 添加点击事件
        button.addActionListener(e -> {
            try {
                // 创建PropertyBiz实例
                PropertyBiz propertyBiz = new PropertyBiz();
                
                // 收集所有属性
                Map<String, String> propertiesMap = new HashMap<>();
                for (Component component : propertiesPanel.getComponents()) {
                    if (component instanceof PropertyRow) {
                        PropertyRow propertyRow = (PropertyRow) component;
                        Property property = propertyRow.getProperty();
                        if (property != null) {
                            propertiesMap.put(property.getName(), property.getValue());
                        }
                    }
                }
                
                // 更新配置
                propertyBiz.changeProperties(propertiesMap);
                
                JOptionPane.showMessageDialog(SettingsFragment.this, 
                        "设置已保存！", "成功", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(SettingsFragment.this, 
                        "保存失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        return button;
    }
    

}