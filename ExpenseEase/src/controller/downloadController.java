
package controller;

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

    // Create an instance of sceneController for navigation
    private sceneController sceneNav = new sceneController();

    @FXML
    public void initialize() {
        // Initialize the table selector with available tables
        tableSelector.getItems().addAll("expenses", "income", "transactions");
        tableSelector.setValue("expenses"); // Default selection

        // Set initial status
        statusLabel.setText("Select a table and click download");
    }

    @FXML
    public void handleDownload(ActionEvent event) {
        String selectedTable = tableSelector.getValue();
        if (selectedTable == null || selectedTable.isEmpty()) {
            statusLabel.setText("Please select a table to download");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save " + selectedTable + " as CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // Set default filename with today's date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String defaultFileName = selectedTable + "_" + currentDate.format(formatter) + ".csv";
        fileChooser.setInitialFileName(defaultFileName);

        // Get the stage from the event source
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            exportTableToCSV(selectedTable, file);
        }
    }

    private void exportTableToCSV(String tableName, File file) {
        String jdbcURL = "jdbc:mysql://localhost:3306/expenseease";
        String username = "root"; // Update with your MySQL username
        String password = "test123"; // Update with your MySQL password

        try (
                Connection conn = DriverManager.getConnection(jdbcURL, username, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
                FileWriter csvWriter = new FileWriter(file)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Header
            for (int i = 1; i <= columnCount; i++) {
                csvWriter.append(metaData.getColumnName(i));
                if (i < columnCount)
                    csvWriter.append(",");
            }
            csvWriter.append("\n");

            // Rows
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    // Handle null values
                    if (value == null)
                        value = "";
                    // Escape commas and quotes
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
                    "Successfully exported " + rowCount + " records from " + tableName + " to " + file.getName());

        } catch (SQLException | IOException ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Navigation methods that delegate to sceneController
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