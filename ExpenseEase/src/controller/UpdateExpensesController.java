package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import controller.DatabaseConnector;

public class UpdateExpensesController implements Initializable {

    @FXML
    private ComboBox<String> monthSelector;
    @FXML
    private TextField billsField;
    @FXML
    private TextField subscriptionsField;
    @FXML
    private TextField entertainmentField;
    @FXML
    private TextField foodField;
    @FXML
    private TextField groceriesField;
    @FXML
    private TextField healthField;
    @FXML
    private TextField shoppingField;
    @FXML
    private TextField travelField;
    @FXML
    private TextField otherField;
    @FXML
    private TextField incomeField;
    @FXML
    private Button updateButton;

    private int selectedMonthNumber;
    private int currentYear;

    // Create an instance of sceneController for navigation
    private sceneController sceneNav = new sceneController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set numeric value handlers for all text fields
        setupNumericOnlyFields();

        // Populate month selector with available months
        populateMonthSelector();

        // Add listener to month selector to load data when changed
        monthSelector.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        loadMonthData(newValue);
                    }
                });
    }

    private void setupNumericOnlyFields() {
        // Add listeners to ensure only numeric values are entered
        TextField[] fields = { billsField, subscriptionsField, entertainmentField,
                foodField, groceriesField, healthField, shoppingField,
                travelField, otherField, incomeField };

        for (TextField field : fields) {
            if (field != null) {
                field.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && !newValue.isEmpty()) {
                        // Only allow digits and decimal point
                        if (!newValue.matches("\\d*(\\.\\d*)?")) {
                            field.setText(oldValue);
                        }
                    }
                });
            }
        }
    }

    private void populateMonthSelector() {
        ObservableList<String> availableMonths = FXCollections.observableArrayList();
        currentYear = LocalDate.now().getYear();

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT DISTINCT MONTH(date) as month FROM transactions WHERE YEAR(date) = ? ORDER BY month";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, currentYear);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int monthNum = rs.getInt("month");
                    Month month = Month.of(monthNum);
                    String monthName = month.getDisplayName(java.time.format.TextStyle.FULL,
                            java.util.Locale.getDefault());
                    availableMonths.add(monthName);
                }
            }

            monthSelector.setItems(availableMonths);

            // If no months available yet, disable update button
            if (availableMonths.isEmpty()) {
                updateButton.setDisable(true);
                showAlert(AlertType.INFORMATION, "No Data",
                        "No transaction data found for the current year. Please add transactions first.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error",
                    "Failed to load available months: " + e.getMessage());
        }
    }

    private void loadMonthData(String monthName) {
        try {
            // Disable the update button during loading to prevent multiple clicks
            updateButton.setDisable(true);

            // Convert month name to month number safely
            int monthNumber = -1;
            for (Month m : Month.values()) {
                if (m.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault())
                        .equalsIgnoreCase(monthName)) {
                    monthNumber = m.getValue();
                    break;
                }
            }

            // If we couldn't find a match, show error and return
            if (monthNumber == -1) {
                System.err.println("Could not parse month name: " + monthName);
                showAlert(AlertType.ERROR, "Error", "Could not determine month from selection");
                return;
            }

            selectedMonthNumber = monthNumber;
            System.out.println("Selected month: " + monthName + " (month #" + selectedMonthNumber + ")");

            // Store current values to restore in case of error
            String[] currentValues = new String[10];
            TextField[] fields = { billsField, subscriptionsField, entertainmentField,
                    foodField, groceriesField, healthField, shoppingField,
                    travelField, otherField, incomeField };

            for (int i = 0; i < fields.length; i++) {
                currentValues[i] = fields[i].getText();
            }

            try (Connection conn = DatabaseConnector.getConnection()) {
                if (conn == null) {
                    // Re-enable update button if there was data before
                    updateButton.setDisable(false);
                    throw new SQLException("Could not connect to database");
                }

                String sql = "SELECT " +
                        "SUM(bills) as bills, " +
                        "SUM(subscriptions) as subscriptions, " +
                        "SUM(entertainment) as entertainment, " +
                        "SUM(food) as food, " +
                        "SUM(groceries) as groceries, " +
                        "SUM(health) as health, " +
                        "SUM(shopping) as shopping, " +
                        "SUM(travel) as travel, " +
                        "SUM(other) as other, " +
                        "SUM(income) as income " +
                        "FROM transactions " +
                        "WHERE YEAR(date) = ? AND MONTH(date) = ?";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, currentYear);
                    stmt.setInt(2, selectedMonthNumber);

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        // Populate the text fields with the existing values
                        billsField.setText(String.format("%.2f", rs.getDouble("bills")));
                        subscriptionsField.setText(String.format("%.2f", rs.getDouble("subscriptions")));
                        entertainmentField.setText(String.format("%.2f", rs.getDouble("entertainment")));
                        foodField.setText(String.format("%.2f", rs.getDouble("food")));
                        groceriesField.setText(String.format("%.2f", rs.getDouble("groceries")));
                        healthField.setText(String.format("%.2f", rs.getDouble("health")));
                        shoppingField.setText(String.format("%.2f", rs.getDouble("shopping")));
                        travelField.setText(String.format("%.2f", rs.getDouble("travel")));
                        otherField.setText(String.format("%.2f", rs.getDouble("other")));
                        incomeField.setText(String.format("%.2f", rs.getDouble("income")));

                        // Enable the update button
                        updateButton.setDisable(false);
                    } else {
                        // Don't clear fields, just show message
                        showAlert(AlertType.INFORMATION, "No Data",
                                "No transaction data found for " + monthName);

                        // Restore previous values if any
                        for (int i = 0; i < fields.length; i++) {
                            fields[i].setText(currentValues[i]);
                        }

                        // Only disable update button if there was no previous data
                        boolean hasData = false;
                        for (String value : currentValues) {
                            if (value != null && !value.trim().isEmpty()) {
                                hasData = true;
                                break;
                            }
                        }
                        updateButton.setDisable(!hasData);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Database Error",
                        "Failed to load transaction data: " + e.getMessage());

                // Restore previous values if any
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setText(currentValues[i]);
                }

                // Re-enable update button if there was data before
                updateButton.setDisable(false);
            }
        } catch (Exception e) {
            // Catch-all to prevent UI from breaking
            e.printStackTrace();
            System.err.println("Error in loadMonthData: " + e.getMessage());
            showAlert(AlertType.ERROR, "Error",
                    "An error occurred while loading month data: " + e.getMessage());

            // Don't clear fields here - this might be causing the background clearing
            updateButton.setDisable(false);
        }
    }

    @FXML
    public void handleUpdateExpenses(ActionEvent event) {
        if (monthSelector.getValue() == null) {
            showAlert(AlertType.WARNING, "Selection Required", "Please select a month to update.");
            return;
        }

        try {
            // Get values from fields
            double bills = getValueFromField(billsField);
            double subscriptions = getValueFromField(subscriptionsField);
            double entertainment = getValueFromField(entertainmentField);
            double food = getValueFromField(foodField);
            double groceries = getValueFromField(groceriesField);
            double health = getValueFromField(healthField);
            double shopping = getValueFromField(shoppingField);
            double travel = getValueFromField(travelField);
            double other = getValueFromField(otherField);
            double income = getValueFromField(incomeField);

            // Update the database
            if (updateTransactions(bills, subscriptions, entertainment, food, groceries,
                    health, shopping, travel, other, income)) {
                showAlert(AlertType.INFORMATION, "Success", "Transactions updated successfully!");

                // Refresh charts to show the updated data
                refreshCharts();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to update transactions: " + e.getMessage());
        }
    }

    private boolean updateTransactions(double bills, double subscriptions, double entertainment,
            double food, double groceries, double health,
            double shopping, double travel, double other, double income) {

        String sql = "UPDATE transactions SET " +
                "bills = ?, " +
                "subscriptions = ?, " +
                "entertainment = ?, " +
                "food = ?, " +
                "groceries = ?, " +
                "health = ?, " +
                "shopping = ?, " +
                "travel = ?, " +
                "other = ?, " +
                "income = ? " +
                "WHERE YEAR(date) = ? AND MONTH(date) = ?";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, bills);
            stmt.setDouble(2, subscriptions);
            stmt.setDouble(3, entertainment);
            stmt.setDouble(4, food);
            stmt.setDouble(5, groceries);
            stmt.setDouble(6, health);
            stmt.setDouble(7, shopping);
            stmt.setDouble(8, travel);
            stmt.setDouble(9, other);
            stmt.setDouble(10, income);
            stmt.setInt(11, currentYear);
            stmt.setInt(12, selectedMonthNumber);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", e.getMessage());
            return false;
        }
    }

    private double getValueFromField(TextField field) {
        if (field != null && field.getText() != null && !field.getText().isEmpty()) {
            try {
                return Double.parseDouble(field.getText().trim());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    private void clearFields() {
        TextField[] fields = { billsField, subscriptionsField, entertainmentField,
                foodField, groceriesField, healthField, shoppingField,
                travelField, otherField, incomeField };

        for (TextField field : fields) {
            if (field != null) {
                field.clear();
            }
        }
    }

    // New method to refresh charts
    private void refreshCharts() {
        try {
            FXMLLoader homeLoader = sceneController.getHomeLoader();
            if (homeLoader != null) {
                HomeController homeController = homeLoader.getController();
                if (homeController != null) {
                    // Call the home controller method to refresh charts
                    homeController.refreshCharts();
                    System.out.println("Charts refreshed after updating transactions");
                }
            }
        } catch (Exception e) {
            // Just log the error - don't stop the update process
            System.err.println("Error refreshing charts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation methods
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
        // Already on this screen
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