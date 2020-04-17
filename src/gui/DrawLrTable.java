package gui;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.JTableHeader;

import parser.LrTable;
import parser.Table;
import parser.Action;

public class DrawLrTable extends JFrame{
    public Vector<Vector<String>> data = new Vector<Vector<String>>();
    public Vector<String> Title = new Vector<String>();
    private final LrTable lrtable = new LrTable("src/parser/grammar.txt");
    private final Table table = lrtable.getLrTable();
    private final Action actiontable [][] = table.getActionTable();
    
    public DrawLrTable() {
        int state_number = 0;
        setTitle("LR(1)分析表");
        setBounds(200, 0, 1000, 750);
        setVisible(true);
        
        //构建表头
        Title.add("States");
        for(String s : table.getTableHead()) {
            Title.add(s);
        }
        
        //构建表体
        for(Action[] strings : actiontable)
        {
            Vector<String> W = new Vector<String>();
            W.add("I"+state_number);
            for (int i = 0; i < strings.length; i++)
            {
                if(strings[i] != null) 
                    W.add(strings[i].toString());
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
