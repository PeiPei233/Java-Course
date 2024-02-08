package zju.chat;

import lombok.Getter;
import zju.chat.component.ZButton;
import zju.chat.component.ZComboBox;
import zju.chat.component.ZLabel;
import zju.chat.component.ZTextField;
import zju.chat.model.Message;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import static zju.chat.Style.*;


/**
 * Chat: chat window
 */
public class Chat extends JFrame {

    /**
     * The client that this chat window belongs to.
     */
    private final Client client;

    /**
     * The list model of the contacts.
     */
    private final DefaultListModel<Message> contacts = new DefaultListModel<>();

    /**
     * The current contact that is being chatted with.
     */
    @Getter
    private String currentContact = null;

    /**
     * Whether the current contact is a room.
     */
    @Getter
    private boolean isRoom = false;

    /**
     * The style document of the message display.
     */
    private StyledDocument messageDoc;

    /**
     * The style of the message from me.
     */
    private JList<Message> contactList;

    /**
     * Constructor of Chat.
     *
     * @param client the client that this chat window belongs to
     */
    public Chat(Client client) {
        super(client.getUsername());        // set the title of the window to the username
        this.client = client;
        setMinimumSize(new Dimension(800, 600));
        // logout when the window is closed
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.logout();
                dispose();
            }
        });

        // get contacts
        for (Message message : client.getContacts()) {
            contacts.addElement(message);
        }

        // create window components
        createUI();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Create the UI of the chat window.
     */
    void createUI() {

        // the add contact panel
        JPanel addContactPanel = new JPanel();
        addContactPanel.setLayout(new BoxLayout(addContactPanel, BoxLayout.X_AXIS));
        addContactPanel.setBackground(Color.WHITE);
        addContactPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // the contact combo box used to select the type of the contact
        JComboBox<String> contactComboBox = new ZComboBox<>();
        contactComboBox.setEditable(false);
        contactComboBox.setFont(Style.font);
        contactComboBox.setBorder(null);
        contactComboBox.addItem("User");
        contactComboBox.addItem("Room");
        contactComboBox.setMaximumSize(new Dimension(100, contactComboBox.getPreferredSize().height));

        // the contact field used to input the name of the contact
        JTextField contactField = new ZTextField();
        contactField.setMaximumSize(new Dimension(Integer.MAX_VALUE, contactComboBox.getPreferredSize().height));
        contactField.setMinimumSize(new Dimension(50, contactComboBox.getPreferredSize().height));

        // the add button used to add the contact
        JButton addButton = new ZButton("Add", ZButton.Type.PRIMARY);
        addButton.setMaximumSize(new Dimension(50, contactComboBox.getPreferredSize().height));

        addContactPanel.add(contactComboBox);
        addContactPanel.add(Box.createHorizontalStrut(10));
        addContactPanel.add(contactField);
        addContactPanel.add(Box.createHorizontalStrut(10));
        addContactPanel.add(addButton);
        addContactPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, addContactPanel.getPreferredSize().height));

        // the contact list
        contactList = new JList<>(contacts);
        // set the cell renderer of the contact list
        contactList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                Message message = (Message) value;
                String username = message.getOpposite(client.getUsername());
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                if (isSelected) {
                    panel.setBackground(new Color(222, 226, 230));
                } else {
                    panel.setBackground(Color.WHITE);
                }
                // the top panel contains the username and the timestamp
                JPanel topPanel = new JPanel();
                topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
                topPanel.setBackground(panel.getBackground());
                String time = timestampToString(message.getTimestamp());

                // truncate text to fit the width of the list
                FontMetrics fm = getFontMetrics(Style.font);
                int maxUsernameWidth = list.getWidth() - fm.stringWidth(time) - getInsets().left - getInsets().right - 20;
                String truncatedUsername = truncateText(fm, username, maxUsernameWidth);

                topPanel.add(new ZLabel(truncatedUsername));
                topPanel.add(Box.createHorizontalGlue());
                topPanel.add(new ZLabel(time, ZLabel.Type.SECONDARY));
                panel.add(topPanel);

                // the bottom panel contains the truncated content of the message
                JPanel messagePanel = new JPanel();
                messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
                messagePanel.setBackground(panel.getBackground());

                // truncate text to fit the width of the list
                int maxContentWidth = list.getWidth() - getInsets().left - getInsets().right - 20;
                String content = message.getContent();
                if (message.isRoom() && message.getFrom() != null && !message.getFrom().equals(client.getUsername())) {
                    content = message.getFrom() + ": " + content;
                }
                String truncatedContent = truncateText(fm, content, maxContentWidth);
                messagePanel.add(new ZLabel(truncatedContent, ZLabel.Type.SECONDARY));
                messagePanel.add(Box.createHorizontalGlue());
                panel.add(messagePanel);
                panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                return panel;
            }
        });

        // the scroll pane of the contact list
        JScrollPane contactListScrollPane = new JScrollPane(contactList);
        contactListScrollPane.setBackground(Color.WHITE);
        contactListScrollPane.setMinimumSize(new Dimension(120, contactListScrollPane.getPreferredSize().height));

        // the panel of the contact list
        JPanel contactListPanel = new JPanel();
        contactListPanel.setLayout(new BoxLayout(contactListPanel, BoxLayout.Y_AXIS));
        contactListPanel.setBackground(Color.WHITE);
        contactListPanel.setBorder(null);
        contactListPanel.add(addContactPanel);
        contactListPanel.add(contactListScrollPane);

        // the message display text pane
        JTextPane messagePane = new JTextPane();
        messagePane.setEditable(false);
        messagePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messageDoc = messagePane.getStyledDocument();

        // the message display scroll pane
        JScrollPane messageDisplayPanel = new JScrollPane(messagePane);
        messageDisplayPanel.setBackground(Color.WHITE);

        // the input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // the input text pane
        JTextPane inputPane = new JTextPane();
        inputPane.setFont(Style.font);
        inputPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        inputPane.setEditable(false);

        // the input scroll pane
        JScrollPane inputScrollPane = new JScrollPane(inputPane);
        inputScrollPane.setBackground(Color.WHITE);
        inputScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        inputScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        inputPanel.add(inputScrollPane);

        // the send button
        JButton sendButton = new ZButton("Send", ZButton.Type.PRIMARY);
        sendButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        sendButton.setMinimumSize(new Dimension(0, sendButton.getPreferredSize().height));

        // the send button panel contains the send button and the horizontal glue
        JPanel sendButtonPanel = new JPanel();
        sendButtonPanel.setLayout(new BoxLayout(sendButtonPanel, BoxLayout.X_AXIS));
        sendButtonPanel.setBackground(Color.WHITE);
        sendButtonPanel.add(Box.createHorizontalGlue());
        sendButtonPanel.add(sendButton);
        sendButtonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, sendButtonPanel.getPreferredSize().height));
        inputPanel.add(sendButtonPanel);

        // the list of room members
        JList<String> roomMemberList = new JList<>(new Vector<>());
        roomMemberList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
                if (isSelected) {
                    panel.setBackground(new Color(222, 226, 230));
                } else {
                    panel.setBackground(Color.WHITE);
                }
                // truncate text
                FontMetrics fm = getFontMetrics(Style.font);
                int maxWidth = list.getWidth() - getInsets().left - getInsets().right - 20;
                String truncatedText = truncateText(fm, (String) value, maxWidth);
                panel.add(new ZLabel(truncatedText));
                panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
                panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return panel;
            }
        });

        // the scroll pane of the room member list
        JScrollPane roomMemberListScrollPane = new JScrollPane(roomMemberList);
        roomMemberListScrollPane.setBackground(Color.WHITE);

        // the panel of the room member list
        JPanel roomMemberListPanel = new JPanel();
        roomMemberListPanel.setLayout(new BoxLayout(roomMemberListPanel, BoxLayout.Y_AXIS));
        roomMemberListPanel.setBackground(Color.WHITE);
        roomMemberListPanel.setBorder(null);

        // the label of the room member list
        JLabel roomMemberListLabel = new ZLabel("Room Members");
        roomMemberListLabel.setBackground(Color.WHITE);
        roomMemberListLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        roomMemberListPanel.add(roomMemberListLabel);
        roomMemberListPanel.add(roomMemberListScrollPane);
        roomMemberListPanel.setVisible(false);

        // the panel of the message display
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setBackground(Color.WHITE);

        // the split panel of the message display and the room member list
        JSplitPane messageSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, messageDisplayPanel, inputPanel);
        messageSplitPanel.setResizeWeight(0.8);

        // the split panel of the contact list and the message display
        JSplitPane roomSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, messageSplitPanel, roomMemberListPanel);
        roomSplitPanel.setResizeWeight(0.8);
        roomSplitPanel.addPropertyChangeListener(e -> {
            roomMemberList.repaint();
        });

        // the title label of the message display
        JLabel titleLabel = new ZLabel("No Contact");
        titleLabel.setFont(Style.font.deriveFont(Font.PLAIN, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // the quit room button
        JLabel quitRoomButton = new ZLabel("Quit Room", ZLabel.Type.LINK);
        quitRoomButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        quitRoomButton.setVisible(false);

        // the title panel of the message display contains the title label and the quit room button
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(quitRoomButton);

        messagePanel.add(titlePanel, BorderLayout.NORTH);
        messagePanel.add(roomSplitPanel, BorderLayout.CENTER);

        // the split panel of the contact list and the message display
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contactListPanel, messagePanel);
        splitPane.setResizeWeight(0.2);
        splitPane.setOneTouchExpandable(true);
        splitPane.addPropertyChangeListener(e -> {
            contactList.repaint();
        });

        // set the content pane of the window
        setContentPane(splitPane);

        // select the contact when the contact list is clicked
        contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Message selectedMessage = contactList.getSelectedValue();
                // get the selected contact and update the message display
                if (selectedMessage != null) {
                    String originContact = currentContact;
                    boolean originIsRoom = isRoom;
                    currentContact = selectedMessage.getOpposite(client.getUsername());
                    isRoom = selectedMessage.isRoom();
                    String newContactTitle = currentContact;
                    if (isRoom) {
                        Vector<String> roomMembers = null;
                        try {
                            roomMembers = client.getRoomMembers(currentContact);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        newContactTitle += " (" + roomMembers.size() + ")";
                        roomMemberList.setListData(roomMembers);
                        roomMemberListPanel.setVisible(true);
                        quitRoomButton.setVisible(true);
                        if (!originIsRoom) {
                            roomSplitPanel.setDividerLocation(0.8);
                        }
                    } else {
                        roomMemberListPanel.setVisible(false);
                        quitRoomButton.setVisible(false);
                    }
                    titleLabel.setText(newContactTitle);
                    Vector<Message> messages = null;
                    try {
                        messages = client.getMessages(currentContact);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Fail to get messages: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    if (currentContact != null) {
                        if (originContact == null || !originContact.equals(currentContact)) {
                            try {
                                messageDoc.remove(0, messageDoc.getLength());
                                inputPane.setEditable(true);
                                inputPane.setText("");
                            } catch (BadLocationException err) {
                                err.printStackTrace();
                            }
                        }
                        if (messages != null) {
                            for (Message message : messages) {
                                appendMessage(message);
                            }
                        }
                    }
                } else {
                    currentContact = null;
                    titleLabel.setText("No Contact");
                    isRoom = false;
                    roomMemberListPanel.setVisible(false);
                    quitRoomButton.setVisible(false);
                    try {
                        messageDoc.remove(0, messageDoc.getLength());
                        inputPane.setEditable(false);
                        inputPane.setText("");
                    } catch (BadLocationException err) {
                        err.printStackTrace();
                    }
                }
            }
        });

        // double click item to chat with the contact
        roomMemberList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && roomMemberList.getSelectedValue() != null) {
                    String contact = roomMemberList.getSelectedValue();
                    if (!contact.equals(client.getUsername())) {
                        try {
                            client.addContact(contact, false);
                            // select the new contact
                            for (int i = 0; i < contacts.size(); i++) {
                                Message message = contacts.get(i);
                                if (message.getOpposite(client.getUsername()).equals(contact)) {
                                    contactList.setSelectedIndex(i);
                                    break;
                                }
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(Chat.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        // use enter to send message
        // use shift + enter to insert a new line
        inputPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    String content = inputPane.getText();
                    if (!content.isEmpty()) {
                        try {
                            client.sendMessage(currentContact, content, isRoom);
                            inputPane.setText("");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(Chat.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isShiftDown()) {
                    e.consume();
                    inputPane.setText(inputPane.getText() + "\n");
                }
            }
        });

        // send message when the send button is clicked
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String content = inputPane.getText();
                if (!content.isEmpty()) {
                    try {
                        client.sendMessage(currentContact, content, isRoom);
                        inputPane.setText("");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(Chat.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // add contact when the add button is clicked or the enter key is pressed
        ActionListener addContactListener = e -> {
            String contact = contactField.getText();
            if (!contact.isEmpty()) {
                String type = (String) contactComboBox.getSelectedItem();
                try {
                    assert type != null;
                    boolean isRoom = type.equals("Room");
                    client.addContact(contact, isRoom);
                    // select the new contact
                    for (int i = 0; i < contacts.size(); i++) {
                        Message message = contacts.get(i);
                        if (message.getOpposite(client.getUsername()).equals(contact)) {
                            contactList.setSelectedIndex(i);
                            break;
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Chat.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(Chat.this, "Please fill in the contact name", "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        addButton.addActionListener(addContactListener);
        contactField.addActionListener(addContactListener);

        // quit room when the quit room button is clicked
        quitRoomButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    client.quitRoom(currentContact);
                    contacts.removeElementAt(contactList.getSelectedIndex());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Chat.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }

    /**
     * Add a message to the chat window.
     *
     * @param message the message to be added
     */
    public void addMessage(Message message) {
        if (currentContact != null && currentContact.equals(message.getOpposite(client.getUsername()))) {
            appendMessage(message);
            if (contacts.get(0).getOpposite(client.getUsername()).equals(currentContact)) {
                contacts.set(0, message);
                return;
            }
        }

        // move the contact to the top
        String selectedContact = currentContact;
        for (int i = 0; i < contacts.size(); i++) {
            Message contact = contacts.get(i);
            if (contact.getOpposite(client.getUsername()).equals(message.getOpposite(client.getUsername()))) {
                contacts.remove(i);
                break;
            }
        }
        contacts.add(0, message);
        if (selectedContact != null) {
            for (int i = 0; i < contacts.size(); i++) {
                Message contact = contacts.get(i);
                if (contact.getOpposite(client.getUsername()).equals(selectedContact)) {
                    contactList.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    /**
     * Append a message to the message display.
     *
     * @param message the message to be appended
     */
    private void appendMessage(Message message) {
        try {
            int start = messageDoc.getLength();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String userInfo = message.getFrom() + " " + sdf.format(new Date(message.getTimestamp())) + "\n";
            messageDoc.insertString(messageDoc.getLength(), userInfo, message.getFrom().equals(client.getUsername()) ? fromMeInfoStyle : fromOtherInfoStyle);
            messageDoc.setParagraphAttributes(start, userInfo.length(), message.getFrom().equals(client.getUsername()) ? fromMeInfoStyle : fromOtherInfoStyle, true);

            start = messageDoc.getLength();
            String content = message.getContent() + "\n";
            messageDoc.insertString(messageDoc.getLength(), content, message.getFrom().equals(client.getUsername()) ? fromMeMessageStyle : fromOtherMessageStyle);
            messageDoc.setParagraphAttributes(start, content.length(), message.getFrom().equals(client.getUsername()) ? fromMeMessageStyle : fromOtherMessageStyle, true);
        } catch (BadLocationException err) {
            err.printStackTrace();
        }
    }

    /**
     * Convert a timestamp to a string.
     *
     * @param timestamp the timestamp in milliseconds to be converted
     * @return the string representation of the timestamp
     */
    String timestampToString(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));
        Calendar now = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {
            return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        } else if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            return String.format("%02d-%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            return String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        }
    }


    /**
     * Truncate text to fit the width of the list.
     *
     * @param fm       the font metrics of the font
     * @param text     the text to be truncated
     * @param maxWidth the maximum width of the text
     * @return the truncated text
     */
    String truncateText(FontMetrics fm, String text, int maxWidth) {
        if (fm.stringWidth(text) > maxWidth) {
            String ellipsis = "...";

            // truncate text with origin + ... < max
            int textWidth = fm.stringWidth(ellipsis);
            int nChars = 0;
            for (; nChars < text.length(); nChars++) {
                textWidth += fm.charWidth(text.charAt(nChars));
                if (textWidth > maxWidth) {
                    break;
                }
            }

            text = text.substring(0, nChars) + ellipsis;
        }

        return text;
    }

}
