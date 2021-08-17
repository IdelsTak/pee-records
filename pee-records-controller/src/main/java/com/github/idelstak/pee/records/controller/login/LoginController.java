/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller.login;

import com.github.idelstak.pee.records.dao.spi.DoctorsDao;
import com.github.idelstak.pee.records.dao.impl.MySqlDoctorsDao;
import com.github.idelstak.pee.records.model.spi.Doctor;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
    private RadioButton doctorRadioButton;
    @FXML
    private ToggleGroup userTypeToggleGroup;
    @FXML
    private RadioButton patientRadioButton;
    @FXML
    private TextField userNameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginStatusLabel;
    private final DataSource dataSource;
    private final List<Doctor> allDoctors = new ArrayList<>();
    private final BooleanProperty correctLoginProp = new SimpleBooleanProperty(false);
    private final ButtonType loginButtonType;
    private Optional<String> user = Optional.empty();

    public LoginController(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source must not be null");
        }
        this.dataSource = dataSource;
        this.loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
    }

    @FXML
    void initialize() {
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
    
    public Optional<String> getUsername(){
        return Optional.of(userNameTextField.getText());
    } 

    private boolean correctLogin() {
        if (passwordField.getText() == null || passwordField.getText().isBlank()) {
            correctLoginProp.setValue(false);
        } else {
            if (allDoctors.isEmpty()) {
                DoctorsDao dao = new MySqlDoctorsDao(dataSource);

                try {
                    Iterable<Doctor> doctors = dao.getAllDoctors();
                    for (Doctor doctor : doctors) {
                        allDoctors.add(doctor);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            allDoctors.stream()
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
