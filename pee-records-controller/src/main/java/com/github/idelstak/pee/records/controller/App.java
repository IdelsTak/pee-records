/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller;

import com.github.idelstak.pee.records.controller.patient.PatientViewController;
import com.github.idelstak.pee.records.controller.login.LoginAttempt;
import com.github.idelstak.pee.records.model.spi.Patient;
import com.github.idelstak.pee.records.view.api.FxmlParent;
import com.github.idelstak.pee.records.view.patient.PatientViewFxml;
import java.util.Optional;
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

        Optional<Patient> user = attempt.getUser();

        user.ifPresent(username -> {
            FxmlParent fxmlParent = new FxmlParent(new PatientViewFxml(), new PatientViewController(attempt.getConnectedDataSource(), username));
            scene.setRoot(fxmlParent.get());
            primaryStage.setMaximized(true);
        });

        primaryStage.setTitle("Pee Calendar");
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
