/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.spi.core;

import com.github.idelstak.pee.records.model.api.Credentials;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface Login {

    Credentials getCredentials();

}
