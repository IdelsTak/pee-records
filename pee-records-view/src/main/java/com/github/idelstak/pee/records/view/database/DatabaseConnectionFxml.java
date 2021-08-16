/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.view.database;

import com.github.idelstak.pee.records.view.spi.FxmlUrl;
import java.lang.invoke.MethodHandles;
import java.net.URL;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class DatabaseConnectionFxml implements FxmlUrl {

    private final String name;

    public DatabaseConnectionFxml() {
        this.name = "/fxml/database-connection.fxml";
    }

    @Override
    public URL get() {
        return MethodHandles.lookup().lookupClass().getResource(name);
    }

}
