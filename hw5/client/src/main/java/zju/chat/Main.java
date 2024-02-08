package zju.chat;

import javax.swing.*;

/**
 * Main: the entry of the program
 */
public class Main {

    public static void main(String[] args) throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        SwingUtilities.invokeLater(Index::new);
    }
}