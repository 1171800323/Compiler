package gui;

import lexer.Edge;
import lexer.Graph;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;


public class Dfa extends JFrame {
    public Vector<Vector<String>> data = new Vector<>();
    public Vector<String> Title = new Vector<>();
    private final Graph graph = new Graph("src/lexer/dfa.txt");
    private final Set<Integer> State = new TreeSet<>();

    public Dfa() {
        setTitle("DFA转换表");
        setBounds(200, 0, 1000, 750);
        setVisible(true);
//        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Title.add("States");
        int num = 1;
        for (Edge edge : graph.getEdges()) {
            State.add(edge.getSource());
            State.add(edge.getTarget());
            if (!edge.getWeight().contains("other") && !Title.contains(edge.getWeight())) {
                Title.add(edge.getWeight());
                num++;
            }
        }
        Title.add("other");
        for (Integer state : State) {
            Vector<String> W = new Vector<>();

            String[] s = new String[num + 1];
            s[0] = String.valueOf(state);
            for (Edge edge : graph.getEdges()) {
                int locataion;
                if (!edge.getWeight().contains("other")) {
                    locataion = Title.indexOf(edge.getWeight());
                } else {
                    locataion = num;
                }
                if (state == edge.getSource()) {
                    String temp = String.valueOf(edge.getTarget());
                    if (graph.getEndStates().containsKey(edge.getTarget())) {
                        temp += "(" + graph.getEndStates().get(edge.getTarget()) + ")";
                    }
                    s[locataion] = temp;
//                    System.out.println(locataion + " " + s[locataion]);
                } else {
                    if (s[locataion] == null) {
                        s[locataion] = " ";
                    }
                }
            }
            if (graph.getEndStates().containsKey(state)) {
                s[0] += '*';
            }
            W.addAll(Arrays.asList(s).subList(0, num + 1));
            data.add(W);
        }
        JTable table = new JTable(data, Title);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        add(table, BorderLayout.CENTER);
        table.setRowHeight(15);
        JTableHeader tableHeader = table.getTableHeader();
        add(tableHeader, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);
    }
}