package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class HomeController implements Initializable {

    @FXML
    private AnchorPane lineChartPane;
    @FXML
    private AnchorPane pieChartPane;

    private LineChartController lineChartController;
    private PieChartController pieChartController;

    // Create an instance of sceneController for navigation
    private sceneController sceneNav = new sceneController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // This method will be called after the FXML is loaded
        setupChartControllers();
    }

    private void setupChartControllers() {
        try {
            if (lineChartPane != null) {
                // The FXML loader for LineChart.fxml will be loaded by fx:include
                lineChartController = (LineChartController) lineChartPane.getProperties().get("controller");
            }

            if (pieChartPane != null) {
                // The FXML loader for PieChart.fxml will be loaded by fx:include
                pieChartController = (PieChartController) pieChartPane.getProperties().get("controller");
            }
        } catch (Exception e) {
            System.err.println("Error getting chart controllers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshCharts() {
        if (lineChartController != null) {
            lineChartController.refreshChart();
            System.out.println("Line chart refresh requested");
        } else {
            System.out.println("Line chart controller is null");
        }

        if (pieChartController != null) {
            pieChartController.updateChartData();
        }
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
    public void exitApplication(ActionEvent event) {
        System.exit(0);
    }
}