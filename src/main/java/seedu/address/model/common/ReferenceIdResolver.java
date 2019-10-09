package seedu.address.model.common;

import seedu.address.model.person.Person;

public interface ReferenceIdResolver {

    /**
     * Gets a person whose {@code ReferenceId} matches the given id, otherwise, null.
     */
    Person resolve(ReferenceId id);

    /**
     * Checks whether a person whose {@code ReferenceId} matches the given id.
     */
    boolean hasPerson(ReferenceId id);
}