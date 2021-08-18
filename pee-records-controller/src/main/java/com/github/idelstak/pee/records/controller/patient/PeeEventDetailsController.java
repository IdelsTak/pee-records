/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller.patient;

import com.github.idelstak.pee.records.controller.util.DateStringConverter;
import com.github.idelstak.pee.records.model.spi.PeeEvent;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class PeeEventDetailsController {

    @FXML
    private DialogPane dialogPane;
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private ChoiceBox<PeeEvent.Type> typeChoiceBox;
    private final LocalDate eventDate;
    private final PeeEvent.Type type;

    public PeeEventDetailsController() {
        this(null, null);
    }

    public PeeEventDetailsController(LocalDate eventDate, PeeEvent.Type type) {
        this.eventDate = eventDate;
        this.type = type;
    }

    @FXML
    public void initialize() {
        eventDatePicker.setConverter(new DateStringConverter());
        eventDatePicker.skinProperty().addListener(o -> eventDatePicker.requestFocus());

        PeeEvent.Type[] typesArr = PeeEvent.Type.values();

        for (PeeEvent.Type type : typesArr) {
            typeChoiceBox.getItems().add(type);
        }

        if (eventDate != null) {
            eventDatePicker.valueProperty().setValue(eventDate);
        } else {
            eventDatePicker.valueProperty().setValue(LocalDate.now());
        }

        if (type != null) {
            typeChoiceBox.getSelectionModel().select(type);
        } else {
            typeChoiceBox.getSelectionModel().selectFirst();
        }
    }

    public LocalDate getEventDate() {
        return eventDatePicker.valueProperty().getValue();
    }

    public PeeEvent.Type getType() {
        return typeChoiceBox.getSelectionModel().getSelectedItem();
    }
}
