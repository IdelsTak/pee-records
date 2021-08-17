/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.spi;

import com.github.idelstak.pee.records.model.spi.core.Person;
import java.time.LocalDate;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface Patient extends Person {

    LocalDate getDateOfBirth();

    LocalDate getRegistrationDate();

    Iterable<PeeCycle> getPeeCycles();
}
