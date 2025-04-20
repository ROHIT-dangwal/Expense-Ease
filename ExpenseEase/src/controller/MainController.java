package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainController {

    @FXML
    private void initialize() {
        System.out.println("Controller initialized");
    }

    @FXML
    private void handleAddExpense(ActionEvent event) {
        System.out.println("Add Expense button clicked");
    }

    @FXML
    private void handleViewExpenses(ActionEvent event) {
        System.out.println("View Expenses button clicked");
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Platform.exit();
    }
}