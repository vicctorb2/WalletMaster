package model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class Group {

    private ArrayList<Month> monthInfo = new ArrayList<>();
    private Set<String> columnNames = new LinkedHashSet<>();
    private String groupName;

    public Group() {

    }

    public Group(String groupName){
        this.groupName = groupName;
        for (MonthName monthName : MonthName.values()) {
            monthInfo.add(new Month(monthName.getName()));
        }
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void addMonth(Month month) {
        monthInfo.add(month);
    }

    public String getGroupName() {
        return groupName;
    }

    public ArrayList<Month> getMonthInfo(){
        return monthInfo;
    }

    public Set<String> getColumnNames(){
        return columnNames;
    }

    public void addColumn(String columnName) {
        columnNames.add(columnName);
    }

    public void removeColumn(String columnName) {
        columnNames.remove(columnName);
    }

    public void changeColumn(String oldColumnName, String newColumnName) {
        columnNames.remove(oldColumnName);
        columnNames.add(newColumnName);
    }
}
