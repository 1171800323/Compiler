package gui;

import parser.Parser;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Enumeration;

public class DrawSyntaxTree {

    public DrawSyntaxTree(String filename) {
        Parser parser = new Parser(filename);
        DefaultMutableTreeNode rootNode = parser.getRoot();
        JFrame jf = new JFrame("SyntaxTree");
        jf.setSize(400, 800);
        jf.setLocationRelativeTo(null);
        JPanel panel = new JPanel(new BorderLayout());

        JTree tree = new JTree(rootNode);

        // 设置树显示根节点句柄
       tree.setShowsRootHandles(true);

       // 设置树节点可编辑
       tree.setEditable(true);
       
       expandAll(tree,new TreePath(rootNode), true);
       
       // 创建滚动面板，包裹树（因为树节点展开后可能需要很大的空间来显示，所以需要用一个滚动面板来包裹）
       JScrollPane scrollPane = new JScrollPane(tree);

       // 添加滚动面板到那内容面板
       panel.add(scrollPane, BorderLayout.CENTER);

       // 设置窗口内容面板并显示
       jf.setContentPane(panel);
       jf.setVisible(true);
    }
    
    private static void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

}
