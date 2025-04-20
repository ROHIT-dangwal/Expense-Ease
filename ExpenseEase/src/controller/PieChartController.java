package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class PieChartController {

    @FXML
    private PieChart pieChart;

    @FXML
    private Label currentmonth;

    @FXML
    public void initialize() {
        // Set chart title
        pieChart.setTitle("Expense Distribution");

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        currentmonth.setText(today.format(formatter));
        // Create sample data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Bills", 25),
                new PieChart.Data("Subscriptions", 20),
                new PieChart.Data("Entertainment", 10),
                new PieChart.Data("Food & Drink", 15),
                new PieChart.Data("Groceries", 12),
                new PieChart.Data("Health and Wellbeing", 8),
                new PieChart.Data("Shopping", 10),
                new PieChart.Data("Travel", 15));

        // Add data to chart
        pieChart.setData(pieChartData);

        // Enable animation
        pieChart.setAnimated(true);

        // Make the chart labels visible
        pieChart.setLabelsVisible(true);

        // Add tooltips to show exact values
        addTooltipsToPieChart();

        // Add style class for CSS styling
        pieChart.getStyleClass().add("expense-pie-chart");
    }

    private void addTooltipsToPieChart() {
        // Add tooltips to each pie slice showing the exact percentage
        for (final PieChart.Data data : pieChart.getData()) {
            Tooltip tooltip = new Tooltip(String.format("%s: $%.2f",
                    data.getName(), data.getPieValue() * 100));
            Tooltip.install(data.getNode(), tooltip);

            // Optional: Add hover effect to highlight slices
            data.getNode().setOnMouseEntered(event -> {
                data.getNode().setScaleX(1.1);
                data.getNode().setScaleY(1.1);
            });
            data.getNode().setOnMouseExited(event -> {
                data.getNode().setScaleX(1);
                data.getNode().setScaleY(1);
            });
        }
    }

    // In a real application, you would add methods to update the chart with real
    // data
    public void updateChartData(ObservableList<PieChart.Data> newData) {
        pieChart.getData().clear();
        pieChart.setData(newData);
        addTooltipsToPieChart();
    }
}
