//@@author CarbonGrid
package seedu.address.ui;

import static java.awt.Desktop.getDesktop;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.OmniPanelTab;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.appointments.AppointmentsCommand;
import seedu.address.logic.commands.common.CommandResult;
import seedu.address.logic.commands.duties.DutyShiftCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.patients.ListPatientCommand;
import seedu.address.logic.commands.staff.ListStaffCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.ui.autocomplete.AutoCompleter;
import seedu.address.ui.commandboxhistory.CommandBoxHistory;
import seedu.address.ui.queue.QueueListPanel;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> implements AutoComplete, OmniPanelManager {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private final Stage primaryStage;
    private final Logic logic;
    private final AutoCompleter autoCompleter;
    private final CommandBoxHistory commandBoxHistory;
    private OmniPanelTab currentOmniPanelTab;
    private boolean requiresReset;

    private final HashSet<Runnable> deferredDropSelectors;

    // Independent Ui parts residing in this Ui container
    private AutoCompleteOverlay aco;
    private CommandBox commandBox;
    private PersonListPanel patientListPanel;
    private PersonListPanel staffListPanel;
    private QueueListPanel queueListPanel;
    private EventListPanel appointmentListPanel;
    private EventListPanel dutyShiftListPanel;
    private ResultDisplay resultDisplay;
    private TabBar tabBar;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private MenuItem checkForUpdatesMenuItem;

    @FXML
    private MenuItem aboutUsMenuItem;

    @FXML
    private StackPane omniPanelPlaceholder;

    @FXML
    private StackPane queueListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private StackPane tabBarPlaceholder;

    @FXML
    private SplitPane upperPane;

    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.autoCompleter = new AutoCompleter();
        this.commandBoxHistory = new CommandBoxHistory();
        this.requiresReset = false;

        this.deferredDropSelectors = new HashSet<>();
        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        getRoot().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> deferredDropSelectors.forEach(Runnable::run));

        upperPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            switch (keyEvent.getCode()) {
            case TAB:
                keyEvent.consume();
                commandBox.getRoot().requestFocus();
                break;
            default:
            }
        });

        logic.bindOmniPanelTabConsumer(this::setOmniPanelTab);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
        setAccelerator(checkForUpdatesMenuItem, KeyCombination.valueOf("F2"));
        setAccelerator(aboutUsMenuItem, KeyCombination.valueOf("F3"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     *
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        patientListPanel = new PersonListPanel(logic.getFilteredPatientList(), deferredDropSelectors);
        staffListPanel = new PersonListPanel(logic.getFilteredStaffList(), deferredDropSelectors);

        appointmentListPanel = new EventListPanel(logic.getFilteredAppointmentList(), deferredDropSelectors, true);
        dutyShiftListPanel = new EventListPanel(logic.getFilteredDutyShiftList(), deferredDropSelectors, false);

        tabBar = new TabBar(this);
        tabBarPlaceholder.getChildren().add(tabBar.getRoot());

        queueListPanel = new QueueListPanel(logic.getConsultationRoomList(),
                logic.getQueueList(), logic.getReferenceIdResolver());
        queueListPanelPlaceholder.getChildren().add(queueListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAddressBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        commandBox = new CommandBox(this::executeCommand, this);
        commandBoxPlaceholder.getChildren().addAll(commandBox.getRoot());

        aco = new AutoCompleteOverlay(this::autoCompleterSelected);
        anchorPane.getChildren().add(aco.getRoot());
        AnchorPane.setBottomAnchor(aco.getRoot(), 0.0);
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        //Screen Size
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();

        if (screenHeight < guiSettings.getWindowHeight() || screenWidth < guiSettings.getWindowWidth()) {
            return;
        }

        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());

        if (guiSettings.getWindowCoordinates() == null
                || screenWidth < guiSettings.getWindowWidth() + guiSettings.getWindowCoordinates().getX()
                || screenHeight < guiSettings.getWindowHeight() + guiSettings.getWindowCoordinates().getY()) {
            return;
        }

        primaryStage.setX(guiSettings.getWindowCoordinates().getX());
        primaryStage.setY(guiSettings.getWindowCoordinates().getY());
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Opens URI to User Guide.
     */
    @FXML
    private void handleHelp() {
        try {
            getDesktop().browse(URI.create("https://ay1920s1-cs2103t-t09-3.github.io/main/UserGuide#Features"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens URI to Releases.
     */
    @FXML
    private void handleCheckForUpdates() {
        try {
            getDesktop().browse(URI.create("https://github.com/AY1920S1-CS2103T-T09-3/main/releases"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens URI to About Us.
     */
    @FXML
    private void handleAboutUs() {
        try {
            getDesktop().browse(URI.create("https://ay1920s1-cs2103t-t09-3.github.io/main/AboutUs"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        primaryStage.hide();
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private void executeCommand(String commandText) {
        try {
            CommandResult commandResult = logic.execute(commandText);
            requiresReset = true;
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            autoCompleter.setCustomSuggestions(commandResult.getListOfSuggestedCommands());
        } catch (CommandException | ParseException e) {
            logger.info("Invalid command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
        }
    }

    /**
     * Called to update AutoComplete when new commands.
     */
    public void updateCommandAutoComplete(String commandText) {
        if (!commandText.isBlank()) {
            logic.eagerEvaluate(commandText, resultDisplay::setFeedbackToUser);
            requiresReset = true;
        }
        aco.showSuggestions(commandText, autoCompleter.update(commandText).getSuggestions());
        Region acoRoot = aco.getRoot();
        acoRoot.setTranslateX(Math.min(acoRoot.getTranslateX(), getRoot().getWidth() - acoRoot.getWidth()));
    }

    /**
     * Receives Key Press event from Command Box and executes expected behaviours.
     */
    public void updateSelectionKeyPressedCommandBox(KeyCode keyCode) {
        switch (keyCode) {
        case UP:
            if (aco.isSuggesting()) {
                aco.traverseSelection(true);
                break;
            }
            commandBox.setCommandTextField(commandBoxHistory.getOlder());
            break;
        case DOWN:
            if (aco.isSuggesting()) {
                aco.traverseSelection(false);
                break;
            }
            commandBox.setCommandTextField(commandBoxHistory.getNewer());
            break;
        case ENTER:
            if (aco.isSuggesting()) {
                aco.simulateMouseClick();
                break;
            }
            commandBoxHistory.add(commandBox.handleCommandEntered());
            break;
        default:
        }
    }

    /**
     * Called whenever AutoComplete has a selection.
     */
    private void autoCompleterSelected(String selectedText) {
        commandBox.appendCommandTextField(selectedText);
    }

    /**
     * Sets OmniPanelTab.
     */
    @Override
    public void setOmniPanelTab(OmniPanelTab omniPanelTab) {
        if (omniPanelTab.equals(currentOmniPanelTab)) {
            return;
        }
        if (requiresReset) {
            switch (currentOmniPanelTab) {
            case PATIENTS_TAB:
                executeCommand(ListPatientCommand.COMMAND_WORD);
                break;
            case APPOINTMENTS_TAB:
                executeCommand(AppointmentsCommand.COMMAND_WORD);
                break;
            case DOCTORS_TAB:
                executeCommand(ListStaffCommand.COMMAND_WORD);
                break;
            case DUTY_SHIFT_TAB:
                executeCommand(DutyShiftCommand.COMMAND_WORD);
                break;
            default:
            }
            requiresReset = false;
        }
        tabBar.selectTabUsingIndex(omniPanelTab.getTabBarIndex());
        currentOmniPanelTab = omniPanelTab;
        resultDisplay.setFeedbackToUser("");
        Region region;
        switch (omniPanelTab) {
        case PATIENTS_TAB:
            region = patientListPanel.getRoot();
            break;
        case APPOINTMENTS_TAB:
            region = appointmentListPanel.getRoot();
            break;
        case DOCTORS_TAB:
            region = staffListPanel.getRoot();
            break;
        case DUTY_SHIFT_TAB:
            region = dutyShiftListPanel.getRoot();
            break;
        default:
            return;
        }
        Platform.runLater(() -> omniPanelPlaceholder.getChildren().setAll(region));
    }

    @Override
    public void regainOmniPanelSelector() {
        switch (currentOmniPanelTab) {
        case PATIENTS_TAB:
            patientListPanel.regainSelector();
            break;
        case APPOINTMENTS_TAB:
            appointmentListPanel.regainSelector();
            break;
        case DOCTORS_TAB:
            staffListPanel.regainSelector();
            break;
        case DUTY_SHIFT_TAB:
            dutyShiftListPanel.regainSelector();
            break;
        default:
        }
    }
}
