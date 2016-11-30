package view;

import model.Group;
import model.Month;
import util.ControlTitle;
import util.ReportProcessor;
import util.XMLProcessor;
import view.chart.ChartPanel;
import view.dialog.GroupDialog;
import view.dialog.ReportDialog;
import view.dialog.StateDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private XMLProcessor base = new XMLProcessor();

    private Map<String, GroupPanel> panels = new LinkedHashMap<>();

    private JComboBox groupList;
    private JPanel panelForPane = new JPanel(new CardLayout());
    private JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 5, 5));
    private ChartPanel chartPanel = new ChartPanel();

    public MainFrame(String frameName){
        super(frameName);
        setWindowFeatures();

        try {
            base.loadData();
            initializeContainers();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        createMenu();
        createControls();
        panelPositioning();

        setVisible(true);
    }

    private void createControls() {

        groupList = new JComboBox();
        for (Group group : base.getGroups()) groupList.addItem(group.getGroupName());
        groupList.addActionListener(e -> {
            String groupName = (String) ((JComboBox) e.getSource()).getSelectedItem();
            CardLayout cl = (CardLayout)panelForPane.getLayout();
            cl.show(panelForPane, groupName);
            chartPanel.setGroup(panels.get(groupName).getGroup());
            chartPanel.setMonth(panels.get(groupName).getFirstMonth());
            chartPanel.createChart();
        });

        JButton groups = new JButton(ControlTitle.GROUPS);
        groups.addActionListener(e -> {
            new GroupDialog(this);
        });

        JButton report = new JButton(ControlTitle.REPORT);
        report.addActionListener(e -> {
            new ReportDialog(this);
        });

        JButton states = new JButton(ControlTitle.STATES);
        states.addActionListener(e -> {
            new StateDialog(this);
        });

        buttonPanel.add(states);
        buttonPanel.add(groups);
        buttonPanel.add(report);
        buttonPanel.add(groupList);
    }

    private void panelPositioning(){
        panelForPane.add(new JPanel() , "nullPanel");
        buttonPanel.setBackground(new JTabbedPane().getBackground());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(BorderLayout.NORTH, buttonPanel);
        panel.add(panelForPane, BorderLayout.CENTER);

        add(BorderLayout.EAST, chartPanel);
        add(panel, BorderLayout.CENTER);
    }

    private void setWindowFeatures(){
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        URL imgURL = MainFrame.class.getResource("wallet-master.png");
        ImageIcon imageIcon = new ImageIcon(imgURL);
        setIconImage(imageIcon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension size = kit.getScreenSize();
        setBounds(0, 0, size.width, size.height);
    }

    private void createMenu(){
        JMenuBar menu = new JMenuBar();

        JMenu file = new JMenu(ControlTitle.FILE);
        JMenuItem report = new JMenuItem(ControlTitle.REPORT);

        report.addActionListener(e -> new ReportDialog(this));
        JMenuItem saveInfo = new JMenuItem(ControlTitle.SAVE);
        saveInfo.addActionListener(e -> base.saveData());
        JMenuItem exit = new JMenuItem(ControlTitle.EXIT);
        exit.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        file.add(report);
        file.add(saveInfo);
        file.add(exit);
        menu.add(file);
        setJMenuBar(menu);
    }

    public void initializeContainers() {
        for (int i = 0; i < base.getGroups().size(); i++) {
            GroupPanel groupPanel = new GroupPanel(base.getGroups().get(i), chartPanel);
            for (Month month : base.getGroups().get(i).getMonthInfo()) {
                TabbedPanel tabbedPanel = new TabbedPanel(month);
                month.getStateList().forEach(tabbedPanel::addColumn);
                groupPanel.addPanel(tabbedPanel, chartPanel);
            }
            groupPanel.initListeners();
            panels.put(base.getGroups().get(i).getGroupName(), groupPanel);
            panelForPane.add(groupPanel, base.getGroups().get(i).getGroupName());
            if (i == 0) {
                chartPanel.setGroup(base.getGroups().get(i));
                chartPanel.createChart();
            }
        }
    }

    public XMLProcessor getBaseInstance(){
        return base;
    }

    public void addGroup(String groupName) {
        Group group = new Group(groupName.trim());
        GroupPanel newGroupedPanel = new GroupPanel(group, chartPanel);
        newGroupedPanel.setGroup(group, chartPanel);
        newGroupedPanel.initListeners();
        groupList.addItem(groupName);
        panels.put(groupName, newGroupedPanel);
        panelForPane.add(newGroupedPanel, groupName);
        base.addGroup(group);
    }

    public void deleteGroup(String groupName) {
        groupList.removeItem(groupName);
        panelForPane.remove(panels.get(groupName));
        panels.remove(groupName);
        base.removeGroup(groupName);
    }

    public void changeGroup(String oldName, String newName) {
        GroupPanel panel = panels.get(oldName);
        Group group = panel.getGroup();
        group.setGroupName(newName);
        groupList.addItem(newName);
        groupList.removeItem(oldName);
        panels.put(newName, panels.get(oldName));
        panelForPane.remove(panels.get(oldName));
        panelForPane.add(newName, panels.get(newName));
        panels.remove(oldName);
        base.changeGroup(oldName, newName);
    }

    public void addState(String groupName, String stateName) {
        GroupPanel panel = panels.get(groupName);
        panel.addColumn(stateName);
        chartPanel.revalidateCheckBox();
        chartPanel.createChart();
    }

    public void deleteState(String groupName, String stateName) {
        GroupPanel panel = panels.get(groupName);
        panel.deleteColumn(stateName);
        chartPanel.revalidateCheckBox();
        chartPanel.createChart();
    }

    public void changeState(String groupName, String oldName, String newName) {
        GroupPanel panel = panels.get(groupName);
        panel.changeColumn(oldName, newName);
        chartPanel.revalidateCheckBox();
        chartPanel.createChart();
    }

    public void makeReport(int groupIndex, int monthIndex) {
        if (monthIndex == 12) {
            ReportProcessor.makeReport(groupIndex, base);
        } else {
            ReportProcessor.makeReport(groupIndex, monthIndex, base);
        }
    }
}