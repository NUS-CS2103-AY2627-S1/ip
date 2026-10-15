package dude.gui;

import java.util.Optional;
import java.util.stream.Collectors;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

import dude.command.duplicate.DuplicateResolution;
import dude.command.duplicate.DuplicateResolutionHandler;
import dude.command.duplicate.DuplicateTaskConflict;

/**
 * Collects duplicate-resolution choices through JavaFX modal dialogs.
 */
public class DialogDuplicateResolutionHandler implements DuplicateResolutionHandler {
    /**
     * Creates a handler that uses JavaFX dialogs for duplicate resolution.
     */
    public DialogDuplicateResolutionHandler() {
    }

    @Override
    public DuplicateResolution resolve(DuplicateTaskConflict conflict) {
        String matches = conflict.matches().stream()
                .map(match -> String.format("%d. %s", match.index() + 1, match.task()))
                .collect(Collectors.joining(System.lineSeparator()));
        ButtonType edit = new ButtonType("Edit", ButtonBar.ButtonData.YES);
        ButtonType add = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Duplicate task");
        dialog.setHeaderText("DUDE found an existing task with the same description.");
        dialog.setContentText(matches);
        dialog.getButtonTypes().setAll(edit, add, cancel);

        Optional<ButtonType> choice = dialog.showAndWait();
        if (choice.isEmpty() || choice.get() == cancel) {
            return DuplicateResolution.cancel();
        }
        if (choice.get() == add) {
            return DuplicateResolution.add();
        }
        if (conflict.matches().size() == 1) {
            return DuplicateResolution.edit(conflict.matches().get(0).index());
        }

        ChoiceDialog<Integer> selection = new ChoiceDialog<>(
                conflict.matches().get(0).index() + 1,
                conflict.matches().stream().map(match -> match.index() + 1).toList());
        selection.setTitle("Choose task to edit");
        selection.setHeaderText("Which matching task should DUDE edit?");
        selection.setContentText("Task number:");
        Optional<Integer> selected = selection.showAndWait();
        if (selected.isEmpty()) {
            return DuplicateResolution.cancel();
        }
        return DuplicateResolution.edit(selected.get() - 1);
    }
}
