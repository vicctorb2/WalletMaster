package model;

import java.util.ArrayList;
import java.util.List;

public class State {

    public static final int DAYS_IN_MONTH = 31;

    private String name;
    private ArrayList<Double> dailyData = new ArrayList();
    private double summary = 0D;

    public State() {

    }

    public State(String columnHeader){
        this.name = columnHeader;
        for(int i  = 0 ; i < DAYS_IN_MONTH; i ++)
            dailyData.add(0D);
        summary = 0 ;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void recountSummary(){
        summary = 0;
        for (int i = 0; i < dailyData.size() - 1 ; i ++) {
            summary += dailyData.get(i);
        }
    }

    public void addValue(Double value) {
        dailyData.add(value);
    }

    public void setSummary(double summary) {
        this.summary = summary;
    }
    public void setValue(int index, double value){
        dailyData.set(index, value);
    }

    public List getDailyData() {
        return dailyData;
    }

    public String getName() {
        return name;
    }

    public double getSummary() {
        return summary;
    }
}

