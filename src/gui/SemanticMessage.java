package gui;

import parser.Parser;

import javax.swing.*;
import java.awt.*;

public class SemanticMessage {

    public SemanticMessage(String filename) {

        JFrame frame1 = new JFrame();//定义一个新的页面，叫frame
        frame1.setTitle("语义分析");
        frame1.setBounds(0, 0, 1250, 800);
        frame1.setVisible(true);

        Font font = new Font("Consolas", Font.PLAIN, 18);

        TextArea textArea1 = new TextArea();
        textArea1.setFont(font);
        textArea1.setBounds(0, 0, 300, 750);
        frame1.add(textArea1);
        textArea1.setForeground(Color.white);
        textArea1.setBackground(Color.BLACK);


        TextArea textArea2 = new TextArea();
        textArea2.setBounds(300, 0, 300, 750);
        textArea2.setFont(font);
        frame1.add(textArea2);
        textArea2.setForeground(Color.white);
        textArea2.setBackground(Color.BLACK);

        TextArea textArea3 = new TextArea();
        textArea3.setBounds(600, 0, 300, 750);
        textArea3.setFont(font);
        frame1.add(textArea3);
        textArea3.setForeground(Color.white);
        textArea3.setBackground(Color.BLACK);

        TextArea textArea4 = new TextArea();
        textArea4.setBounds(900, 0, 300, 750);
        Font font2 = new Font("宋体", Font.ITALIC, 18);
        textArea4.setFont(font2);
        frame1.add(textArea4);
        textArea4.setForeground(Color.white);
        textArea4.setBackground(Color.BLACK);


        Parser parser = new Parser(filename);
        String symbolTable = parser.getSymbolTable();
        String interCode = parser.getCodeList();
        String errorMessage = parser.getSemanticErrorMessage();
        String quaternions = parser.getQuaternions() ;

        textArea1.append("符号表： \n");
        textArea1.append(symbolTable + "\n");
        textArea2.append("中间代码： \n");
        textArea2.append(interCode + "\n");
        textArea3.append("四元式： \n");
        textArea3.append(quaternions + "\n");

        textArea4.append("错误信息： \n");
        textArea4.append(errorMessage + "\n");

        frame1.add(textArea1);
    }
}
