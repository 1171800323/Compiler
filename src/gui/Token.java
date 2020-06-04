package gui;

import lexer.Lexer;

import javax.swing.*;
import java.awt.*;

public class Token extends JFrame {

    public Token(String filename){
        setTitle("Token");
        setBounds(200, 0, 400, 650);
        setVisible(true);
        setResizable(false);

        TextArea textToken = new TextArea();
        Font font = new Font("Consolas", Font.PLAIN, 18);
        textToken.setFont(font);
        textToken.setForeground(Color.white);
        textToken.setBackground(Color.BLACK);
        textToken.setBounds(0,0,100,650);
        Lexer lexer = new Lexer(filename);
        for (lexer.Token token : lexer.getTokens()) {
            textToken.append(token.toString()+"\n");
        }

        TextArea textError = new TextArea();
        textError.setFont(font);
        textError.setForeground(Color.red);
        textError.setBackground(Color.BLACK);
        textError.setBounds(200,0,200,650);
        textError.append("Error: \n");
        textError.append(lexer.getErrors());

        this.add(textError);
        this.add(textToken);

    }
}
