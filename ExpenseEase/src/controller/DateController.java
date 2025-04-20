package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class DateController implements Initializable {

    @FXML
    private Label dateLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initial update
        updateDateTimeLabel();

        // Set up a timeline to update every second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateDateTimeLabel()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateDateTimeLabel() {
        if (dateLabel != null) {
            LocalDateTime current = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss");
            dateLabel.setText(current.format(formatter));
        }
    }
}