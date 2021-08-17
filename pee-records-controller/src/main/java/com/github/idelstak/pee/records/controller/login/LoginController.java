/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller.login;

import com.github.idelstak.pee.records.dao.impl.MySqlPatientsDao;
import com.github.idelstak.pee.records.dao.spi.PatientsDao;
import com.github.idelstak.pee.records.model.spi.Patient;
import com.github.idelstak.pee.records.model.spi.core.Login;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javax.sql.DataSource;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    @FXML
    private DialogPane loginDialogPane;
    @FXML
    private TextField userNameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginStatusLabel;
    private final DataSource dataSource;
    private final List<Patient> allPatients = new ArrayList<>();
    private final BooleanProperty correctLoginProp = new SimpleBooleanProperty(false);
    private final ButtonType loginButtonType;

    public LoginController(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source must not be null");
        }
        this.dataSource = dataSource;
        this.loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
    }

    @FXML
    public void initialize() {
        //Make the user name get the focus when dialog is shown
        userNameTextField.skinProperty().addListener((o) -> userNameTextField.requestFocus());
        //Add the button types
        loginDialogPane.getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        Button loginButton = (Button) loginDialogPane.lookupButton(loginButtonType);

        loginButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            String username = userNameTextField.textProperty().getValue();
            return username == null || username.isBlank();
        }, userNameTextField.textProperty()));

        loginButton.addEventFilter(ActionEvent.ACTION, evt -> {
            BooleanProperty statusVisibleProp = loginStatusLabel.visibleProperty();

            if (!statusVisibleProp.isBound()) {
                statusVisibleProp.bind(correctLoginProp.not());
            }

            boolean correct = correctLogin();

            //If not correct just consume the event
            //Consuming the event doesn't close the dialog
            if (!correct) {
                evt.consume();
            }
        });

    }

    public ButtonType getLoginButton() {
        return loginButtonType;
    }

    public Optional<Patient> getUsername() {
        return allPatients.stream()
                .filter(patient -> emailsMatch(patient, userNameTextField.getText()))
                .findFirst();
    }

    private boolean emailsMatch(Patient patient, String username) {
        String email = patient.getCredentials()
                .getEmail()
                .toLowerCase()
                .trim();
        username = username.toLowerCase().trim();
        return email.equals(username);
    }

    private boolean correctLogin() {
        if (passwordField.getText() == null || passwordField.getText().isBlank()) {
            correctLoginProp.setValue(false);
        } else {
            if (allPatients.isEmpty()) {
                PatientsDao dao = new MySqlPatientsDao(dataSource);

                try {
                    Iterable<Patient> patients = dao.getAllPatients();
                    for (Patient patient : patients) {
                        allPatients.add(patient);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            allPatients.stream()
                    .map(Login::getCredentials)
                    .filter(credentials -> credentials != null)
                    .filter(credentials -> credentials.getEmail() != null)
                    .filter(credentials -> {
                        String email = credentials.getEmail();
                        String trimedEmail = email.toLowerCase().trim();
                        String trimmedUserName = userNameTextField.getText().toLowerCase().trim();
                        return trimedEmail.equals(trimmedUserName);
                    })
                    .map(credentials -> credentials.getPassword())
                    .findFirst()
                    .ifPresentOrElse(
                            pass -> {
                                correctLoginProp.setValue(Arrays.equals(pass, passwordField.getText().toCharArray()));
                            },
                            () -> correctLoginProp.setValue(false)
                    );
        }

        return correctLoginProp.get();
    }
}
