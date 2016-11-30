package view;

import model.Group;
import model.Month;
import view.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GroupPanel extends JPanel {

    private JPanel panel = new JPanel();
    private JTabbedPane  monthInfoPane = new JTabbedPane();
    private ArrayList<TabbedPanel> monthTabs = new ArrayList<>();
    private Group group;
    private ChartPanel chartPanel;

    public Group getGroup() {
        return group;
    }

    public void initListeners() {
        monthInfoPane.addChangeListener(e -> {
            JTabbedPane jTabbedPane = (JTabbedPane) e.getSource();
            Month month = ((TabbedPanel) jTabbedPane.getSelectedComponent()).getMonth();
            chartPanel.setMonth(month);
            chartPanel.createChart();
        });
    }
    public GroupPanel(Group group, ChartPanel chartPanel) {
        this.group = group;
        this.chartPanel = chartPanel;

        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, monthInfoPane);
        add(BorderLayout.NORTH, panel);
    }

    public void setGroup(Group group, ChartPanel chartPanel) {
        for (Month month : group.getMonthInfo()) {
            TabbedPanel p = new TabbedPanel(month);
            monthTabs.add(p);
            monthInfoPane.addTab(month.getMonth(), p);
            p.setChartPanel(chartPanel);
        }
    }

    public Month getFirstMonth() {
        return monthTabs.get(0).getMonth();
    }

    public void addPanel(TabbedPanel panel, ChartPanel chartPanel) {
        monthTabs.add(panel);
        monthInfoPane.addTab(panel.getMonth().getMonth(), panel);
        panel.setChartPanel(chartPanel);
    }

    public void deleteColumn(String columnName) {
        for (TabbedPanel panel : monthTabs) {
            panel.removeColumn(columnName);
        }
        group.removeColumn(columnName);
    }

    public void addColumn(String columnName) {
        for (TabbedPanel panel : monthTabs) {
            panel.addColumn(columnName);
        }
        group.addColumn(columnName);
    }

    public void changeColumn(String oldColumnName, String newColumnName) {
        for (TabbedPanel panel : monthTabs) {
            panel.changeColumn(oldColumnName, newColumnName);
        }
        group.changeColumn(oldColumnName, newColumnName);
    }
}