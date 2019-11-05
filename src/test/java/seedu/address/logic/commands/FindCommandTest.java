package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static seedu.address.commons.core.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ELLE;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.person.predicates.PersonContainsKeywordPredicate;
import seedu.address.testutil.TestUtil;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = TestUtil.getTypicalModelManager();
        expectedModel = TestUtil.getTypicalModelManager();
    }

    @Test
    public void equals() {
        PersonContainsKeywordPredicate firstPredicate =
                new PersonContainsKeywordPredicate("first");
        PersonContainsKeywordPredicate secondPredicate =
                new PersonContainsKeywordPredicate("second");

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0, "xx");
        PersonContainsKeywordPredicate predicate = preparePredicate("xx");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPatientList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPatientList());
    }

    @Test
    public void execute_multipleKeywords_personsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1, "Elle");
        PersonContainsKeywordPredicate predicate = preparePredicate("Elle");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPatientList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ELLE), model.getFilteredPatientList());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private PersonContainsKeywordPredicate preparePredicate(String userInput) {
        return new PersonContainsKeywordPredicate(userInput);
    }
}
