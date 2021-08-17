/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.spi.core;

import com.github.idelstak.pee.records.model.api.Credentials;
import com.github.idelstak.pee.records.model.api.Name;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface Person extends Entity, Nameable, Login {

    @Override
    public int getId();

    @Override
    public Name getName();

    @Override
    public Credentials getCredentials();

}
