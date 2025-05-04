package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import controller.TransactionSummary;
import controller.DatabaseConnector;

public class ViewTransactionsController implements Initializable {

    @FXML
    private TableView<TransactionSummary> transactionsTable;
    @FXML
    private TableColumn<TransactionSummary, String> monthColumn;
    @FXML
    private TableColumn<TransactionSummary, Double> billsColumn;
    @FXML
    private TableColumn<TransactionSummary, Double> subscriptionsColumn;
    @FXML
    private TableColumn<TransactionSummary, Double> entertainmentColumn;
    @FXML
    private TableColumn<TransactionSummary, Double> foodDrinkColumn;
    @FXML
    private TableColumn<TransactionSummary, Double> groceriesColumn;
    @FXML
    private TableColumn<TransactionSummary, Double> healthColumn;
    @FXML
    private TableColumn<TransactionSummary, Double> shoppingColumn;
    @FXML
    private TableColumn<TransactionSummary, Double> travelColumn;
    @FXML
    private TableColumn<TransactionSummary, Double> otherColumn;
    @FXML
    private TableColumn<TransactionSummary, Double> totalExpenditureColumn;
    @FXML
    private TableColumn<TransactionSummary, Double> incomeColumn;
    @FXML
    private TableColumn<TransactionSummary, Double> grossSavingsColumn;

    // Create an instance of sceneController for navigation
    private sceneController sceneNav = new sceneController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up column mappings to the TransactionSummary properties
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        billsColumn.setCellValueFactory(new PropertyValueFactory<>("bills"));
        subscriptionsColumn.setCellValueFactory(new PropertyValueFactory<>("subscriptions"));
        entertainmentColumn.setCellValueFactory(new PropertyValueFactory<>("entertainment"));
        foodDrinkColumn.setCellValueFactory(new PropertyValueFactory<>("foodDrink"));
        groceriesColumn.setCellValueFactory(new PropertyValueFactory<>("groceries"));
        healthColumn.setCellValueFactory(new PropertyValueFactory<>("health"));
        shoppingColumn.setCellValueFactory(new PropertyValueFactory<>("shopping"));
        travelColumn.setCellValueFactory(new PropertyValueFactory<>("travel"));
        otherColumn.setCellValueFactory(new PropertyValueFactory<>("other"));
        totalExpenditureColumn.setCellValueFactory(new PropertyValueFactory<>("totalExpenditure"));
        incomeColumn.setCellValueFactory(new PropertyValueFactory<>("income"));
        grossSavingsColumn.setCellValueFactory(new PropertyValueFactory<>("grossSavings"));

        // Format the numeric columns to show 2 decimal places
        formatCurrencyColumns();

        // Load transaction data
        loadTransactionData();
    }

    private void formatCurrencyColumns() {
        // You can customize cell factories to format as currency if needed
        // This is optional and can be implemented later
    }

    public void loadTransactionData() {
        ObservableList<TransactionSummary> summaryList = FXCollections.observableArrayList();

        // Get the current year
        int currentYear = LocalDate.now().getYear();

        try (Connection conn = DatabaseConnector.getConnection()) {
            // SQL to get monthly summary of all transactions
            String sql = "SELECT " +
                    "MONTH(date) as month, " +
                    "SUM(bills) as bills, " +
                    "SUM(subscriptions) as subscriptions, " +
                    "SUM(entertainment) as entertainment, " +
                    "SUM(food) as food_drink, " +
                    "SUM(groceries) as groceries, " +
                    "SUM(health) as health, " +
                    "SUM(shopping) as shopping, " +
                    "SUM(travel) as travel, " +
                    "SUM(other) as other, " +
                    "SUM(income) as income " +
                    "FROM transactions " +
                    "WHERE YEAR(date) = ? " +
                    "GROUP BY MONTH(date) " +
                    "ORDER BY MONTH(date)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, currentYear);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int monthNum = rs.getInt("month");
                    double bills = rs.getDouble("bills");
                    double subscriptions = rs.getDouble("subscriptions");
                    double entertainment = rs.getDouble("entertainment");
                    double foodDrink = rs.getDouble("food_drink");
                    double groceries = rs.getDouble("groceries");
                    double health = rs.getDouble("health");
                    double shopping = rs.getDouble("shopping");
                    double travel = rs.getDouble("travel");
                    double other = rs.getDouble("other");
                    double income = rs.getDouble("income");

                    // Calculate total expenditure
                    double totalExpenditure = bills + subscriptions + entertainment + foodDrink +
                            groceries + health + shopping + travel + other;

                    // Calculate gross savings (income - total expenditure)
                    double grossSavings = income - totalExpenditure;

                    // Format the month name
                    YearMonth yearMonth = YearMonth.of(currentYear, monthNum);
                    String monthName = yearMonth.format(DateTimeFormatter.ofPattern("MMM"));

                    // Create a transaction summary and add it to the list
                    TransactionSummary summary = new TransactionSummary(
                            monthName, bills, subscriptions, entertainment,
                            foodDrink, groceries, health, shopping,
                            travel, other, totalExpenditure, income, grossSavings);

                    summaryList.add(summary);
                }
            }

            // Set the data to the table view
            transactionsTable.setItems(summaryList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error",
                    "Failed to load transaction data: " + e.getMessage());
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
        // Already on this screen
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

    // Method to refresh the table data (can be called after updating records)
    public void refreshData() {
        loadTransactionData();
    }
}