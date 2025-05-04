package controller;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TransactionSummary {
    private final StringProperty month;
    private final DoubleProperty bills;
    private final DoubleProperty subscriptions;
    private final DoubleProperty entertainment;
    private final DoubleProperty foodDrink;
    private final DoubleProperty groceries;
    private final DoubleProperty health;
    private final DoubleProperty shopping;
    private final DoubleProperty travel;
    private final DoubleProperty other;
    private final DoubleProperty totalExpenditure;
    private final DoubleProperty income;
    private final DoubleProperty grossSavings;

    public TransactionSummary(String month, double bills, double subscriptions, double entertainment,
            double foodDrink, double groceries, double health, double shopping,
            double travel, double other, double totalExpenditure, double income, double grossSavings) {
        this.month = new SimpleStringProperty(month);
        this.bills = new SimpleDoubleProperty(bills);
        this.subscriptions = new SimpleDoubleProperty(subscriptions);
        this.entertainment = new SimpleDoubleProperty(entertainment);
        this.foodDrink = new SimpleDoubleProperty(foodDrink);
        this.groceries = new SimpleDoubleProperty(groceries);
        this.health = new SimpleDoubleProperty(health);
        this.shopping = new SimpleDoubleProperty(shopping);
        this.travel = new SimpleDoubleProperty(travel);
        this.other = new SimpleDoubleProperty(other);
        this.totalExpenditure = new SimpleDoubleProperty(totalExpenditure);
        this.income = new SimpleDoubleProperty(income);
        this.grossSavings = new SimpleDoubleProperty(grossSavings);
    }

    // Month
    public String getMonth() {
        return month.get();
    }

    public void setMonth(String value) {
        month.set(value);
    }

    public StringProperty monthProperty() {
        return month;
    }

    // Bills
    public double getBills() {
        return bills.get();
    }

    public void setBills(double value) {
        bills.set(value);
    }

    public DoubleProperty billsProperty() {
        return bills;
    }

    // Subscriptions
    public double getSubscriptions() {
        return subscriptions.get();
    }

    public void setSubscriptions(double value) {
        subscriptions.set(value);
    }

    public DoubleProperty subscriptionsProperty() {
        return subscriptions;
    }

    // Entertainment
    public double getEntertainment() {
        return entertainment.get();
    }

    public void setEntertainment(double value) {
        entertainment.set(value);
    }

    public DoubleProperty entertainmentProperty() {
        return entertainment;
    }

    // Food & Drink
    public double getFoodDrink() {
        return foodDrink.get();
    }

    public void setFoodDrink(double value) {
        foodDrink.set(value);
    }

    public DoubleProperty foodDrinkProperty() {
        return foodDrink;
    }

    // Groceries
    public double getGroceries() {
        return groceries.get();
    }

    public void setGroceries(double value) {
        groceries.set(value);
    }

    public DoubleProperty groceriesProperty() {
        return groceries;
    }

    // Health
    public double getHealth() {
        return health.get();
    }

    public void setHealth(double value) {
        health.set(value);
    }

    public DoubleProperty healthProperty() {
        return health;
    }

    // Shopping
    public double getShopping() {
        return shopping.get();
    }

    public void setShopping(double value) {
        shopping.set(value);
    }

    public DoubleProperty shoppingProperty() {
        return shopping;
    }

    // Travel
    public double getTravel() {
        return travel.get();
    }

    public void setTravel(double value) {
        travel.set(value);
    }

    public DoubleProperty travelProperty() {
        return travel;
    }

    // Other
    public double getOther() {
        return other.get();
    }

    public void setOther(double value) {
        other.set(value);
    }

    public DoubleProperty otherProperty() {
        return other;
    }

    // Total Expenditure
    public double getTotalExpenditure() {
        return totalExpenditure.get();
    }

    public void setTotalExpenditure(double value) {
        totalExpenditure.set(value);
    }

    public DoubleProperty totalExpenditureProperty() {
        return totalExpenditure;
    }

    // Income
    public double getIncome() {
        return income.get();
    }

    public void setIncome(double value) {
        income.set(value);
    }

    public DoubleProperty incomeProperty() {
        return income;
    }

    // Gross Savings
    public double getGrossSavings() {
        return grossSavings.get();
    }

    public void setGrossSavings(double value) {
        grossSavings.set(value);
    }

    public DoubleProperty grossSavingsProperty() {
        return grossSavings;
    }
}