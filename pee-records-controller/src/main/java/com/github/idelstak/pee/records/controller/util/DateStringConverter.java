/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.util.StringConverter;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class DateStringConverter extends StringConverter<LocalDate> {

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, YYYY");

    @Override
    public String toString(LocalDate ld) {
        if (ld == null) {
            return "";
        }
        return dtf.format(ld);
    }

    @Override
    public LocalDate fromString(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(s, dtf);
    }

}
