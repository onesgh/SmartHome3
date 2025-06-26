package com.example.smarthome3.controllers.Homeowner;

import com.example.smarthome3.Database.DatabaseConnector;
import com.example.smarthome3.Database.User;
import com.example.smarthome3.Database.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AlertController implements Initializable {

    @FXML public Label dateTimeLabel;
    @FXML public Text user_name;
    @FXML public VBox messageContainer;
    @FXML public VBox Container1;
    @FXML public VBox Container2;
    @FXML public VBox Container3;

    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }

        updateDateTime();
        loadRecentAlerts();
    }

    private void updateDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a | EEEE, d 'of' MMMM, yyyy");
        String formattedDate = java.time.LocalDateTime.now().format(formatter);
        dateTimeLabel.setText(formattedDate);

        User user = UserSession.getInstance().getUser();
        if (user != null) {
            user_name.setText("Hi, " + user.getName() + " 👋");
        }
    }

    private void loadRecentAlerts() {
        User user = UserSession.getInstance().getUser();
        if (user == null || connection == null) return;

        String query = """
            SELECT a.timestamp, a.message
            FROM Alert a
            JOIN Home h ON a.HomeId = h.HomeId
            WHERE h.OwnerId = ?
            ORDER BY a.timestamp DESC
            LIMIT 3
        """;

        List<VBox> containers = List.of(Container1, Container2, Container3);

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, user.getID());
            ResultSet rs = stmt.executeQuery();

            int index = 0;
            while (rs.next() && index < containers.size()) {
                String timestamp = rs.getString("timestamp");
                String message = rs.getString("message");
                VBox alertBox = createAlertBox(timestamp, message);

                containers.get(index).getChildren().clear();
                containers.get(index).getChildren().add(alertBox);

                index++;
            }

            // Vider les conteneurs restants s’il y a moins de 3 alertes
            for (int i = index; i < containers.size(); i++) {
                containers.get(i).getChildren().clear();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createAlertBox(String timestamp, String message) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setPadding(new Insets(5));

        Label dateLabel = new Label(timestamp);
        dateLabel.setStyle("-fx-text-fill: #777; -fx-font-size: 13;");

        Text msgText = new Text(message);
        msgText.setWrappingWidth(500);
        msgText.setFont(Font.font("Arial", 14));
        msgText.setFill(Color.web("#3a2a0c"));
        msgText.setStyle("-fx-font-weight: bold;");

        box.getChildren().addAll(dateLabel, msgText);
        return box;
    }
}
