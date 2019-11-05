package seedu.address.logic.commands.common;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;
    private final List<String> listOfSuggestedCommands;

    /**
     * Help information should be shown to the user.
     */
    private final boolean showHelp;

    /**
     * The application should exit.
     */
    private final boolean exit;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, List<String> listOfSuggestedCommands, boolean showHelp, boolean exit) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.listOfSuggestedCommands = (listOfSuggestedCommands == null) ? List.of() : listOfSuggestedCommands;
        this.showHelp = showHelp;
        this.exit = exit;
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, null, false, false);
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser}
     * and {@code listOfSuggestedCommands}. Other fields will be set to their default value.
     */
    public CommandResult(String feedbackToUser, List<String> listOfSuggestedCommands) {
        this(feedbackToUser, listOfSuggestedCommands, false, false);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public List<String> getListOfSuggestedCommands() {
        return listOfSuggestedCommands;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult)) {
            return false;
        }

        CommandResult otherCommandResult = (CommandResult) other;
        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit;
    }

    @Override
    public String toString() {
        return feedbackToUser;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit);
    }

}
