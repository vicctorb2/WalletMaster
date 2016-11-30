package view.chart;

public enum ChartType {
    LINE("Линейный"), BAR("Гистограмма");

    private String name;

    ChartType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ChartType convertValue(String value) {
        switch (value) {
            case "Гистограмма":
                return BAR;
            case "Линейный":
                return LINE;
            default:
                return null;
        }
    }
}
