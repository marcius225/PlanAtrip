package com.example.planatrip;

public class BudgetObject {

    private int id;
    private String expenseName;
    private String expenseValue;

    public BudgetObject(String expenseName, String expenseValue, int id) {
        this.expenseName = expenseName;
        this.expenseValue = expenseValue;
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName){
        this.expenseName = expenseName;
    }

    public String getExpenseValue() {
        return expenseValue;
    }

    public void setExpenseValue() {
        this.expenseValue = expenseValue;
    }

}