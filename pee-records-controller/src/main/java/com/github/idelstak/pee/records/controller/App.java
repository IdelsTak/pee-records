/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller;

import com.github.idelstak.pee.records.controller.login.LoginAttempt;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class App extends Application {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new BorderPane(), Color.WHITE);
        //Check whether database connection can be established
        LoginAttempt attempt = new LoginAttempt();

        Optional<String> user = attempt.getUser();
        
        LOGGER.log(Level.INFO, "Logged in user: {0}", user);

        primaryStage.setTitle("Pee Records");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
