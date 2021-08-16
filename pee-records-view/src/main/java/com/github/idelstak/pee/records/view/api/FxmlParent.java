/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.view.api;

import com.github.idelstak.pee.records.view.spi.FxmlUrl;
import java.io.IOException;
import java.util.function.Supplier;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class FxmlParent implements Supplier<Parent> {

    private final FxmlUrl fxmlUrl;
    private final Object controller;

    public FxmlParent(FxmlUrl fxmlUrl, Object controller) {
        if (fxmlUrl == null) {
            throw new IllegalArgumentException("FXML URL must not be null");
        } else if (controller == null) {
            throw new IllegalArgumentException("Controller must not be null");
        }
        this.fxmlUrl = fxmlUrl;
        this.controller = controller;
    }

    @Override
    public Parent get() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(fxmlUrl.get());

        // setting ClassLoader for OSGi environments
        loader.setClassLoader(controller.getClass().getClassLoader());

        Parent root;
        try {
            root = (Parent) loader.load();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load " + fxmlUrl.get().getFile(), ex);
        }
        return root;
    }

}
