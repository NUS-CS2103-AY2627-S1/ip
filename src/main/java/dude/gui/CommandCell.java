package dude.gui;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Renders one command usage with a completion hint for the highlighted row.
 */
public class CommandCell extends ListCell<String> {
    /**
     * Creates an empty command usage cell.
     */
    public CommandCell() {
    }

    @Override
    protected void updateItem(String usage, boolean empty) {
        super.updateItem(usage, empty);
        if (empty || usage == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        Label usageLabel = new Label(usage);
        usageLabel.getStyleClass().add("command-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label hint = new Label("Tab to complete");
        hint.getStyleClass().add("command-hint");

        HBox row = new HBox(usageLabel, spacer, hint);
        row.getStyleClass().add("command-row");
        setText(null);
        setGraphic(row);
    }
}
