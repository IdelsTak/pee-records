/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.spi;

import com.github.idelstak.pee.records.model.spi.core.Entity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface PeeEvent extends Entity {

    PeeCycle getCycle();

    LocalDateTime getWhen();

    Type getType();

    public enum Type {

        WET_NIGHT("Wet Night"),
        FEW_DROPS("Few Drops"),
        DRY_NIGHT("Dry Night");
        private final String description;

        private Type(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }

        public static class Description {

            private final String description;

            public Description(String description) {
                if (description == null || description.isBlank()) {
                    throw new IllegalArgumentException("Pee type description must not be null");
                }
                this.description = description;
            }

            public Type toPeeType() {
                Optional<Type> optionalType = Arrays.stream(Type.values())
                        .filter(type -> type.toString().equalsIgnoreCase(description))
                        .findFirst();
                return optionalType.orElseThrow(() -> new TypeNotPresentException(description, null));
            }
        }

    }

}
