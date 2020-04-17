package gui;

import parser.Action;
import parser.LrTable;
import parser.Table;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Vector;

public class DrawLrTable extends JFrame{
    public Vector<Vector<String>> data = new Vector<>();
    public Vector<String> Title = new Vector<>();
    private final LrTable lrtable = new LrTable("src/parser/grammar.txt");
    private final Table table = lrtable.getLrTable();
    private final Action actionTable[][] = table.getActionTable();

    public DrawLrTable() {
        int state_number = 0;
        setTitle("LR(1)分析表");
        setBounds(200, 0, 1000, 750);
        setVisible(true);
        
        //构建表头
        Title.add("States");
        Title.addAll(table.getTableHead());
        
        //构建表体
        for(Action[] strings : actionTable)
        {
            Vector<String> W = new Vector<>();
            W.add("I"+state_number);
            for (Action string : strings) {
                if (string != null)
                    W.add(string.toString());
                else
                    W.add("  ");
            }
            data.add(W);
            state_number++;
        }
        
        //制表
        JTable table = new JTable(data, Title);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        add(table, BorderLayout.CENTER);
        table.setRowHeight(15);
        JTableHeader tableHeader = table.getTableHeader();
        add(tableHeader, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);
    }    
}
