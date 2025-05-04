
package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class downloadController {

    @FXML
    private ComboBox<String> tableSelector;

    @FXML
    private Label statusLabel;

    @FXML
    private Button downloadButton;
    private sceneController sceneNav = new sceneController();

    @FXML
    public void initialize() {
        ObservableList<String> options = FXCollections.observableArrayList(
                "transactions",
                "Monthly Summary",
                "Category Report");
        tableSelector.setItems(options);
        tableSelector.setValue("transactions");
        statusLabel.setText("Select data type and click download");
    }

    @FXML
    public void handleDownload(ActionEvent event) {
        String selectedOption = tableSelector.getValue();
        if (selectedOption == null) {
            statusLabel.setText("Please select a data type first");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save " + selectedOption + " as CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String defaultFileName = "expenseease_" + selectedOption + "_" + currentDate.format(formatter) + ".csv";
        fileChooser.setInitialFileName(defaultFileName);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            switch (selectedOption) {
                case "transactions":
                    exportAllDataToCSV(file);
                    break;
                case "Monthly Summary":
                    exportMonthlySummary(file);
                    break;
                case "Category Report":
                    exportCategoryReport(file);
                    break;
                default:
                    exportAllDataToCSV(file);
            }
        }
    }

    private void exportAllDataToCSV(File file) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            if (conn == null) {
                statusLabel.setText("Error: Could not connect to database");
                return;
            }

            try (FileWriter csvWriter = new FileWriter(file)) {
                String query = "SELECT t.id, t.date, t.income, " +
                        "t.bills, t.subscriptions, t.entertainment, " +
                        "t.food, t.groceries, t.health, " +
                        "t.shopping, t.travel, t.other, " +
                        "(t.bills + t.subscriptions + t.entertainment + " +
                        "t.food + t.groceries + t.health + " +
                        "t.shopping + t.travel + t.other) AS total_expenses, " +
                        "(t.income - (t.bills + t.subscriptions + t.entertainment + " +
                        "t.food + t.groceries + t.health + " +
                        "t.shopping + t.travel + t.other)) AS savings " +
                        "FROM transactions t " +
                        "ORDER BY t.date DESC";

                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query)) {

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(metaData.getColumnName(i));
                        if (i < columnCount)
                            csvWriter.append(",");
                    }
                    csvWriter.append("\n");
                    int rowCount = 0;
                    while (rs.next()) {
                        rowCount++;
                        for (int i = 1; i <= columnCount; i++) {
                            String value = rs.getString(i);
                            if (value == null)
                                value = "";
                            if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                                value = "\"" + value.replace("\"", "\"\"") + "\"";
                            }
                            csvWriter.append(value);
                            if (i < columnCount)
                                csvWriter.append(",");
                        }
                        csvWriter.append("\n");
                    }

                    csvWriter.flush();
                    statusLabel.setText(
                            "Successfully exported " + rowCount + " transactions to " + file.getName());
                }
            }
        } catch (SQLException | IOException ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void exportMonthlySummary(File file) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            if (conn == null) {
                statusLabel.setText("Error: Could not connect to database");
                return;
            }

            try (FileWriter csvWriter = new FileWriter(file)) {
                // Query to get monthly summaries
                String query = "SELECT " +
                        "YEAR(date) as year, " +
                        "MONTH(date) as month, " +
                        "MONTHNAME(date) as month_name, " +
                        "SUM(income) as total_income, " +
                        "SUM(bills + subscriptions + entertainment + food + groceries + " +
                        "health + shopping + travel + other) as total_expenses, " +
                        "SUM(income - (bills + subscriptions + entertainment + food + groceries + " +
                        "health + shopping + travel + other)) as total_savings " +
                        "FROM transactions " +
                        "GROUP BY YEAR(date), MONTH(date), MONTHNAME(date) " +
                        "ORDER BY YEAR(date) DESC, MONTH(date) DESC";

                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query)) {

                    csvWriter.append("Year,Month,Month Name,Total Income,Total Expenses,Total Savings\n");

                    int rowCount = 0;
                    while (rs.next()) {
                        rowCount++;
                        int year = rs.getInt("year");
                        int month = rs.getInt("month");
                        String monthName = rs.getString("month_name");
                        double income = rs.getDouble("total_income");
                        double expenses = rs.getDouble("total_expenses");
                        double savings = rs.getDouble("total_savings");

                        csvWriter.append(String.valueOf(year)).append(",");
                        csvWriter.append(String.valueOf(month)).append(",");
                        csvWriter.append(monthName).append(",");
                        csvWriter.append(String.format("%.2f", income)).append(",");
                        csvWriter.append(String.format("%.2f", expenses)).append(",");
                        csvWriter.append(String.format("%.2f", savings)).append("\n");
                    }

                    csvWriter.flush();
                    statusLabel.setText(
                            "Successfully exported " + rowCount + " monthly summaries to " + file.getName());
                }
            }
        } catch (SQLException | IOException ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void exportCategoryReport(File file) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            if (conn == null) {
                statusLabel.setText("Error: Could not connect to database");
                return;
            }

            try (FileWriter csvWriter = new FileWriter(file)) {
                String query = "SELECT " +
                        "SUM(bills) as total_bills, " +
                        "SUM(subscriptions) as total_subscriptions, " +
                        "SUM(entertainment) as total_entertainment, " +
                        "SUM(food) as total_food, " +
                        "SUM(groceries) as total_groceries, " +
                        "SUM(health) as total_health, " +
                        "SUM(shopping) as total_shopping, " +
                        "SUM(travel) as total_travel, " +
                        "SUM(other) as total_other, " +
                        "SUM(bills + subscriptions + entertainment + food + groceries + " +
                        "health + shopping + travel + other) as grand_total " +
                        "FROM transactions";

                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query)) {

                    if (rs.next()) {
                        csvWriter.append("Expense Category,Total Amount,Percentage\n");

                        double bills = rs.getDouble("total_bills");
                        double subscriptions = rs.getDouble("total_subscriptions");
                        double entertainment = rs.getDouble("total_entertainment");
                        double food = rs.getDouble("total_food");
                        double groceries = rs.getDouble("total_groceries");
                        double health = rs.getDouble("total_health");
                        double shopping = rs.getDouble("total_shopping");
                        double travel = rs.getDouble("total_travel");
                        double other = rs.getDouble("total_other");
                        double grandTotal = rs.getDouble("grand_total");

                        writeCategory(csvWriter, "Bills", bills, grandTotal);
                        writeCategory(csvWriter, "Subscriptions", subscriptions, grandTotal);
                        writeCategory(csvWriter, "Entertainment", entertainment, grandTotal);
                        writeCategory(csvWriter, "Food", food, grandTotal);
                        writeCategory(csvWriter, "Groceries", groceries, grandTotal);
                        writeCategory(csvWriter, "Health", health, grandTotal);
                        writeCategory(csvWriter, "Shopping", shopping, grandTotal);
                        writeCategory(csvWriter, "Travel", travel, grandTotal);
                        writeCategory(csvWriter, "Other", other, grandTotal);

                        csvWriter.append("TOTAL,").append(String.format("%.2f", grandTotal)).append(",100.00%\n");

                        csvWriter.flush();
                        statusLabel.setText("Successfully exported expense categories to " + file.getName());
                    } else {
                        statusLabel.setText("No expense data available to export");
                    }
                }
            }
        } catch (SQLException | IOException ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void writeCategory(FileWriter writer, String category, double amount, double total) throws IOException {
        double percentage = (total > 0) ? (amount / total * 100) : 0;
        writer.append(category).append(",");
        writer.append(String.format("%.2f", amount)).append(",");
        writer.append(String.format("%.2f%%", percentage)).append("\n");
    }

    @FXML
    public void navigateToHome(ActionEvent event) {
        sceneNav.navigateToHome(event);
    }

    @FXML
    public void navigateToAddExpense(ActionEvent event) {
        sceneNav.navigateToAddExpense(event);
    }

    @FXML
    public void navigateToViewExpenses(ActionEvent event) {
        sceneNav.navigateToViewExpenses(event);
    }

    @FXML
    public void navigateToUpdate(ActionEvent event) {
        sceneNav.navigateToUpdate(event);
    }

    @FXML
    public void navigateToDownload(ActionEvent event) {
        sceneNav.navigateToDownload(event);
    }

    @FXML
    public void exitApplication() {
        System.exit(0);
    }
}