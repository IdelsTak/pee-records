/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller.login;

import com.github.idelstak.pee.records.controller.database.ConnectionPreferences;
import com.github.idelstak.pee.records.controller.database.DatabaseConnectionController;
import com.github.idelstak.pee.records.dao.api.InitializeTables;
import com.github.idelstak.pee.records.database.DatabaseAccess;
import com.github.idelstak.pee.records.model.spi.Patient;
import com.github.idelstak.pee.records.view.api.FxmlParent;
import com.github.idelstak.pee.records.view.database.DatabaseConnectionFxml;
import com.github.idelstak.pee.records.view.login.LoginFxml;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javax.sql.DataSource;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class LoginAttempt {

    private Optional<Patient> loggedInUser = Optional.empty();
    private DataSource dataSource;

    public LoginAttempt() {
    }

    public Optional<Patient> getUser() {

        ConnectionPreferences preferences = new ConnectionPreferences();

        String user = preferences.getUsername();
        char[] password = preferences.getPassword();
        String url = preferences.getUrl();

        DatabaseAccess databaseAccess = new DatabaseAccess(user, password, url);

        //Attempting to access the database will throw an error if
        //(1) the server has not been started
        //(2) the url points to a non-existent database
        //(3) the database login credentials are not correct
        try {
            dataSource = databaseAccess.getDataSource();

            try ( Connection conn = dataSource.getConnection()) {
                //Initialize the database tables
                //Create them if they don't exist
                //Insert at least one admin
                InitializeTables initializeTables = new InitializeTables(dataSource);

                initializeTables.start();
                //After tables have been initialized
                //show the login dialog
                showLoginDialog(dataSource);
            }
        } catch (IOException | SQLException e) {
            showConnectionError(preferences, e.getMessage());
        }

        return loggedInUser;
    }

    public DataSource getConnectedDataSource() {
        return dataSource;
    }

    private void showLoginDialog(DataSource source) {
        LoginFxml loginFxml = new LoginFxml();
        LoginController controller = new LoginController(source);

        FxmlParent fxmlParent = new FxmlParent(loginFxml, controller);
        Parent root = fxmlParent.get();

        Alert alert = new Alert(Alert.AlertType.NONE);

        alert.setTitle("Pee Calendar");
        alert.setDialogPane((DialogPane) root);

        alert.showAndWait()
                .filter(btn -> btn == controller.getLoginButton())
                .ifPresentOrElse(
                        //Login the user
                        btn -> {
                            loggedInUser = controller.getUsername();
                        },
                        //Exit. User chose to not login
                        () -> Platform.exit()
                );
    }

    private void showConnectionError(ConnectionPreferences prefs, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Database Connection Error");
        alert.setHeaderText("Failed to connect.\nCheck connection settings");
        try {
            alert.setContentText(message.substring(message.indexOf(": ") + 1));
        } catch (Exception e) {
            //message could be in a format that is unexpected
        }

        ButtonType settingsBtnType = new ButtonType("Settings", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(settingsBtnType);

        alert.showAndWait()
                .filter(btn -> btn == settingsBtnType)
                .ifPresentOrElse(
                        btn -> showDatabaseSettings(prefs),
                        //Exit if the settings won't be modified
                        () -> Platform.exit()
                );
    }

    private void showDatabaseSettings(ConnectionPreferences prefs) {
        DatabaseConnectionFxml dbConnFxml = new DatabaseConnectionFxml();
        DatabaseConnectionController controller = new DatabaseConnectionController(prefs);

        FxmlParent fxmlParent = new FxmlParent(dbConnFxml, controller);
        Parent root = fxmlParent.get();

        Alert alert = new Alert(Alert.AlertType.NONE);

        alert.setTitle("Pee Calendar");
        alert.setDialogPane((DialogPane) root);

        alert.showAndWait()
                .filter(btn -> btn == controller.getSaveButton())
                .ifPresentOrElse(
                        //If the settings are ok
                        //show a login dialog
                        btn -> getUser(),
                        //Exit if the settings were not saved
                        () -> Platform.exit()
                );
    }

}
