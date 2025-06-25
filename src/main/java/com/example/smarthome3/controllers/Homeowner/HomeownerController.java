package com.example.smarthome3.controllers.Homeowner;

import com.example.smarthome3.Database.DatabaseConnector;
import com.example.smarthome3.Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;



import javafx.scene.control.Label;

import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HomeownerController implements Initializable {

    @FXML
    private BorderPane homeowner_parent;
    @FXML
    private Text user_name;
    @FXML
    private Label dateTimeLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing HomeownerController...");

        String name = Model.getInstance().getViewFactory().getLoggedInUser();
        if (user_name != null && name != null) {
            user_name.setText("Hi, " + name + " 👋");
        }



        if (dateTimeLabel != null) {
            LocalDateTime now = LocalDateTime.now();

            String daySuffix = getDayOfMonthSuffix(now.getDayOfMonth());
            DateTimeFormatter baseFormatter = DateTimeFormatter.ofPattern("h:mm a | EEEE, d 'of' MMMM, yyyy");
            String formattedDate = now.format(baseFormatter);

            int day = now.getDayOfMonth();
            formattedDate = formattedDate.replaceFirst("\\b" + day + "\\b", day + daySuffix);

            dateTimeLabel.setText(formattedDate);
        }


        // Menu Switching Listener
        Model.getInstance().getViewFactory().getHomeownerSelectedMenuItem().addListener((obs, oldVal, newVal) -> {
            Node view = switch (newVal) {
                case "Humidity" -> Model.getInstance().getViewFactory().getHumidityView();
                case "Temperature" -> Model.getInstance().getViewFactory().getTemperatureView();
                case "Motion" -> Model.getInstance().getViewFactory().getMotionView();
                case "Light" -> {
                    try {
                        yield Model.getInstance().getViewFactory().getLightView();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                default -> Model.getInstance().getViewFactory().getDashboardView();
            };

            if (homeowner_parent != null && view != null) {
                homeowner_parent.setCenter(view);
            }
        });
    }

    // Helper method to get suffix for day number (st, nd, rd, th)
    private String getDayOfMonthSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

}