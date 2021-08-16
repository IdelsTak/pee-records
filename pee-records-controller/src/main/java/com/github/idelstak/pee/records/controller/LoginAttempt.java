/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller;

import com.github.idelstak.pee.records.controller.database.DatabaseConnectionController;
import com.github.idelstak.pee.records.controller.login.LoginController;
import com.github.idelstak.pee.records.database.DatabaseAccess;
import com.github.idelstak.pee.records.view.api.FxmlParent;
import com.github.idelstak.pee.records.view.database.DatabaseConnectionFxml;
import com.github.idelstak.pee.records.view.login.LoginFxml;
import java.sql.Connection;
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

    private Optional<String> loggedInUser = Optional.empty();

    public LoginAttempt() {
    }

    public Optional<String> getUser() {

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
            DataSource ds = databaseAccess.getDataSource();

            try (Connection conn = ds.getConnection()) {
                showLoginDialog(ds);
            }
        } catch (Exception e) {
            showConnectionError(preferences, e.getMessage());
        }

        return loggedInUser;
    }

    private void showLoginDialog(DataSource source) {
        LoginFxml loginFxml = new LoginFxml();
        LoginController controller = new LoginController(source);

        FxmlParent fxmlParent = new FxmlParent(loginFxml, controller);
        Parent root = fxmlParent.get();

        Alert alert = new Alert(Alert.AlertType.NONE);

        alert.setTitle("Login");
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
        alert.setContentText(message.substring(message.indexOf(": ") + 1));

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

        alert.setTitle("Settings");
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
