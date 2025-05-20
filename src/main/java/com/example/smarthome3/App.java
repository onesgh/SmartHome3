package com.example.smarthome3;
import java.sql.*;
import com.example.smarthome3.Models.Model;

import javafx.application.Application;
import javafx.scene.Scene;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class App extends Application {


    @Override
    public void start(Stage stage) {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
       // try {
           // Scene scene = new Scene(loader.load());
           // stage.setScene(scene);
           // stage.setTitle("Smart Home - Login");
           // stage.show();
       // } catch (Exception e) {
            //e.printStackTrace();

       // }
        Model.getInstance().getViewFactory().showSignUpWindow();

    }
}