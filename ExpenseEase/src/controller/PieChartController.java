package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import controller.DatabaseConnector;

public class PieChartController {

    @FXML
    private PieChart pieChart;

    @FXML
    private Label currentmonth;

    @FXML
    public void initialize() {
        // Set chart title
        pieChart.setTitle("Expense Distribution");

        // Get current month and display it
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        currentmonth.setText(today.format(formatter));

        // Load real data from database for current month
        loadCurrentMonthData();

        // Enable animation
        pieChart.setAnimated(true);

        // Make the chart labels visible
        pieChart.setLabelsVisible(true);

        // Add style class for CSS styling
        pieChart.getStyleClass().add("expense-pie-chart");
    }

    private void loadCurrentMonthData() {
        // Get current month's start and end dates
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        Connection conn = DatabaseConnector.getConnection();

        // Check if connection is valid before proceeding
        if (conn == null) {
            System.err.println("Database connection failed - showing sample data");
            displaySampleData();
            currentmonth.setText(currentmonth.getText() + " (Database Connection Failed)");
            return;
        }

        try {
            // Check if we have any data for the current month
            String checkQuery = "SELECT COUNT(*) FROM transactions WHERE date BETWEEN ? AND ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setDate(1, java.sql.Date.valueOf(startDate));
                checkStmt.setDate(2, java.sql.Date.valueOf(endDate));

                ResultSet checkRs = checkStmt.executeQuery();
                if (checkRs.next() && checkRs.getInt(1) > 0) {
                    // We have data, fetch the sums for each category
                    fetchAndDisplayData(conn, startDate, endDate);
                } else {
                    // No data for current month - don't create empty records anymore
                    // Just show empty state
                    displayEmptyState();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error: " + e.getMessage());
            // Fall back to sample data if there's a database error
            displaySampleData();
            currentmonth.setText(currentmonth.getText() + " (Database Error)");
        }
        // Don't close the connection here as it's a shared connection from
        // DatabaseConnector
    }

    private void fetchAndDisplayData(Connection conn, LocalDate startDate, LocalDate endDate) throws SQLException {
        // Modified query to SUM all transactions within date range
        String query = "SELECT " +
                "SUM(bills) as bills, " +
                "SUM(subscriptions) as subscriptions, " +
                "SUM(entertainment) as entertainment, " +
                "SUM(food) as food, " +
                "SUM(groceries) as groceries, " +
                "SUM(health) as health, " +
                "SUM(shopping) as shopping, " +
                "SUM(travel) as travel, " +
                "SUM(other) as other " +
                "FROM transactions " +
                "WHERE date BETWEEN ? AND ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Create a list to hold the pie chart data
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

                // Add each category with its value - only add if value > 0
                double bills = rs.getDouble("bills");
                double subscriptions = rs.getDouble("subscriptions");
                double entertainment = rs.getDouble("entertainment");
                double food = rs.getDouble("food");
                double groceries = rs.getDouble("groceries");
                double health = rs.getDouble("health");
                double shopping = rs.getDouble("shopping");
                double travel = rs.getDouble("travel");
                double other = rs.getDouble("other");

                // Calculate total expenses
                double totalExpenses = bills + subscriptions + entertainment + food +
                        groceries + health + shopping + travel + other;

                if (totalExpenses > 0) {
                    // We have actual expense data, add each category
                    if (bills > 0)
                        pieChartData.add(new PieChart.Data("Bills", bills));
                    if (subscriptions > 0)
                        pieChartData.add(new PieChart.Data("Subscriptions", subscriptions));
                    if (entertainment > 0)
                        pieChartData.add(new PieChart.Data("Entertainment", entertainment));
                    if (food > 0)
                        pieChartData.add(new PieChart.Data("Food & Drink", food));
                    if (groceries > 0)
                        pieChartData.add(new PieChart.Data("Groceries", groceries));
                    if (health > 0)
                        pieChartData.add(new PieChart.Data("Health and Wellbeing", health));
                    if (shopping > 0)
                        pieChartData.add(new PieChart.Data("Shopping", shopping));
                    if (travel > 0)
                        pieChartData.add(new PieChart.Data("Travel", travel));
                    if (other > 0)
                        pieChartData.add(new PieChart.Data("Other", other));

                    // Update the chart with the real data
                    pieChart.setData(pieChartData);

                    // Add tooltips with exact values
                    addTooltipsToPieChart();
                } else {
                    // If all values are 0, show empty state
                    displayEmptyState();
                }
            } else {
                displayEmptyState();
            }
        }
    }

    // Display an empty state when no expenses are recorded
    private void displayEmptyState() {
        // Clear any existing data
        pieChart.setData(FXCollections.observableArrayList());

        // Reset the label text if it already has a message
        if (currentmonth.getText().contains("(")) {
            // Extract just the month and year part
            currentmonth.setText(currentmonth.getText().split("\\(")[0].trim());
        }

        // Add the "No Expenses" text
        currentmonth.setText(currentmonth.getText() + " (No Expenses)");

        // Display a single empty slice with a different color
        ObservableList<PieChart.Data> emptyData = FXCollections.observableArrayList(
                new PieChart.Data("No expenses recorded", 1));
        pieChart.setData(emptyData);

        // Style the empty slice if it exists
        if (!pieChart.getData().isEmpty()) {
            PieChart.Data slice = pieChart.getData().get(0);
            if (slice.getNode() != null) {
                slice.getNode().setStyle("-fx-pie-color: #e0e0e0;"); // Light gray
            }
        }

        // Add a tooltip to the empty slice
        addTooltipsToPieChart();
    }

    private void displaySampleData() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Bills", 25),
                new PieChart.Data("Subscriptions", 20),
                new PieChart.Data("Entertainment", 10),
                new PieChart.Data("Food & Drink", 15),
                new PieChart.Data("Groceries", 12),
                new PieChart.Data("Health and Wellbeing", 8),
                new PieChart.Data("Shopping", 10),
                new PieChart.Data("Travel", 15),
                new PieChart.Data("Other", 5));

        pieChart.setData(pieChartData);
        addTooltipsToPieChart();
    }

    private void addTooltipsToPieChart() {
        // Add tooltips to each pie slice showing the exact value in currency format
        for (final PieChart.Data data : pieChart.getData()) {
            // Calculate the percentage of total
            double total = pieChart.getData().stream()
                    .mapToDouble(PieChart.Data::getPieValue)
                    .sum();
            double percentage = total > 0 ? (data.getPieValue() / total) * 100 : 0;

            // Format the tooltip text based on whether it's the "No expenses" slice
            String tooltipText;
            if (data.getName().equals("No expenses recorded")) {
                tooltipText = "No expenses have been recorded for this period.\nAdd expenses to see the distribution.";
            } else {
                tooltipText = String.format("%s: $%.2f (%.1f%%)",
                        data.getName(), data.getPieValue(), percentage);
            }

            Tooltip tooltip = new Tooltip(tooltipText);

            // Make sure the node is available before installing the tooltip
            if (data.getNode() != null) {
                Tooltip.install(data.getNode(), tooltip);

                // Add hover effect to highlight slices
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
    }

    // Method to update chart data for a specific month
    public void updateChartData(YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        // Reset the label text
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        currentmonth.setText(startDate.format(formatter));

        try (Connection conn = DatabaseConnector.getConnection()) {
            // Check if there's data for this month
            String checkQuery = "SELECT COUNT(*) FROM transactions WHERE date BETWEEN ? AND ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setDate(1, java.sql.Date.valueOf(startDate));
                checkStmt.setDate(2, java.sql.Date.valueOf(endDate));

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    fetchAndDisplayData(conn, startDate, endDate);
                } else {
                    // No data for this month
                    displayEmptyState();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            displaySampleData();
            currentmonth.setText(currentmonth.getText() + " (Error)");
        }
    }
}