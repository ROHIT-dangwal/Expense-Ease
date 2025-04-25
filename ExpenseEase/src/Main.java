import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.DatabaseConnector;
import controller.HomeController;
import controller.sceneController;
import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Get the URL for the home.fxml file
            URL location = getClass().getResource("fxml/home.fxml");
            if (location == null) {
                throw new IOException("Cannot find home.fxml file");
            }

            // Create the FXMLLoader
            FXMLLoader loader = new FXMLLoader(location);

            // Load the FXML file
            Parent root = loader.load();

            // Store the loader in sceneController for later use
            sceneController.storeHomeLoader(loader);

            // Get the controller instance
            HomeController homeController = loader.getController();

            // Set minimum size constraints - the window cannot be resized smaller than this
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(600);

            primaryStage.setTitle("ExpenseEase");

            Scene scene = new Scene(root, 1500, 800);

            // Add CSS stylesheet
            String cssPath = "styles/home.css";
            URL cssUrl = getClass().getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("CSS file not found: " + cssPath);
            }

            primaryStage.setScene(scene);

            // Set to full screen mode but allow esc key to exit full screen
            // primaryStage.setFullScreen(true);
            // primaryStage.setFullScreenExitHint("Press ESC to exit full screen mode");

            // Alternative: Use maximized (fills screen but keeps window decorations)
            primaryStage.setMaximized(true);

            primaryStage.show();

            // Initialize database connection
            DatabaseConnector.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting application: " + e.getMessage());
            throw e;
        }
    }
}