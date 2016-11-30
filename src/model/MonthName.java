package model;

public enum MonthName {
    JANUARY("январь"), FEBRUARY("февраль"), MARCH("март"), APRIL("апрель"), MAY("май"),
    JUNE("июнь"), JULY("июль"), AUGUST("август"), SEPTEMBER("сентябрь"), OCTOBER("октябрь"),
    NOVEMBER("ноябрь"), DECEMBER("декабрь");

    private final String name;

    MonthName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
