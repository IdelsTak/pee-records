/*
 * Copyright 2021
 */

module pee.controller {
    requires java.prefs;
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires pee.database.access;
    requires pee.view;
    requires pee.dao;
    requires pee.model;
    
    opens com.github.idelstak.pee.records.controller to javafx.graphics;
    exports com.github.idelstak.pee.records.controller;
    
    opens com.github.idelstak.pee.records.controller.database to javafx.fxml;
    exports com.github.idelstak.pee.records.controller.database;
    
    opens com.github.idelstak.pee.records.controller.login to javafx.fxml;
    exports com.github.idelstak.pee.records.controller.login;
}
