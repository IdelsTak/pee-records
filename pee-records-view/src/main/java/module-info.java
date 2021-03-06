/*
 * Copyright 2021
 */

module pee.view {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.base;
    requires com.calendarfx.view;
    
    exports com.github.idelstak.pee.records.view.api;
    exports com.github.idelstak.pee.records.view.spi;
    exports com.github.idelstak.pee.records.view.database;
    exports com.github.idelstak.pee.records.view.login;
    exports com.github.idelstak.pee.records.view.patient;
}
