package controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import controller.DatabaseConnector;

public class addincomeController {

    @FXML
    private TextField incomeField;
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
    private Button addButton;
    private sceneController sceneNav = new sceneController();

    @FXML
    public void initialize() {
        setupNumericOnlyFields();
    }

    private void setupNumericOnlyFields() {
        TextField[] fields = { incomeField, billsField, subscriptionsField, entertainmentField,
                foodField, groceriesField, healthField, shoppingField, travelField, otherField };

        for (TextField field : fields) {
            if (field != null) {
                field.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && !newValue.isEmpty()) {
                        if (!newValue.matches("\\d*(\\.\\d*)?")) {
                            field.setText(oldValue);
                        }
                    }
                });
            }
        }
    }

    @FXML
    public void handleAddExpenses(ActionEvent event) {
        try {
            double income = getValueFromField(incomeField);
            double bills = getValueFromField(billsField);
            double subscriptions = getValueFromField(subscriptionsField);
            double entertainment = getValueFromField(entertainmentField);
            double food = getValueFromField(foodField);
            double groceries = getValueFromField(groceriesField);
            double health = getValueFromField(healthField);
            double shopping = getValueFromField(shoppingField);
            double travel = getValueFromField(travelField);
            double other = getValueFromField(otherField);

            LocalDate today = LocalDate.now();

            if (insertTransaction(today, income, bills, subscriptions, entertainment,
                    food, groceries, health, shopping, travel, other)) {
                showAlert(AlertType.INFORMATION, "Success", "Transaction added successfully!");
                clearFields();
                refreshCharts();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to add transaction: " + e.getMessage());
        }
    }

    private boolean insertTransaction(LocalDate date, double income, double bills, double subscriptions,
            double entertainment, double food, double groceries, double health,
            double shopping, double travel, double other) {
        String sql = "INSERT INTO transactions (date, income, bills, subscriptions, entertainment, " +
                "food, groceries, health, shopping, travel, other) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setDouble(2, income);
            stmt.setDouble(3, bills);
            stmt.setDouble(4, subscriptions);
            stmt.setDouble(5, entertainment);
            stmt.setDouble(6, food);
            stmt.setDouble(7, groceries);
            stmt.setDouble(8, health);
            stmt.setDouble(9, shopping);
            stmt.setDouble(10, travel);
            stmt.setDouble(11, other);

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
        TextField[] fields = { incomeField, billsField, subscriptionsField, entertainmentField,
                foodField, groceriesField, healthField, shoppingField, travelField, otherField };

        for (TextField field : fields) {
            if (field != null) {
                field.clear();
            }
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshCharts() {
        try {
            FXMLLoader homeLoader = sceneController.getHomeLoader();
            if (homeLoader != null) {
                HomeController homeController = homeLoader.getController();
                if (homeController != null) {
                    homeController.refreshCharts();
                }
            }
        } catch (Exception e) {
            System.err.println("Error refreshing charts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void navigateToHome(ActionEvent event) {
        sceneNav.navigateToHome(event);
    }

    @FXML
    public void navigateToAddExpense(ActionEvent event) {
        // Already on this screen
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