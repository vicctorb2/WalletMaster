package view.dialog;

import util.ControlTitle;
import view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GroupDialog extends JDialog {

    private MainFrame owner;

    private JList groupList;
    private JLabel groupLabel = new JLabel(ControlTitle.GROUP_LIST);
    private final JButton addButton = new JButton(ControlTitle.ADD);
    private JButton changeButton = new JButton(ControlTitle.CHANGE);
    private JButton deleteButton = new JButton(ControlTitle.DELETE);
    private JButton closeButton = new JButton(ControlTitle.CLOSE);

    private String selectedGroupName = null;

    public GroupDialog(MainFrame owner) {
        this.owner = owner;
        setTitle(ControlTitle.GROUPS);
        DefaultListModel model = new DefaultListModel();
        owner.getBaseInstance().getGroupNames().forEach(model::addElement);
        groupList = new JList(model);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        setBounds(dimension.width * 3 / 8, dimension.height * 3 / 10, dimension.width / 4, dimension.height * 5 / 20);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        groupLabel.setFont(new Font(Font.SERIF, Font.BOLD, 14));

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new GridLayout(4, 1, 2, 2));
        controlsPanel.setBorder(new EmptyBorder(18, 0, 0, 0));
        controlsPanel.add(addButton);
        controlsPanel.add(changeButton);
        controlsPanel.add(deleteButton);
        controlsPanel.add(closeButton);

        JPanel groupListPanel = new JPanel(new BorderLayout());
        groupListPanel.add(groupLabel, BorderLayout.NORTH);
        groupListPanel.add(new JScrollPane(groupList), BorderLayout.CENTER);

        mainPanel.add(groupListPanel, BorderLayout.CENTER);
        mainPanel.add(controlsPanel, BorderLayout.EAST);

        add(mainPanel);

        setListeners();

        setModal(true);
        setVisible(true);
    }

    private void setListeners() {

        groupList.addListSelectionListener(e -> {
            selectedGroupName = (String) ((JList) e.getSource()).getSelectedValue();
        });

        addButton.addActionListener(e -> {
            String groupName = "";
            while (groupName.trim().isEmpty()) {
                groupName = JOptionPane.showInputDialog(ControlTitle.ENTER_GROUP_NAME);
                if (groupName == null) {
                    break;
                }
                if (!groupName.trim().isEmpty()) {
                    if (!((DefaultListModel) groupList.getModel()).contains(groupName.trim())) {
                        owner.addGroup(groupName);
                        DefaultListModel model = (DefaultListModel) groupList.getModel();
                        model.addElement(groupName);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                ControlTitle.GROUP_NAME_EXIST,
                                ControlTitle.INPUT_ERROR,
                                JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            ControlTitle.ENTER_GROUP_NAME,
                            ControlTitle.INPUT_ERROR,
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        changeButton.addActionListener(e -> {
            if (selectedGroupName == null) {
                JOptionPane.showMessageDialog(this,
                        ControlTitle.SELECT_GROUP_FROM_LIST,
                        ControlTitle.INPUT_ERROR,
                        JOptionPane.WARNING_MESSAGE);
            } else {
                String newGroupName = "";
                while (newGroupName.trim().isEmpty()) {
                    newGroupName = (String) JOptionPane.showInputDialog(this,
                            ControlTitle.ENTER_NEW_GROUP_NAME,
                            ControlTitle.CHANGE_GROUP,
                            JOptionPane.DEFAULT_OPTION,
                            null, null, selectedGroupName);
                    if (newGroupName == null) {
                        break;
                    }
                    if (!newGroupName.trim().isEmpty()) {
                        DefaultListModel model = (DefaultListModel) groupList.getModel();
                        model.setElementAt(newGroupName, groupList.getSelectedIndex());
                        owner.changeGroup(selectedGroupName, newGroupName);
                        selectedGroupName = newGroupName;
                    } else {
                        JOptionPane.showMessageDialog(this,
                                ControlTitle.ENTER_GROUP_NAME,
                                ControlTitle.INPUT_ERROR,
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        deleteButton.addActionListener(e -> {
            if (selectedGroupName == null) {
                JOptionPane.showMessageDialog(this,
                        ControlTitle.SELECT_GROUP_FROM_LIST,
                        ControlTitle.INPUT_ERROR,
                        JOptionPane.WARNING_MESSAGE);
            } else {
                int result = JOptionPane.showConfirmDialog(this,
                        String.format(ControlTitle.DELETE_GROUP_FORMAT_REQUEST, selectedGroupName),
                        ControlTitle.DELETE_GROUP,
                        JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    owner.deleteGroup(selectedGroupName);
                    DefaultListModel model = (DefaultListModel) groupList.getModel();
                    model.remove(groupList.getSelectedIndex());
                }
            }
        });
        closeButton.addActionListener(e -> {
            setVisible(false);
            invalidate();
        });
    }
}
