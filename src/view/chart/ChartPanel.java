package view.chart;

import model.Group;
import model.Month;
import model.State;
import org.japura.gui.CheckComboBox;
import org.japura.gui.event.ListCheckListener;
import org.japura.gui.event.ListEvent;
import org.japura.gui.model.ListCheckModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ChartPanel extends JPanel {

    private HashMap<String, State> states = new HashMap<>();
    private Group group;
    private Month month;
    private ChartType chartType = ChartType.LINE;

    private CheckComboBox ccb;
    private JComboBox chartTypeList;
    private ListCheckModel listCheckModel;

    private JPanel northPanel;
    private JPanel centerPanel;

    private DefaultCategoryDataset monthData;
    private DefaultCategoryDataset yearData;

    public ChartPanel(){

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

        ccb = new CheckComboBox();

        listCheckModel = ccb.getModel();
        listCheckModel.addListCheckListener(new ListCheckListener() {
            @Override
            public void removeCheck(ListEvent listEvent) {
                ArrayList<State> states = new ArrayList<>();
                ArrayList<Object> items = (ArrayList<Object>) listCheckModel.getCheckeds();
                for (Object object : items) {
                    states.add(ChartPanel.this.states.get(object));
                }
                ChartPanel.this.createChart(states);
            }

            @Override
            public void addCheck(ListEvent listEvent) {
                ArrayList<State> states = new ArrayList<>();
                ArrayList<Object> items = (ArrayList<Object>) listCheckModel.getCheckeds();
                for (Object object : items) {
                    states.add(ChartPanel.this.states.get(object));
                }
                ChartPanel.this.createChart(states);
            }
        });

        chartTypeList = new JComboBox();
        for (ChartType type : ChartType.values()) chartTypeList.addItem(type.getName());

        chartTypeList.addActionListener(e -> {
            setChartType(ChartType.convertValue((String) ((JComboBox) e.getSource()).getSelectedItem()));
            createChart();
        });

        northPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        northPanel.setBorder(new EmptyBorder(5, 0, 0, 5));
        northPanel.add(new JPanel());
        northPanel.add(new JPanel());
        northPanel.add(new JPanel());
        northPanel.add(chartTypeList);
        northPanel.add(ccb);

        centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.setPreferredSize(new Dimension(660, getHeight()));

        setLayout(new BorderLayout());
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;

        ccb = new CheckComboBox();
        setMonth(group.getMonthInfo().get(0));
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
        revalidateCheckBox();
    }

    public void revalidateCheckBox() {
        states.clear();
        listCheckModel.clear();
        for (State state : month.getStateList()) {
            states.put(state.getName(), state);
            listCheckModel.addElement(state.getName());
        }
        listCheckModel.setCheck(states.keySet().toArray());
    }

    public DefaultCategoryDataset getMonthData() {
        return monthData;
    }

    public DefaultCategoryDataset getYearData() {
        return yearData;
    }

    public void createChart() {
        monthData = new DefaultCategoryDataset();
        for (State state : month.getStateList()) {
            java.util.List list = state.getDailyData();
            for(int i = 0; i < list.size(); i++) {
                monthData.setValue((Number) Double.parseDouble(list.get(i).toString()), state.getName(), i + 1);
            }
        }
        JFreeChart chart = drawChart(month.getMonth(), "День", "Сумма",
                monthData, chartType);
        CategoryPlot p = chart.getCategoryPlot();
        p.setRangeGridlinePaint(Color.black);

        org.jfree.chart.ChartPanel panel = new org.jfree.chart.ChartPanel(chart);

        yearData = new DefaultCategoryDataset();
        for (int i = 0; i < group.getMonthInfo().size(); i++) {
            java.util.List list = group.getMonthInfo().get(i).getStateList();
            for(int j = 0; j < list.size(); j++) {
                yearData.setValue((Number) ((State) list.get(j)).getSummary(),
                        j + 1, i + 1);
            }
        }
        JFreeChart chart1 = drawChart("", "Месяц", "Сумма",
                yearData, chartType);
        org.jfree.chart.ChartPanel panel1 = new org.jfree.chart.ChartPanel(chart1);

        centerPanel.removeAll();
        centerPanel.add(panel);
        centerPanel.add(panel1);
        centerPanel.revalidate();
    }

    public void createChart(ArrayList<State> states) {
        monthData = new DefaultCategoryDataset();
        for (State state : states) {
            java.util.List list = state.getDailyData();
            for(int i = 0; i < list.size(); i++) {
                monthData.setValue((Number) Double.parseDouble(list.get(i).toString()), state.getName(), i + 1);
            }
        }
        JFreeChart chart = drawChart(month.getMonth(), "День", "Сумма",
                monthData, chartType);
        CategoryPlot p = chart.getCategoryPlot();
        p.setRangeGridlinePaint(Color.black);

        org.jfree.chart.ChartPanel panel = new org.jfree.chart.ChartPanel(chart);

        yearData = new DefaultCategoryDataset();
        for (int i = 0; i < group.getMonthInfo().size(); i++) {
            java.util.List list = group.getMonthInfo().get(i).getStateList();
            for(int j = 0; j < list.size(); j++) {
                yearData.setValue((Number) ((State) list.get(j)).getSummary(),
                        j + 1, i + 1);
            }
        }
        JFreeChart chart1 = drawChart("", "Месяц", "Сумма",
                yearData, chartType);

        org.jfree.chart.ChartPanel panel1 = new org.jfree.chart.ChartPanel(chart1);

        centerPanel.removeAll();
        centerPanel.add(panel);
        centerPanel.add(panel1);
        centerPanel.revalidate();
    }

    private JFreeChart drawChart(String title, String xLabel, String yLabel,
                                 DefaultCategoryDataset dataSet, ChartType chartType) {
        switch (chartType) {
            case LINE:
                return ChartFactory.createLineChart(title, xLabel, yLabel, dataSet,
                        PlotOrientation.VERTICAL, false, true, false);
            case BAR:
                return ChartFactory.createBarChart(title, xLabel, yLabel, dataSet,
                        PlotOrientation.VERTICAL, false, true, false);
            default:
                return null;
        }
    }
}
