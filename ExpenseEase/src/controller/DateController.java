package controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class DateController implements Initializable {

    @FXML
    private Label dateLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateDateLabel();
    }

    private void updateDateLabel() {
        if (dateLabel != null) {
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            dateLabel.setText(currentDate.format(formatter));
        }
    }
}