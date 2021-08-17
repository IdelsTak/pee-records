/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.view.patient;

import com.github.idelstak.pee.records.view.spi.FxmlUrl;
import java.lang.invoke.MethodHandles;
import java.net.URL;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class CycleDetailsFxml implements FxmlUrl {

    private final String name;

    public CycleDetailsFxml() {
        this.name = "/fxml/cycle-details.fxml";
    }

    @Override
    public URL get() {
        return MethodHandles.lookup().lookupClass().getResource(name);
    }

}
