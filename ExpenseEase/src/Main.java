import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL location = getClass().getResource("fxml/home.fxml");
        Parent root = FXMLLoader.load(location);

        // Set minimum size constraints - the window cannot be resized smaller than this
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);

        primaryStage.setTitle("ExpenseEase");

        Scene scene = new Scene(root, 1200, 800);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}