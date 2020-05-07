package gui;

import parser.Parser;

import javax.swing.*;
import java.awt.*;

public class SemanticMessage extends JFrame {

    public SemanticMessage(String filename) {
        setTitle("语义分析");
        setBounds(200, 0, 500, 750);
        setVisible(true);
        TextArea textArea = new TextArea();
        Font font = new Font("Consolas", Font.PLAIN, 18);
        textArea.setFont(font);
        textArea.setForeground(Color.white);
        textArea.setBackground(Color.BLACK);

        Parser parser = new Parser(filename);
        String symbolTable = parser.getSymbolTable();
        String interCode = parser.getCodeList();
        String errorMessage = parser.getSemanticErrorMessage();

        textArea.append("符号表： \n");
        textArea.append(symbolTable + "\n");
        textArea.append("中间代码： \n");
        textArea.append(interCode + "\n");
        textArea.append("错误信息： \n");
        textArea.append(errorMessage + "\n");

        this.add(textArea);
    }
}
