package controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.net.URL;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import java.io.IOException;

public class sceneController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    /**
     * Generic method to switch between scenes
     * 
     * @param event    The action event that triggered the scene change
     * @param fxmlFile The name of the FXML file to load
     */
    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            URL location = getClass().getResource("/fxml/" + fxmlFile);
            if (location == null) {
                throw new IOException("Cannot find FXML file: " + fxmlFile);
            }

            root = FXMLLoader.load(location);
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading " + fxmlFile + ": " + e.getMessage());
        }
    }

    /**
     * Navigate to the home screen
     */
    public void navigateToHome(ActionEvent event) {
        System.out.println("Navigating to Home screen");
        switchScene(event, "home.fxml");
    }

    /**
     * Navigate to the add expense screen
     */
    public void navigateToAddExpense(ActionEvent event) {
        System.out.println("Navigating to Add Expense screen");
        switchScene(event, "add.fxml");
    }

    /**
     * Navigate to the view expenses screen
     */
    public void navigateToViewExpenses(ActionEvent event) {
        System.out.println("Navigating to View Expenses screen");
        switchScene(event, "view.fxml");
    }

    public void navigateToDownload(ActionEvent event) {
        System.out.println("Navigating to Download/Export screen");
        switchScene(event, "download.fxml");
    }

    /**
     * Navigate to the update/settings screen
     */
    public void navigateToUpdate(ActionEvent event) {
        System.out.println("Navigating to Update/Settings screen");
        switchScene(event, "update.fxml");
    }

    public void exitApplication(ActionEvent event) {
        System.out.println("Exiting application");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}