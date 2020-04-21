package com.hujinwen.download;


import com.hujinwen.download.core.DownloadWorker;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.MessageFormat;

public class HelloWorldSwing {

    private static final MessageFormat LOG_FORMAT = new MessageFormat("Progress -> {0}%, Speed -> {1}kb/s\n");

    public static void main(String[] args) {
        // 创建 JFrame 实例
        JFrame frame = new JFrame("JDer");
        // Setting the width and height of frame
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);
        /*
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel);

        // 设置界面可见
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {

        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);

        // 创建 JLabel
        JLabel label1 = new JLabel("下载链接：");
        label1.setBounds(10, 20, 80, 25);
        panel.add(label1);

        JTextField urlLink = new JTextField(20);
        urlLink.setBounds(100, 20, 450, 25);
        panel.add(urlLink);

        // 下载位置
        JLabel label2 = new JLabel("下载位置：");
        label2.setBounds(10, 50, 80, 25);
        panel.add(label2);

        JTextField pathText = new JTextField(20);
        pathText.setBounds(100, 50, 450, 25);
        panel.add(pathText);

        // 线程数
        JLabel label3 = new JLabel("最大线程数：");
        label3.setBounds(10, 80, 110, 25);
        panel.add(label3);

        JTextField threadNumText = new JTextField(20);
        threadNumText.setBounds(100, 80, 25, 25);
        threadNumText.setText("1");
        panel.add(threadNumText);

        // 创建登录按钮
        JButton loginButton = new JButton("download");
        loginButton.setBounds(10, 110, 150, 25);
        panel.add(loginButton);

        // 创建日志打印域
        final JTextArea textArea = new JTextArea(5, 10);
        // 设置垂直滚动条
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        // 设置滚动条跟随
        int height = 10;
        Point p = new Point();
        p.setLocation(0, textArea.getLineCount() * height);
        scrollPane.getViewport().setViewPosition(p);

        textArea.setLineWrap(true);
        textArea.setBounds(10, 150, 580, 200);
        panel.add(textArea);

        // 绑定事件
        loginButton.addActionListener(e -> {
            new Thread(() -> {
                final String url = urlLink.getText();
                final String path = pathText.getText();
                final String threadNum = threadNumText.getText();
                try {
                    final DownloadWorker download = DownloadManager.download(url, path, Integer.parseInt(threadNum), null, false);

                    while (!download.isFinish()) {
                        final double progress = download.getProgress();
                        final double speed = download.getSpeed();
                        final String log = LOG_FORMAT.format(new Object[]{progress * 100, speed});
//                        textArea.append(log);
                        textArea.setText(log);
                        Thread.sleep(1000);
                    }
                } catch (IOException | InterruptedException ex) {
                    textArea.setText(ex.getMessage());
                }
            }).start();
        });
    }

}