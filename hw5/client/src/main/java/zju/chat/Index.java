package zju.chat;

import zju.chat.component.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Index: login and register
 */
public class Index extends JFrame {

    JPanel loginPanel;
    JPanel registerPanel;

    public Index() {
        super("Chat-Java");

        createLoginPanel();
        createRegisterPanel();

        setContentPane(loginPanel);

        setMinimumSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    JPanel createFormItemPanel(JComponent label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(Color.WHITE);
        panel.add(label);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(field);
        panel.setMaximumSize(new Dimension(500, panel.getPreferredSize().height));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return panel;
    }

    void createLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel titleLabel = new ZLabel("Chat-Java");
        titleLabel.setFont(Style.font.deriveFont(Font.PLAIN, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel usernameLabel = new ZLabel("Username:");
        JTextField usernameField = new ZTextField();
        JLabel passwordLabel = new ZLabel("Password:");
        JPasswordField passwordField = new ZPasswordField();
        JLabel serverLabel = new ZLabel("Server:");
        JTextField serverField = new ZTextField();
        serverField.setText("localhost:8080");
        JButton loginButton = new ZButton("Login", ZButton.Type.PRIMARY);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, loginButton.getPreferredSize().height));
        JButton registerButton = new ZButton("To Register", ZButton.Type.PRIMARY);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, registerButton.getPreferredSize().height));

        JLabel footerLabel = new ZLabel("Developed by PeiPei, Zhejiang University");
        footerLabel.setFont(Style.font.deriveFont(Font.PLAIN, 12));
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerLabel.setForeground(new Color(108, 117, 125));

        JPanel usernamePanel = createFormItemPanel(usernameLabel, usernameField);
        JPanel passwordPanel = createFormItemPanel(passwordLabel, passwordField);
        JPanel serverPanel = createFormItemPanel(serverLabel, serverField);
        JPanel buttonPanel = createFormItemPanel(loginButton, registerButton);

        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(usernamePanel);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(passwordPanel);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(serverPanel);
        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(buttonPanel);
        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(footerLabel);

        registerButton.addActionListener(e -> {
            setContentPane(registerPanel);
            revalidate();
        });

        ActionListener loginActionListener = e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String server = serverField.getText();
            if (username.isEmpty() || password.isEmpty() || server.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Client.login(username, password, server);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        loginButton.addActionListener(loginActionListener);
        usernameField.addActionListener(loginActionListener);
        passwordField.addActionListener(loginActionListener);
        serverField.addActionListener(loginActionListener);
    }

    void createRegisterPanel() {
        registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel titleLabel = new ZLabel("Chat-Java");
        titleLabel.setFont(Style.font.deriveFont(Font.PLAIN, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new ZLabel("Email:");
        JTextField emailField = new ZTextField();
        JLabel usernameLabel = new ZLabel("Username:");
        JTextField usernameField = new ZTextField();
        JLabel passwordLabel = new ZLabel("Password:");
        JPasswordField passwordField = new ZPasswordField();
        JLabel confirmPasswordLabel = new ZLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new ZPasswordField();
        JLabel serverLabel = new ZLabel("Server:");
        JTextField serverField = new ZTextField();
        serverField.setText("localhost:8080");
        JButton loginButton = new ZButton("To Login", ZButton.Type.PRIMARY);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, loginButton.getPreferredSize().height));
        JButton registerButton = new ZButton("Register", ZButton.Type.PRIMARY);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, registerButton.getPreferredSize().height));

        JLabel footerLabel = new ZLabel("Developed by PeiPei, Zhejiang University");
        footerLabel.setFont(Style.font.deriveFont(Font.PLAIN, 12));
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerLabel.setForeground(new Color(108, 117, 125));

        JPanel emailPanel = createFormItemPanel(emailLabel, emailField);
        JPanel usernamePanel = createFormItemPanel(usernameLabel, usernameField);
        JPanel passwordPanel = createFormItemPanel(passwordLabel, passwordField);
        JPanel confirmPasswordPanel = createFormItemPanel(confirmPasswordLabel, confirmPasswordField);
        JPanel serverPanel = createFormItemPanel(serverLabel, serverField);
        JPanel buttonPanel = createFormItemPanel(loginButton, registerButton);

        registerPanel.add(Box.createVerticalStrut(10));
        registerPanel.add(titleLabel);
        registerPanel.add(Box.createVerticalStrut(30));
        registerPanel.add(emailPanel);
        registerPanel.add(Box.createVerticalStrut(20));
        registerPanel.add(usernamePanel);
        registerPanel.add(Box.createVerticalStrut(20));
        registerPanel.add(passwordPanel);
        registerPanel.add(Box.createVerticalStrut(20));
        registerPanel.add(confirmPasswordPanel);
        registerPanel.add(Box.createVerticalStrut(20));
        registerPanel.add(serverPanel);
        registerPanel.add(Box.createVerticalStrut(30));
        registerPanel.add(buttonPanel);
        registerPanel.add(Box.createVerticalStrut(30));
        registerPanel.add(footerLabel);

        loginButton.addActionListener(e -> {
            setContentPane(loginPanel);
            revalidate();
        });

        ActionListener registerActionListener = e -> {
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String server = serverField.getText();
            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || server.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Client.register(email, username, password, server);
                JOptionPane.showMessageDialog(this, "Register success", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        registerButton.addActionListener(registerActionListener);
        emailField.addActionListener(registerActionListener);
        usernameField.addActionListener(registerActionListener);
        passwordField.addActionListener(registerActionListener);
        confirmPasswordField.addActionListener(registerActionListener);
        serverField.addActionListener(registerActionListener);

    }
}
