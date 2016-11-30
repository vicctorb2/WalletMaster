package model;

import java.util.ArrayList;
import java.util.List;

public class Month {

    private String month;
    private ArrayList<State> columnData = new ArrayList<>();

    public Month(String month){
        this.month = month;
    }

    public double getColumnSummary(int index){
        return columnData.get(index).getSummary();
    }

    public double setColumnDataValue(int columnIndex , int rowIndex , double value){
        columnData.get(columnIndex).setValue(rowIndex, value);
        columnData.get(columnIndex).recountSummary();

        return columnData.get(columnIndex).getSummary();
    }

    public int removeState(String stateName) {
        for(int i = 0; i < columnData.size(); i++) {
            if (stateName.equals(columnData.get(i).getName())) {
                columnData.remove(i);
                return i;
            }
        }

        return -1;
    }

    public void addState(String stateName) {
        columnData.add(new State(stateName));
    }

    public void addState(State state) {
        columnData.add(state);
    }
    public String getMonth() {
        return month;
    }

    public List<State> getStateList() {
        return columnData;
    }
}
