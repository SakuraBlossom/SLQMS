//@@author CarbonGrid
package seedu.address.ui.autocomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.NextCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.appointments.AckAppCommand;
import seedu.address.logic.commands.appointments.AddAppCommand;
import seedu.address.logic.commands.appointments.AppointmentsCommand;
import seedu.address.logic.commands.appointments.CancelAppCommand;
import seedu.address.logic.commands.appointments.ChangeAppCommand;
import seedu.address.logic.commands.appointments.MissAppCommand;
import seedu.address.logic.commands.appointments.SettleAppCommand;
import seedu.address.logic.commands.duties.AddDutyShiftCommand;
import seedu.address.logic.commands.duties.CancelDutyShiftCommand;
import seedu.address.logic.commands.duties.ChangeDutyShiftCommand;
import seedu.address.logic.commands.duties.DutyShiftCommand;
import seedu.address.logic.commands.patients.EditPatientDetailsCommand;
import seedu.address.logic.commands.patients.ListPatientCommand;
import seedu.address.logic.commands.patients.RegisterPatientCommand;
import seedu.address.logic.commands.queue.AddConsultationRoomCommand;
import seedu.address.logic.commands.queue.BreakCommand;
import seedu.address.logic.commands.queue.DequeueCommand;
import seedu.address.logic.commands.queue.EnqueueCommand;
import seedu.address.logic.commands.queue.RemoveRoomCommand;
import seedu.address.logic.commands.queue.ResumeCommand;
import seedu.address.logic.commands.staff.EditStaffDetailsCommand;
import seedu.address.logic.commands.staff.ListStaffCommand;
import seedu.address.logic.commands.staff.RegisterStaffCommand;

/**
 * Component for AutoComplete
 */
public class AutoCompleter {
    private static final Map<String, Set<String>> SUPPORTED_ARGUMENTS = Map.ofEntries(
            Map.entry("editappt", Set.of("-entry", "-start", "-end")),
            Map.entry("editshift", Set.of("-entry", "-start", "-end")),
            Map.entry("editdoctor", Set.of("-entry", "-id", "-name", "-phone", "-address", "-email")),
            Map.entry("editpatient", Set.of("-entry", "-id", "-name", "-phone", "-address", "-tag", "-email")),
            Map.entry("newappt", Set.of("-id", "-rec", "-num", "-start", "-end")),
            Map.entry("newshift", Set.of("-id", "-rec", "-num", "-start", "-end")),
            Map.entry("newdoctor", Set.of("-id", "-name", "-phone", "-address", "-email")),
            Map.entry("newpatient", Set.of("-id", "-name", "-phone", "-address", "-tag", "-email"))
    );

    private static String[] SUPPORTED_COMMANDS = new String[] {
        ListPatientCommand.COMMAND_WORD,
        RegisterPatientCommand.COMMAND_WORD,
        EditPatientDetailsCommand.COMMAND_WORD,

        ListStaffCommand.COMMAND_WORD,
        RegisterStaffCommand.COMMAND_WORD,
        EditStaffDetailsCommand.COMMAND_WORD,

        ExitCommand.COMMAND_WORD,
        HelpCommand.COMMAND_WORD,

        RedoCommand.COMMAND_WORD,
        UndoCommand.COMMAND_WORD,

        EnqueueCommand.COMMAND_WORD,
        DequeueCommand.COMMAND_WORD,

        AckAppCommand.COMMAND_WORD,
        AddAppCommand.COMMAND_WORD,
        AppointmentsCommand.COMMAND_WORD,
        CancelAppCommand.COMMAND_WORD,
        ChangeAppCommand.COMMAND_WORD,
        MissAppCommand.COMMAND_WORD,
        SettleAppCommand.COMMAND_WORD,

        DutyShiftCommand.COMMAND_WORD,
        AddDutyShiftCommand.COMMAND_WORD,
        CancelDutyShiftCommand.COMMAND_WORD,
        ChangeDutyShiftCommand.COMMAND_WORD,

        AddConsultationRoomCommand.COMMAND_WORD,
        RemoveRoomCommand.COMMAND_WORD,

        NextCommand.COMMAND_WORD,
        BreakCommand.COMMAND_WORD,
        ResumeCommand.COMMAND_WORD
    };

    private static final Pattern NO_FLAG = Pattern.compile(".*\\s-\\S+\\s+$|^\\s*\\S*");
    private static final Pattern CONTINUOUS_SPACES = Pattern.compile("\\s+");

    private final Trie trie;
    private Trie customTrie;
    private String currentQuery;

    public AutoCompleter() {
        Arrays.sort(SUPPORTED_COMMANDS);
        trie = new Trie(SUPPORTED_COMMANDS);
        customTrie = null;
    }

    public AutoCompleter(String... arr) {
        this.trie = new Trie(arr);
        customTrie = null;
    }

    /**
     * Updates AutoComplete with current query.
     *
     * @param currentQuery
     * @return AutoComplete itself
     */
    public AutoCompleter update(String currentQuery) {
        currentQuery = currentQuery.stripLeading();
        if (NO_FLAG.matcher(currentQuery).matches()) {
            this.currentQuery = currentQuery;
            return this;
        }
        try {
            Set<String> result = SUPPORTED_ARGUMENTS.get(currentQuery.substring(0, currentQuery.indexOf(' ')));
            HashSet<String> available = new HashSet<>(result);
            available.removeAll(List.of(CONTINUOUS_SPACES.split(currentQuery)));
            if (result.contains("-tag")) {
                available.add("-tag");
            }
            AutoCompleter autoCompleter = new AutoCompleter(available.toArray(String[]::new));
            autoCompleter.currentQuery = currentQuery.substring(currentQuery.lastIndexOf(' ') + 1);
            return autoCompleter;
        } catch (NullPointerException e) {
            return new AutoCompleter("");
        }
    }

    public void setCustomSuggestions(List<String> listOfCommands) {
        customTrie = new Trie(listOfCommands);
    }

    public List<String> getSuggestions() {
        try {
            if (customTrie == null) {
                return trie.find(currentQuery).getPossibilities();
            }

            List<String> listOfSuggestion = customTrie.find(currentQuery).getPossibilities();
            listOfSuggestion.addAll(trie.find(currentQuery).getPossibilities());
            return listOfSuggestion;

        } catch (NullPointerException e) {
            return Collections.emptyList();
        }
    }
}
