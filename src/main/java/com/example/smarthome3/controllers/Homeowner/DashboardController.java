package com.example.smarthome3.controllers.Homeowner;

import com.example.smarthome3.Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Text user_name;

    @FXML
    private Label dateTime;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String name = Model.getInstance().getViewFactory().getLoggedInUser();
        if (user_name != null && name != null) {
            user_name.setText("Hi, " + name + " 👋");
        }

        LocalDateTime now = LocalDateTime.now();

        String daySuffix = getDayOfMonthSuffix(now.getDayOfMonth());
        DateTimeFormatter baseFormatter = DateTimeFormatter.ofPattern("h:mm a | EEEE, d 'of' MMMM, yyyy");
        String formattedDate = now.format(baseFormatter);

        int day = now.getDayOfMonth();
        formattedDate = formattedDate.replaceFirst("\\b" + day + "\\b", day + daySuffix);

        if (dateTime != null) {
            dateTime.setText(formattedDate);
        }
    }

    private String getDayOfMonthSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }
}

