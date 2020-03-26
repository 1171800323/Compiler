package lexer;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;


public class DFA extends JFrame {
    public Vector<Vector<String>> data = new Vector<Vector<String>>();
    public Vector<String> Title = new Vector<String>();
    private final Graph graph = new Graph("src/dfa.txt");
    private final Set<Integer> State = new TreeSet<>();

    public DFA() {
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
            Vector<String> W = new Vector<String>();

            String[] s = new String[num + 1];
            s[0] = String.valueOf(state);
            for (Edge edge : graph.getEdges()) {
                int locataion = -1;
                if (!edge.getWeight().contains("other")) {
                    locataion = Title.indexOf(edge.getWeight());
                } else {
                    locataion = num;
                }
                if (state == edge.getSource()) {
                    String temp = String.valueOf(edge.getTarget());
                    if (graph.getEndStates().containsKey(edge.getTarget())) {
                        temp += "("+graph.getEndStates().get(edge.getTarget())+")";
                    }
                    s[locataion] = temp;
                    System.out.println(locataion + " " + s[locataion]);
                } else {
                    if (s[locataion] == null) {
                        s[locataion] = " ";
                    }
                }
            }
            if (graph.getEndStates().containsKey(state)) {
                s[0] += '*';
            }
            for (int i = 0; i <= num; i++) {
                W.add(s[i]);
            }
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