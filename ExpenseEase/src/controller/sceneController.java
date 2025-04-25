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

    // Store loaders for each screen to enable controller communication
    private static FXMLLoader homeLoader;
    private static FXMLLoader addLoader;
    private static FXMLLoader viewLoader;
    private static FXMLLoader updateLoader;
    private static FXMLLoader downloadLoader;

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

            FXMLLoader loader = new FXMLLoader(location);
            root = loader.load();

            // Store the loader for the corresponding screen
            storeLoader(fxmlFile, loader);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);

            // Add CSS stylesheet
            String cssPath = "/styles/" + fxmlFile.replace(".fxml", ".css");
            URL cssUrl = getClass().getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            // Save current fullscreen state
            boolean wasFullScreen = stage.isFullScreen();

            stage.setScene(scene);

            // Restore fullscreen state
            stage.setFullScreen(wasFullScreen);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading " + fxmlFile + ": " + e.getMessage());
        }
    }

    /**
     * Store the loader for each screen type
     */
    private void storeLoader(String fxmlFile, FXMLLoader loader) {
        if (fxmlFile.equals("home.fxml")) {
            homeLoader = loader;
        } else if (fxmlFile.equals("add.fxml")) {
            addLoader = loader;
        } else if (fxmlFile.equals("view.fxml")) {
            viewLoader = loader;
        } else if (fxmlFile.equals("update.fxml")) {
            updateLoader = loader;
        } else if (fxmlFile.equals("download.fxml")) {
            downloadLoader = loader;
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

    public static void storeHomeLoader(FXMLLoader loader) {
        homeLoader = loader;
    }

    // Getter methods for loaders
    public static FXMLLoader getHomeLoader() {
        return homeLoader;
    }

    public static FXMLLoader getAddLoader() {
        return addLoader;
    }

    public static FXMLLoader getViewLoader() {
        return viewLoader;
    }

    public static FXMLLoader getUpdateLoader() {
        return updateLoader;
    }

    public static FXMLLoader getDownloadLoader() {
        return downloadLoader;
    }
}