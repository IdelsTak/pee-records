/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.view.login;

import com.github.idelstak.pee.records.view.spi.FxmlUrl;
import java.lang.invoke.MethodHandles;
import java.net.URL;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class LoginFxml implements FxmlUrl {

    private final String name;

    public LoginFxml() {
        this.name = "/fxml/login.fxml";
    }

    @Override
    public URL get() {
        return MethodHandles.lookup().lookupClass().getResource(name);
    }

}
