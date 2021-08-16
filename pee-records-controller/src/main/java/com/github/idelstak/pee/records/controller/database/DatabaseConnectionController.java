/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller.database;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class DatabaseConnectionController {

    @FXML
    private TextField hostTextField;
    @FXML
    private TextField portTextField;
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
    void initialize() {

    }
}
