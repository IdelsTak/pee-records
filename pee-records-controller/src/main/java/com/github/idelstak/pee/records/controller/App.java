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
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javax.sql.DataSource;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class App extends Application {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    @Override
    public void start(Stage primaryStage) {
        Preferences prefs = Preferences.userNodeForPackage(App.class);

        String user = prefs.get("user.name", "root");
        char[] password = prefs.get("password", "corner-dicing").toCharArray();
        String url = prefs.get("jdbc.url", "jdbc:mysql://localhost/PeeCalendar");

        DatabaseAccess databaseAccess = new DatabaseAccess(user, password, url);

        try {
            DataSource dataSource = databaseAccess.getDataSource();
            Parent root;

            try (Connection conn = dataSource.getConnection()) {
                LoginFxml loginFxml = new LoginFxml();
                LoginController loginController = new LoginController(dataSource);

                FxmlParent fxmlParent = new FxmlParent(loginFxml, loginController);
                root = fxmlParent.get();
            } catch (SQLException ex) {
                DatabaseConnectionFxml dbConnFxml = new DatabaseConnectionFxml();
                DatabaseConnectionController dbConnController = new DatabaseConnectionController();

                FxmlParent fxmlParent = new FxmlParent(dbConnFxml, dbConnController);
                root = fxmlParent.get();
            }

            Scene scene = new Scene(root);

            primaryStage.setTitle("Pee Records");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Database Connection Error");
            alert.setHeaderText("Failed to connect");
            alert.setContentText(ex.getMessage().substring(ex.getMessage().indexOf(":") + 2));

            Optional<ButtonType> btn = alert.showAndWait();

            btn.ifPresent(btnType -> Platform.exit());

            alert.setOnHidden(eh -> Platform.exit());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
