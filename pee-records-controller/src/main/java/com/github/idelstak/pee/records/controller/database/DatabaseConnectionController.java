/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller.database;

import com.github.idelstak.pee.records.controller.ConnectionPreferences;
import com.github.idelstak.pee.records.database.DatabaseAccess;
import java.net.URI;
import java.sql.Connection;
import java.util.Optional;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class DatabaseConnectionController {

    @FXML
    private DialogPane connectionDialogPane;
    @FXML
    private TextField hostTextField;
    @FXML
    private TextField databaseNameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextfield;
    @FXML
    private TextField urlTextField;
    @FXML
    private Button testConnectionButton;
    @FXML
    private Label connectionStatusLabel;
    @FXML
    private ProgressIndicator connectionProgressIndicator;
    @FXML
    private Label connectionFailureDetailsLabel;
    private final ConnectionPreferences preferences;
    private final ButtonType saveButtonType;

    public DatabaseConnectionController(ConnectionPreferences preferences) {
        this.preferences = preferences;
        this.saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    }

    @FXML
    void initialize() {
        //Add the button types
        connectionDialogPane.getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        Button saveBtn = (Button) connectionDialogPane.lookupButton(saveButtonType);

        saveBtn.addEventFilter(ActionEvent.ACTION, eh -> {
            String user = usernameTextField.getText();
            String password = passwordTextfield.getText();
            char[] pass = password == null ? new char[]{} : password.toCharArray();
            String URL = urlTextField.getText();

            preferences.setUrl(URL);
            preferences.setUsername(user);
            preferences.setPassword(pass);
        });

        String url = preferences.getUrl();
        //URL should be longer than 5 characters if it is valid
        //A proper URL should at least contain: "jdbc:mysql://"
        if (url != null && url.length() > 5) {
            URI uri = URI.create(url.substring(5));

            hostTextField.setText(uri.getHost());
            databaseNameTextField.setText(uri.getPath().substring(1));

            urlTextField.setText(url);
        }

        usernameTextField.setText(preferences.getUsername());
        passwordTextfield.setText(new String(preferences.getPassword()));

        hostTextField.textProperty().addListener((o, ov, nv) -> {
            updateUrl(nv, databaseNameTextField.getText());
        });

        databaseNameTextField.textProperty().addListener((o, ov, nv) -> {
            updateUrl(hostTextField.getText(), nv);
        });

        testConnectionButton.setOnAction(eh -> doTestConnection());
    }

    public ButtonType getSaveButton() {
        return saveButtonType;
    }

    private void doTestConnection() {
        String user = usernameTextField.getText();
        String password = passwordTextfield.getText();
        char[] pass = password == null ? new char[]{} : password.toCharArray();
        String URL = urlTextField.getText();
        DatabaseAccess access = new DatabaseAccess(user, pass, URL);

        Service<Optional<Connection>> connectService = new Service<Optional<Connection>>() {
            @Override
            protected Task<Optional<Connection>> createTask() {
                return new DatabaseConnectionTask(access);
            }
        };

        connectionStatusLabel.textProperty().bind(connectService.titleProperty());
        connectionFailureDetailsLabel.textProperty().bind(connectService.messageProperty());
        connectionProgressIndicator.visibleProperty().bind(connectService.runningProperty());
        connectionProgressIndicator.progressProperty().bind(connectService.progressProperty());

        connectService.setOnSucceeded(wse -> {
            Optional<Connection> optionalConn = (Optional<Connection>) wse.getSource().getValue();

            optionalConn.ifPresentOrElse(
                    conn -> {
                        connectionStatusLabel.setTextFill(Color.GREEN);
                        access.closeConnection();
                    },
                    () -> connectionStatusLabel.setTextFill(Color.RED)
            );
        });

        connectService.setOnRunning(eh -> connectionStatusLabel.setTextFill(Color.BLACK));

        connectService.start();
    }

    private void updateUrl(String host, String name) {
        urlTextField.setText("jdbc:mysql://%s/%s".formatted(host, name));
    }

}
