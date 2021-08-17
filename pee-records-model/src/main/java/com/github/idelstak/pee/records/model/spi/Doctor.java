/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.spi;

import com.github.idelstak.pee.records.model.spi.core.Person;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface Doctor extends Person {

    boolean isActive();
}
