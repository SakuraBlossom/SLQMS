package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.common.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;
    private final AutoCompleterUpdater autoCompleterUpdater;
    private final KeyPressedNotifier keyPressedNotifier;

    @FXML
    private TextField commandTextField;

    public CommandBox(CommandExecutor commandExecutor, AutoCompleterUpdater autoCompleterUpdater,
                      KeyPressedNotifier autoCompleterSelector) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.autoCompleterUpdater = autoCompleterUpdater;
        this.keyPressedNotifier = autoCompleterSelector;
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());

        // EventFilter was used as FXML callback onKeyPressed cannot consume keyEvent.
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                case UP:
                case DOWN:
                    keyEvent.consume();
                    break;
                default:
                }
                keyPressedNotifier.notify(keyEvent.getCode());
            }
        });
    }

    /**
     * Handles Command Entered.
     */
    public void handleCommandEntered() {
        try {
            commandExecutor.execute(commandTextField.getText());
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Handles the Text Change event.
     */
    @FXML
    private void handleTextChanged() {
        autoCompleterUpdater.update(commandTextField.getText());
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    public void setCommandTextField(String suggestion) {
        commandTextField.setText(suggestion);
        commandTextField.positionCaret(commandTextField.getLength());
        handleTextChanged();
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

    /**
     * Represents a function that updates the AutoCompleter.
     */
    @FunctionalInterface
    public interface AutoCompleterUpdater {
        /**
         * Updates AutoCompleter of the command text.
         */
        void update(String commandText);
    }

    /**
     * Represents a function that updates the AutoCompleter.
     */
    @FunctionalInterface
    public interface KeyPressedNotifier {
        /**
         * Updates AutoCompleter of the command text.
         */
        void notify(KeyCode keyCode);
    }
}
