/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.spi;

import com.github.idelstak.pee.records.model.spi.core.Nameable;
import com.github.idelstak.pee.records.model.spi.core.Entity;
import java.time.LocalDate;
import com.github.idelstak.pee.records.model.spi.core.Login;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface Patient extends Entity, Nameable, Login {

    LocalDate getDateOfBirth();

    LocalDate getRegistrationDate();

    Iterable<PeeCycle> getPeeCycles();
}
