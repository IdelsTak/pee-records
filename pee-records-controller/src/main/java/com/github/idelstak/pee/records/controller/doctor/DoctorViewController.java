/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller.doctor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class DoctorViewController {

    @FXML
    private Label usernameLabel;
    private final String username;

    public DoctorViewController(String username) {
        this.username = username;
    }

    @FXML
    public void initialize() {
        usernameLabel.setText(username);
    }
}
