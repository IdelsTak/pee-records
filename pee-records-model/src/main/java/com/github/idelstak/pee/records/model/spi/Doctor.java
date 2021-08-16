/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.spi;

import com.github.idelstak.pee.records.model.spi.core.Nameable;
import com.github.idelstak.pee.records.model.spi.core.Entity;
import com.github.idelstak.pee.records.model.spi.core.Login;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface Doctor extends Entity, Nameable, Login {

    boolean isActive();
}
