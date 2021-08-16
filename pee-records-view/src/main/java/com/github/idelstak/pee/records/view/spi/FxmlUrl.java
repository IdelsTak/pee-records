/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.view.spi;

import java.net.URL;
import java.util.function.Supplier;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface FxmlUrl extends Supplier<URL> {

    @Override
    public URL get();

}
