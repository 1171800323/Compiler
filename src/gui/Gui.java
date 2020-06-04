package gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;


public class Gui {
    private final JFrame jFrame;      // frame窗体
    private final TextArea textArea;  // 文字区

    private final JMenuItem openItem;  // 二级菜单项
    private final JMenuItem saveItem;
    private final JMenuItem exitItem;
    private final JMenuItem dfaItem;
    private final JMenuItem tokenItem;
    private final JMenuItem lrTableItem;
    private final JMenuItem syntax_treeItem;
    private final JMenuItem semanticItem;

    private FileDialog openDialog;
    private File file = null;                       // 打开的文件

    public Gui() {
        jFrame = new JFrame("Compiler");
        jFrame.setSize(800, 800);
        jFrame.setLocationRelativeTo(null); // 居中
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setResizable(false);

        // 设置菜单字体
        // 字体
        Font font1 = new Font("Consolas", Font.PLAIN, 16);
        UIManager.put("Menu.font", font1);
        UIManager.put("MenuItem.font", font1);

        // 创建菜单栏menubar
        // 菜单栏
        JMenuBar menuBar = new JMenuBar();

        // 创建文本域
        textArea = new TextArea();
        Font font2 = new Font("Consolas", Font.PLAIN, 18);
        textArea.setFont(font2);
        textArea.setForeground(Color.white);
        textArea.setBackground(Color.BLACK);

        // 创建菜单项menu
        JMenu fileMenu = new JMenu("File");
        // 一级菜单项
        JMenu lexerMenu = new JMenu("Lexer");
        JMenu parserMenu = new JMenu("Parser");
        JMenu semanticMenu = new JMenu("Semantic");

        // 为"File"菜单项设置"Open","Save","Exit"按钮
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("save");
        exitItem = new JMenuItem("Exit");

        // 为"File"菜单项设置监听
        fileMenuEvent();

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // 为"Lexer"菜单项设置按钮
        dfaItem = new JMenuItem("Dfa");
        tokenItem = new JMenuItem("Token");

        // 为"Lexer"设置监听
        lexerMenuEvent();

        lexerMenu.add(dfaItem);
        lexerMenu.add(tokenItem);


        //为"Parser"菜单设置按钮
        lrTableItem = new JMenuItem("LrTable");
        syntax_treeItem = new JMenuItem("SyntaxTree");

        //为"Parser"设置监听
        parserMenuEvent();
        parserMenu.add(lrTableItem);
        parserMenu.add(syntax_treeItem);

        // 语义分析
        semanticItem = new JMenuItem("Semantic");
        semanticMenuEvent();
        semanticMenu.add(semanticItem);

        // 添加菜单项到menubar
        menuBar.add(fileMenu);
        menuBar.add(lexerMenu);
        menuBar.add(parserMenu);
        menuBar.add(semanticMenu);

        // 添加menubar到frame
        jFrame.setJMenuBar(menuBar);

        // 将文本域添加到frame
        jFrame.add(textArea);

        // frame可见
        jFrame.setVisible(true);
    }

    private void lexerMenuEvent() {
        // 打印输出dfa转换表
        dfaItem.addActionListener(e -> new Dfa());

        // 打印输出token序列
        tokenItem.addActionListener(e -> new Token(file.getAbsolutePath()));
    }

    private void parserMenuEvent() {
        //打印输出LR(1)分析表
        lrTableItem.addActionListener(e -> new DrawLrTable());

        //打印输出语法树
        syntax_treeItem.addActionListener(e -> new DrawSyntaxTree(file.getAbsolutePath()));
    }

    private void semanticMenuEvent() {
        semanticItem.addActionListener(e -> new SemanticMessage(file.getAbsolutePath()));
    }

    private void fileMenuEvent() {
        // 为"Open"设置事件监听
        openItem.addActionListener(e -> {
            //创建一个打开对话框
            openDialog = new FileDialog(jFrame, "Open", FileDialog.LOAD);
            openDialog.setVisible(true);

            // 获取打开文件路径和文件名称并保存到字符串
            String dirPath = openDialog.getDirectory();
            String filename = openDialog.getFile();

            if (dirPath == null || filename == null) {
                return;
            } else {
                textArea.setText(null);
            }

            // 打开文件并读取到textArea
            file = new File(dirPath, filename);
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    textArea.append(line + "\r\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // 为"Save"设置事件监听
        saveItem.addActionListener(e -> {
            if (file != null) {
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                    String text = textArea.getText();
                    bufferedWriter.write(text);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 为"Exit"设置事件监听
        exitItem.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        new Gui();
    }
}