package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AckAppCommand;
import seedu.address.logic.commands.AddAppCommand;
import seedu.address.logic.commands.AppointmentsCommand;
import seedu.address.logic.commands.CancelAppCommand;
import seedu.address.logic.commands.ChangeAppCommand;
import seedu.address.logic.commands.DequeueCommand;
import seedu.address.logic.commands.EnqueueCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.MissAppCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.SettleAppCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.common.Command;
import seedu.address.logic.commands.common.CommandHistory;
import seedu.address.logic.commands.patients.EditPatientDetailsCommand;
import seedu.address.logic.commands.patients.RegisterPatientCommand;
import seedu.address.logic.commands.patients.UnregisterPatientCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.patients.EditPatientDetailsCommandParser;
import seedu.address.logic.parser.patients.RegisterPatientCommandParser;
import seedu.address.logic.parser.patients.UnregisterPatientCommandParser;
import seedu.address.model.Model;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private final CommandHistory commandHistory;

    public AddressBookParser(CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
    }

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput, Model model) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case RegisterPatientCommand.COMMAND_WORD:
            return new RegisterPatientCommandParser().parse(arguments);

        case EditPatientDetailsCommand.COMMAND_WORD:
            return new EditPatientDetailsCommandParser(model).parse(arguments);

        case UnregisterPatientCommand.COMMAND_WORD:
            return new UnregisterPatientCommandParser(model).parse(arguments);

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case UndoCommand.COMMAND_WORD:
            return new UndoCommand(commandHistory);

        case RedoCommand.COMMAND_WORD:
            return new RedoCommand(commandHistory);

        case EnqueueCommand.COMMAND_WORD:
            return new EnqueueCommandParser().parse(arguments);

        case DequeueCommand.COMMAND_WORD:
            return new DequeueCommandParser(model).parse(arguments);

        case AddAppCommand.COMMAND_WORD:
            return new AddAppCommandParser(model).parse(arguments);

        case AppointmentsCommand.COMMAND_WORD:
            return new AppointmentsCommandParser(model).parse(arguments);

        case AckAppCommand.COMMAND_WORD:
            return new AckAppCommandParser(model).parse(arguments);

        case CancelAppCommand.COMMAND_WORD:
            return new CancelAppCommandParser(model).parse(arguments);

        case ChangeAppCommand.COMMAND_WORD:
            return new ChangeAppCommandParser().parse(arguments);

        case MissAppCommand.COMMAND_WORD:
            return new MissAppCommandParser().parse(arguments);

        case SettleAppCommand.COMMAND_WORD:
            return new SettleAppCommandParser().parse(arguments);

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

}
