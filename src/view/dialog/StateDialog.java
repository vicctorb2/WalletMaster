package view.dialog;

import util.ControlTitle;
import view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Set;

public class StateDialog extends JDialog {

    private MainFrame owner;

    private JList groupList;
    private JList stateList;
    private JLabel groupLabel = new JLabel(ControlTitle.GROUP_LIST);
    private JLabel stateLabel = new JLabel(ControlTitle.STATE_LIST);
    private JButton addButton = new JButton(ControlTitle.ADD);
    private JButton deleteButton = new JButton(ControlTitle.DELETE);
    private JButton changeButton = new JButton(ControlTitle.CHANGE);
    private JButton closeButton = new JButton(ControlTitle.CLOSE);

    public StateDialog(MainFrame owner) {
        this.owner = owner;
        setTitle(ControlTitle.STATES);


        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        setBounds(dimension.width * 3 / 8,
                dimension.height * 3 / 10,
                dimension.width / 4 + 15,
                dimension.height * 5 / 20);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultListModel model = new DefaultListModel();
        owner.getBaseInstance().getGroupNames().forEach(model::addElement);
        groupList = new JList(model);
        stateList = new JList(new DefaultListModel<>());

        groupLabel.setFont(new Font(Font.SERIF, Font.BOLD, 14));
        stateLabel.setFont(new Font(Font.SERIF, Font.BOLD, 14));

        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel controlsPanel = new JPanel();
        controlsPanel.setBorder(new EmptyBorder(18, 0, 5, 0));
        controlsPanel.setLayout(new GridLayout(4, 1, 2, 2));

        controlsPanel.add(addButton);
        controlsPanel.add(changeButton);
        controlsPanel.add(deleteButton);
        controlsPanel.add(closeButton);

        JPanel groupListPanel = new JPanel(new BorderLayout());
        groupListPanel.add(groupLabel, BorderLayout.NORTH);
        groupListPanel.add(new JScrollPane(groupList), BorderLayout.CENTER);

        JPanel stateListPanel = new JPanel(new BorderLayout());
        stateListPanel.add(stateLabel, BorderLayout.NORTH);
        stateListPanel.add(new JScrollPane(stateList), BorderLayout.CENTER);

        mainPanel.add(groupListPanel);
        mainPanel.add(stateListPanel);
        mainPanel.add(controlsPanel);

        add(mainPanel);

        setListeners();
        setModal(true);
        setVisible(true);
    }
    private void setListeners() {

        groupList.addListSelectionListener(e -> {
            DefaultListModel model = (DefaultListModel) stateList.getModel();
            Set<String> statesList = owner.getBaseInstance().getGroups().
                    get(groupList.getSelectedIndex()).getColumnNames();
            model.clear();
            statesList.forEach(model::addElement);
        });

        addButton.addActionListener(e -> {
            if (groupList.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this,
                        ControlTitle.SELECT_GROUP_FROM_LIST,
                        ControlTitle.INPUT_ERROR,
                        JOptionPane.WARNING_MESSAGE);
            } else {
                String stateName = "";
                while (stateName.trim().isEmpty()) {
                    stateName = JOptionPane.showInputDialog(ControlTitle.ENTER_STATE_NAME);
                    if (stateName == null) {
                        break;
                    }
                    if (!stateName.trim().isEmpty()) {
                        if (!((DefaultListModel) groupList.getModel()).contains(stateName.trim())) {
                            owner.addState((String) groupList.getSelectedValue(), stateName);
                            DefaultListModel model = (DefaultListModel) stateList.getModel();
                            model.addElement(stateName);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    ControlTitle.STATE_NAME_EXIST,
                                    ControlTitle.INPUT_ERROR,
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                                ControlTitle.ENTER_STATE_NAME,
                                ControlTitle.INPUT_ERROR,
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        changeButton.addActionListener(e -> {
            if (stateList.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this,
                        ControlTitle.SELECT_STATE_FROM_LIST,
                        ControlTitle.INPUT_ERROR,
                        JOptionPane.WARNING_MESSAGE);
            } else {
                String newStateName = "";
                while (newStateName.trim().isEmpty()) {
                    newStateName = (String) JOptionPane.showInputDialog(this,
                            ControlTitle.ENTER_NEW_STATE_NAME,
                            ControlTitle.CHANGE_STATE,
                            JOptionPane.DEFAULT_OPTION,
                            null, null, stateList.getSelectedValue());
                    if (newStateName == null) {
                        break;
                    }
                    if (!newStateName.trim().isEmpty()) {
                        DefaultListModel model = (DefaultListModel) stateList.getModel();
                        owner.changeState((String) groupList.getSelectedValue(),
                                (String) stateList.getSelectedValue(),
                                newStateName);
                        model.setElementAt(newStateName, stateList.getSelectedIndex());
                    } else {
                        JOptionPane.showMessageDialog(this,
                                ControlTitle.ENTER_STATE_NAME,
                                ControlTitle.INPUT_ERROR,
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        deleteButton.addActionListener(e -> {
            if (stateList.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this,
                        ControlTitle.SELECT_STATE_FROM_LIST,
                        ControlTitle.INPUT_ERROR,
                        JOptionPane.WARNING_MESSAGE);
            } else {
                int result = JOptionPane.showConfirmDialog(this,
                        String.format(ControlTitle.DELETE_STATE_FORMAT_REQUEST, stateList.getSelectedValue()),
                        ControlTitle.DELETE_STATE,
                        JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    owner.deleteState((String) groupList.getSelectedValue(), (String) stateList.getSelectedValue());
                    DefaultListModel model = (DefaultListModel) stateList.getModel();
                    model.remove(stateList.getSelectedIndex());
                }
            }
        });
        closeButton.addActionListener(e -> {
            setVisible(false);
            invalidate();
        });
    }
}
