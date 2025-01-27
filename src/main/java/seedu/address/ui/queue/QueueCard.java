//@@author SakuraBlossom
package seedu.address.ui.queue;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.ui.UiPart;

/**
 * An UI component that displays information of a {@code Person} in Queue.
 */
public class QueueCard extends UiPart<Region> {

    private static final String FXML = "queue/QueueListCard.fxml";

    public final Person person;

    @FXML
    private Label id;
    @FXML
    private Label name;
    @FXML
    private Label phone;
    @FXML
    private Label refId;

    public QueueCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().toString());
        refId.setText(person.getReferenceId().toString());
        phone.setText(person.getPhone().toString());
    }
}
