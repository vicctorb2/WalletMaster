package view.dialog;

import view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ReportDialog extends JDialog {

    private MainFrame owner;

    private JComboBox groupList = new JComboBox();
    private JComboBox monthList = new JComboBox();
    private JButton createButton = new JButton("Создать");
    private JButton cancelButton = new JButton("Отмена");

    public ReportDialog(MainFrame owner) {
        this.owner = owner;
        owner.getBaseInstance().getGroupNames().forEach(groupList::addItem);

        owner.getBaseInstance().getGroups().get(0).getMonthInfo().forEach(month -> {
            monthList.addItem(month.getMonth());
        });
        monthList.addItem("За год");

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        setBounds(dimension.width * 3 / 8, dimension.height * 3 / 10, dimension.width / 4, dimension.height * 5 / 20);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel controlsPanel = new JPanel();
        controlsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        controlsPanel.setLayout(new GridLayout(4, 1, 2, 2));
        controlsPanel.add(groupList);
        controlsPanel.add(monthList);
        controlsPanel.add(new JPanel());

        JPanel flourPanel = new JPanel();
        flourPanel.add(createButton);
        flourPanel.add(cancelButton);

        controlsPanel.add(flourPanel);

        add(controlsPanel);

        setListeners();
        setModal(true);
        setVisible(true);
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> {
            setVisible(false);
            invalidate();
        });
        createButton.addActionListener(e -> {
            owner.makeReport(groupList.getSelectedIndex(),
                    monthList.getSelectedIndex());
        });
    }
}

