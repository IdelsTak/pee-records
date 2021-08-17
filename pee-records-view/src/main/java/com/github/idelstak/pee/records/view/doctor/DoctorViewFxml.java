/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.view.doctor;

import com.github.idelstak.pee.records.view.spi.FxmlUrl;
import java.lang.invoke.MethodHandles;
import java.net.URL;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class DoctorViewFxml implements FxmlUrl {

    private final String name;

    public DoctorViewFxml() {
        this.name = "/fxml/doctor-view.fxml";
    }

    @Override
    public URL get() {
        return MethodHandles.lookup().lookupClass().getResource(name);
    }

}
