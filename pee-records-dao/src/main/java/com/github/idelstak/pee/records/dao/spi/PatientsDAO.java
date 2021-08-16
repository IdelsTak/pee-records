/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.dao.spi;

import com.github.idelstak.pee.records.model.api.Name;
import com.github.idelstak.pee.records.model.spi.Patient;
import com.github.idelstak.pee.records.model.spi.core.Entity;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import com.github.idelstak.pee.records.model.spi.core.Login;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface PatientsDAO {

    Iterable<Patient> getAllPatients() throws IOException;

    Optional<Patient> getPatient(Entity patientId) throws IOException;

    Entity addPatient(Name name, Login credentials, LocalDate dateOfBirth, LocalDate registrationDate) throws IOException;

    void rename(Entity patientId, Name newName) throws IOException;

    void changeLoginCredentials(Entity patientId, Login newCredentials) throws IOException;

    void updateDateOfBirth(Entity patientId, LocalDate newDateOfBirth) throws IOException;

    void updateRegistrationDate(Entity patientId, int newRegistrationDate) throws IOException;

}
