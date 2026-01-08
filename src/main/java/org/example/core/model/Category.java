package org.example.core.model;

public class Category {

    public String name;
    public CategoryType type;
    public double budget; // только для EXPENSE

    public Category(String name, CategoryType type) {
        this.name = name;
        this.type = type;
        this.budget = 0;
    }
}
