package ru.jobj4;

public class HR {
    private final int id;
    private final String company;
    private final String name;

    public HR(int id, String company, String name) {
        this.id = id;
        this.company = company;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }
}
