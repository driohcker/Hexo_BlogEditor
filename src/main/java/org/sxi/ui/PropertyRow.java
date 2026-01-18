package org.sxi.ui;

import org.sxi.vo.Property;

import javax.swing.*;
import java.awt.*;

/**
 * 属性行组件，用于显示和修改属性键值对
 */
public class PropertyRow extends JPanel {

    private static final int ROW_HEIGHT = 80;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 35;

    private String key;
    private String value;

    private Property property;

    public PropertyRow(Property property) {
        this.property = property;
        this.key = property.getName();
        this.value = property.getValue();
        initComponents();
        initEvents();
        initData();
    }

    public PropertyRow() {}

    /**
     * 初始化组件
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        setPreferredSize(new Dimension(0, ROW_HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));

        // 创建键值对面板
        JPanel kvPanel = new JPanel();
        kvPanel.setLayout(new BoxLayout(kvPanel, BoxLayout.Y_AXIS));
        kvPanel.setBackground(Color.WHITE);
        kvPanel.setOpaque(true);

        // 键
        JLabel keyLabel = new JLabel(key);
        keyLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        keyLabel.setForeground(new Color(50, 50, 50));

        // 值
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        valueLabel.setForeground(new Color(100, 100, 100));

        // 添加到键值面板
        kvPanel.add(keyLabel);
        kvPanel.add(Box.createVerticalStrut(4));
        kvPanel.add(valueLabel);

        // 修改按钮
        JButton editButton = new JButton("修改");
        editButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        editButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        editButton.setBackground(new Color(240, 240, 240));
        editButton.setForeground(new Color(50, 50, 50));
        editButton.setBorder(BorderFactory.createEmptyBorder());
        editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 添加悬停效果
        editButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                editButton.setBackground(new Color(220, 220, 220));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                editButton.setBackground(new Color(240, 240, 240));
            }
        });

        // 添加点击事件
        editButton.addActionListener(e -> {
            String newValue = JOptionPane.showInputDialog(
                    PropertyRow.this,
                    "修改属性值:",
                    value
            );
            if (newValue != null && !newValue.trim().isEmpty()) {
                value = newValue;
                valueLabel.setText(value);
                // 更新Property对象的值
                if (property != null) {
                    property.setValue(value);
                }
            }
        });

        // 添加到主面板
        add(kvPanel, BorderLayout.CENTER);
        add(editButton, BorderLayout.EAST);
    }

    private void initEvents() {

    }

    private void initData() {

    }
    
    /**
     * 获取Property对象
     * @return Property对象
     */
    public Property getProperty() {
        return property;
    }
}