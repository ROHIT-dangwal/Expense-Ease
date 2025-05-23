package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import controller.DatabaseConnector;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class LineChartController implements Initializable {

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;
    private int currentDisplayYear;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        lineChart.setTitle("Monthly Income vs Expenses");
        xAxis.setLabel("Month");
        yAxis.setLabel("Amount (₹)");

        lineChart.setAnimated(true);
        lineChart.setCreateSymbols(true);

        yAxis.setAutoRanging(false);
        yAxis.setForceZeroInRange(false);

        currentDisplayYear = LocalDate.now().getYear();

        loadChartData();

        lineChart.getStyleClass().add("financial-line-chart");
    }

    public void refreshChart() {
        System.out.println("Refreshing chart for year: " + currentDisplayYear);
        updateChartForYear(currentDisplayYear);
    }

    private void loadChartData() {
        // Default to current year
        updateChartForYear(LocalDate.now().getYear());
    }

    private Connection getDBConnection() throws SQLException {
        Connection conn = DatabaseConnector.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to establish database connection");
        }

        if (conn.isClosed() || !conn.isValid(2)) { // 2 second timeout
            // Try to reconnect
            DatabaseConnector.closeConnection();
            conn = DatabaseConnector.getConnection();

            if (conn == null || conn.isClosed()) {
                throw new SQLException("Database connection is unavailable");
            }
        }
        return conn;
    }

    private Map<Month, Double> getIncomeByMonth(int year) throws SQLException {
        Map<Month, Double> incomeByMonth = new HashMap<>();

        try (Connection conn = getDBConnection()) {
            String query = "SELECT MONTH(date) as month, SUM(income) as total_income " +
                    "FROM transactions " +
                    "WHERE YEAR(date) = ? " +
                    "GROUP BY MONTH(date) " +
                    "ORDER BY MONTH(date)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, year);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int monthNum = rs.getInt("month");
                    double income = rs.getDouble("total_income");
                    incomeByMonth.put(Month.of(monthNum), income);
                    System.out.println("DB Income for Month " + monthNum + ": ₹" + income);
                }
            }
        }

        return incomeByMonth;
    }

    private Map<Month, Double> getExpensesByMonth(int year) throws SQLException {
        Map<Month, Double> expensesByMonth = new HashMap<>();

        try (Connection conn = getDBConnection()) {
            String query = "SELECT MONTH(date) as month, " +
                    "SUM(bills + subscriptions + entertainment + food + groceries + " +
                    "health + shopping + travel + other) as total_expenses " +
                    "FROM transactions " +
                    "WHERE YEAR(date) = ? " +
                    "GROUP BY MONTH(date) " +
                    "ORDER BY MONTH(date)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, year);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int monthNum = rs.getInt("month");
                    double expenses = rs.getDouble("total_expenses");
                    expensesByMonth.put(Month.of(monthNum), expenses);
                    System.out.println("DB Expenses for Month " + monthNum + ": ₹" + expenses);
                }
            }
        }

        return expensesByMonth;
    }

    private void addTooltips() {
        for (XYChart.Series<String, Number> series : lineChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(
                            series.getName() + " in " + data.getXValue() + ": ₹" +
                                    String.format("%.2f", data.getYValue().doubleValue()));
                    javafx.scene.control.Tooltip.install(data.getNode(), tooltip);

                    // Add hover effect
                    data.getNode().setOnMouseEntered(event -> {
                        data.getNode().setScaleX(1.5);
                        data.getNode().setScaleY(1.5);
                    });

                    data.getNode().setOnMouseExited(event -> {
                        data.getNode().setScaleX(1);
                        data.getNode().setScaleY(1);
                    });
                }
            }
        }
    }

    private void adjustYAxisScale() {
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        for (XYChart.Series<String, Number> series : lineChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                double value = data.getYValue().doubleValue();
                if (value < minValue)
                    minValue = value;
                if (value > maxValue)
                    maxValue = value;
            }
        }

        if (minValue == Double.MAX_VALUE || maxValue == Double.MIN_VALUE) {
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(1000);
            yAxis.setTickUnit(100);
            return;
        }

        double range = maxValue - minValue;
        double padding = range * 0.1;
        if (range < 1000) {
            range = 1000;
            padding = 100;
        }

        yAxis.setLowerBound(Math.min(0, minValue - padding));
        yAxis.setUpperBound(maxValue + padding);
        yAxis.setTickUnit(range / 10);

        System.out.println("Y-axis range set to: " + yAxis.getLowerBound() + " to " + yAxis.getUpperBound());
    }

    public void updateChartForYear(int year) {
        // Update the tracked year
        currentDisplayYear = year;
        System.out.println("Updating chart for year: " + year);

        try {
            XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
            incomeSeries.setName("Income");

            XYChart.Series<String, Number> expensesSeries = new XYChart.Series<>();
            expensesSeries.setName("Expenses");

            XYChart.Series<String, Number> savingsSeries = new XYChart.Series<>();
            savingsSeries.setName("Savings");

            Map<Month, Double> incomeByMonth = getIncomeByMonth(year);
            Map<Month, Double> expensesByMonth = getExpensesByMonth(year);

            Set<Month> monthsWithData = new HashSet<>();
            monthsWithData.addAll(incomeByMonth.keySet());
            monthsWithData.addAll(expensesByMonth.keySet());

            System.out.println("Months with data for year " + year + ": " + monthsWithData);

            if (monthsWithData.isEmpty()) {
                lineChart.getData().clear();
                lineChart.setTitle("No data available for " + year);

                yAxis.setLowerBound(0);
                yAxis.setUpperBound(1000);
                yAxis.setTickUnit(100);

                xAxis.setCategories(FXCollections.observableArrayList());

                return;
            }

            final Month endMonth = (year == LocalDate.now().getYear())
                    ? LocalDate.now().getMonth()
                    : Month.DECEMBER;

            List<String> monthLabelsWithData = monthsWithData.stream()
                    .sorted()
                    .filter(month -> month.getValue() <= endMonth.getValue())
                    .map(month -> month.getDisplayName(TextStyle.SHORT, Locale.US))
                    .collect(Collectors.toList());

            xAxis.setCategories(FXCollections.observableArrayList(monthLabelsWithData));

            monthsWithData.stream()
                    .sorted()
                    .filter(month -> month.getValue() <= endMonth.getValue())
                    .forEach(month -> {
                        String monthLabel = month.getDisplayName(TextStyle.SHORT, Locale.US);
                        Double income = incomeByMonth.get(month);
                        Double expenses = expensesByMonth.get(month);

                        System.out.println("Processing month " + monthLabel +
                                ": Income=" + income + ", Expenses=" + expenses);

                        // Add income data point if it exists
                        if (income != null) {
                            incomeSeries.getData().add(new XYChart.Data<>(monthLabel, income));
                            System.out.println("Added income for " + monthLabel + ": " + income);
                        }

                        // Add expenses data point if it exists
                        if (expenses != null) {
                            expensesSeries.getData().add(new XYChart.Data<>(monthLabel, expenses));
                            System.out.println("Added expenses for " + monthLabel + ": " + expenses);
                        }

                        // Calculate and add savings
                        double incomeValue = (income != null) ? income : 0.0;
                        double expensesValue = (expenses != null) ? expenses : 0.0;
                        double savings = incomeValue - expensesValue;

                        savingsSeries.getData().add(new XYChart.Data<>(monthLabel, savings));
                        System.out.println("Added savings for " + monthLabel + ": " + savings);
                    });

            // Clear existing data before adding new
            lineChart.getData().clear();

            // Add all series that have data
            if (!incomeSeries.getData().isEmpty()) {
                lineChart.getData().add(incomeSeries);
                System.out.println("Added income series with " + incomeSeries.getData().size() + " data points");
            }

            if (!expensesSeries.getData().isEmpty()) {
                lineChart.getData().add(expensesSeries);
                System.out.println("Added expenses series with " + expensesSeries.getData().size() + " data points");
            }

            if (!savingsSeries.getData().isEmpty()) {
                lineChart.getData().add(savingsSeries);
                System.out.println("Added savings series with " + savingsSeries.getData().size() + " data points");
            }

            // Set chart title and finalize display
            lineChart.setTitle("Monthly Income vs Expenses (" + year + ")");
            adjustYAxisScale();
            addTooltips();
            System.out.println("Chart updated successfully for year " + year);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error: " + e.getMessage());

            // Clear the chart on error
            lineChart.getData().clear();
            lineChart.setTitle("Error loading data: " + e.getMessage());
        }
    }
}