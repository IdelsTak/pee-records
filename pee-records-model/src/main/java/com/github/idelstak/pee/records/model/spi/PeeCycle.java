/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.spi;

import com.github.idelstak.pee.records.model.spi.core.Entity;
import java.time.LocalDate;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface PeeCycle extends Entity {

    Patient getPatient();

    LocalDate getStartDate();

    LocalDate getEndDate();

    Iterable<PeeEvent> getPeeEvents();

}
