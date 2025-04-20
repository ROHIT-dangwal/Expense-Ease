package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.util.Arrays;

public class LineChartController {

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    public void initialize() {
        // Set up chart title and axes
        lineChart.setTitle("Monthly Income vs Expenses");
        xAxis.setLabel("Month");
        yAxis.setLabel("Amount (₹)");

        // Setup animation
        lineChart.setAnimated(true);

        // Create sample data
        String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

        // Create series for income
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        incomeSeries.getData().add(new XYChart.Data<>("Jan", 2500));
        incomeSeries.getData().add(new XYChart.Data<>("Feb", 2500));
        incomeSeries.getData().add(new XYChart.Data<>("Mar", 2700));
        incomeSeries.getData().add(new XYChart.Data<>("Apr", 2700));
        incomeSeries.getData().add(new XYChart.Data<>("May", 2700));
        incomeSeries.getData().add(new XYChart.Data<>("Jun", 3000));
        incomeSeries.getData().add(new XYChart.Data<>("Jul", 3000));
        incomeSeries.getData().add(new XYChart.Data<>("Aug", 3000));

        // Create series for expenses
        XYChart.Series<String, Number> expensesSeries = new XYChart.Series<>();
        expensesSeries.setName("Expenses");
        expensesSeries.getData().add(new XYChart.Data<>("Jan", 1800));
        expensesSeries.getData().add(new XYChart.Data<>("Feb", 1900));
        expensesSeries.getData().add(new XYChart.Data<>("Mar", 1850));
        expensesSeries.getData().add(new XYChart.Data<>("Apr", 2100));
        expensesSeries.getData().add(new XYChart.Data<>("May", 2200));
        expensesSeries.getData().add(new XYChart.Data<>("Jun", 2300));
        expensesSeries.getData().add(new XYChart.Data<>("Jul", 2100));
        expensesSeries.getData().add(new XYChart.Data<>("Aug", 2050));

        // Create series for gross savings
        XYChart.Series<String, Number> grossSavingsSeries = new XYChart.Series<>();
        grossSavingsSeries.setName("Gross Savings");
        grossSavingsSeries.getData().add(new XYChart.Data<>("Jan", 180));
        grossSavingsSeries.getData().add(new XYChart.Data<>("Feb", 190));
        grossSavingsSeries.getData().add(new XYChart.Data<>("Mar", 180));
        grossSavingsSeries.getData().add(new XYChart.Data<>("Apr", 210));
        grossSavingsSeries.getData().add(new XYChart.Data<>("May", 220));
        grossSavingsSeries.getData().add(new XYChart.Data<>("Jun", 230));
        grossSavingsSeries.getData().add(new XYChart.Data<>("Jul", 210));
        grossSavingsSeries.getData().add(new XYChart.Data<>("Aug", 200));

        // Add data to chart
        lineChart.getData().addAll(incomeSeries, expensesSeries, grossSavingsSeries);

        // Add style class for CSS styling
        lineChart.getStyleClass().add("financial-line-chart");
    }

    // In a real application, you would add methods to update the chart with real
    // data
    public void updateChartData(ObservableList<XYChart.Series<String, Number>> newData) {
        lineChart.getData().clear();
        lineChart.getData().addAll(newData);
    }
}
