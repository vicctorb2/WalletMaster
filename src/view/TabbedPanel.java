package view;

import model.Month;
import model.State;
import view.chart.ChartPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

public class TabbedPanel extends JPanel {

    private JTable table;
    private MyTableModel tableModel = new MyTableModel();

    private Month month;
    private ChartPanel chartPanel;

    public void setChartPanel(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }
    public TabbedPanel(Month month){
        super();
        this.month = month;

        setPanelFeatures();
        setTableFeatures();

        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            try {
                if (row >= 0 && column >= 0 && row < State.DAYS_IN_MONTH) {
                    double summary = month.setColumnDataValue(column, row, Double.parseDouble(tableModel.getValueAt(row, column).toString()));
                    tableModel.setValueAt(String.format("%.2f", summary), State.DAYS_IN_MONTH, column);
                    chartPanel.createChart((ArrayList<State>) month.getStateList());
                }
            } catch (NumberFormatException e1) {
                JOptionPane.showMessageDialog(this,
                        "Введите корректное значение.",
                        "Ошибка ввода",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        add(BorderLayout.CENTER , new JScrollPane(table ,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        table.setAutoResizeMode(table.AUTO_RESIZE_OFF);
        add(BorderLayout.NORTH , new JPanel());
        add(BorderLayout.EAST , new JPanel());
        add(BorderLayout.WEST , new JPanel());
    }

    private void setPanelFeatures(){
        setLayout(new BorderLayout(5, 5));
    }

    public void removeColumn(String columnName) {
        int removeIndex = month.removeState(columnName);

        if (removeIndex != -1) {
            tableModel.removeColumn(removeIndex);
        }
    }

    public void addColumn(String columnName) {
        tableModel.addColumn(columnName);
        month.addState(columnName);
    }

    public void addColumn(State state) {
        java.util.List dailyInfo = new ArrayList(state.getDailyData());
        dailyInfo.add(state.getSummary());
        tableModel.addColumn(state.getName(), dailyInfo.toArray());
    }

    public void changeColumn(String oldColumnName, String newColumnName) {
        for (int i = 0; i < month.getStateList().size(); i++) {
            if (month.getStateList().get(i).getName().equals(oldColumnName)) {
                month.getStateList().get(i).setName(newColumnName);
                table.getTableHeader().getColumnModel().getColumn(i).setHeaderValue(newColumnName);
            }
        }
        revalidate();
    }

    public Month getMonth() {
        return month;
    }

    private void setTableFeatures(){
        table = new JTable(tableModel);
        table.setColumnModel(new DefaultTableColumnModel());
        table.setRowSelectionAllowed(false);
        tableModel.setRowCount(State.DAYS_IN_MONTH + 1);
    }

    public class MyTableModel extends DefaultTableModel {
        public void removeColumn(int column) {
            columnIdentifiers.remove(column);
            for (Object row: dataVector) {
                ((Vector) row).remove(column);
            }
            fireTableStructureChanged();
        }
    }
}
