/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller.patient;

import com.github.idelstak.pee.records.controller.util.DateStringConverter;
import java.time.LocalDate;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class CycleDetailsController {

    @FXML
    private DialogPane dialogPane;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    private final ButtonType saveBtnType;
    private final ObjectProperty<LocalDate> startDateProp = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> endDateProp = new SimpleObjectProperty<>();
    private LocalDate startDate;

    public CycleDetailsController(LocalDate startDate) {
        this.startDate = startDate;
        this.saveBtnType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    }

    @FXML
    public void initialize() {
        dialogPane.getButtonTypes().setAll(saveBtnType, ButtonType.CANCEL);

        startDatePicker.skinProperty().addListener(o -> startDatePicker.requestFocus());
        startDatePicker.valueProperty().setValue(startDate == null ? LocalDate.now() : startDate);
        startDateProp.bind(startDatePicker.valueProperty());
        endDateProp.bind(Bindings.createObjectBinding(() -> {
            LocalDate sd = startDateProp.getValue();
            return sd == null ? LocalDate.now().plusWeeks(13L) : sd.plusWeeks(13L);
        }, startDateProp));

        endDatePicker.valueProperty().bind(endDateProp);

        startDatePicker.setConverter(new DateStringConverter());
        endDatePicker.setConverter(new DateStringConverter());
    }

    public ButtonType getSaveButton() {
        return saveBtnType;
    }

    public LocalDate getStartDate() {
        return startDateProp.getValue();
    }

    public LocalDate getEndDate() {
        return endDateProp.getValue();
    }

}
