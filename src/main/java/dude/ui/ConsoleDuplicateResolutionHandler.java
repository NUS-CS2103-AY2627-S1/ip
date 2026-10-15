package dude.ui;

import java.util.Locale;

import dude.command.duplicate.DuplicateResolution;
import dude.command.duplicate.DuplicateResolutionHandler;
import dude.command.duplicate.DuplicateTaskConflict;

/**
 * Collects duplicate-resolution choices from the console UI.
 */
public final class ConsoleDuplicateResolutionHandler implements DuplicateResolutionHandler {
    private final Ui ui;

    /**
     * Creates a console duplicate-resolution handler.
     *
     * @param ui Console UI used for prompts and input.
     */
    public ConsoleDuplicateResolutionHandler(Ui ui) {
        this.ui = ui;
    }

    @Override
    public DuplicateResolution resolve(DuplicateTaskConflict conflict) {
        ui.showDuplicateConflict(conflict);
        while (true) {
            ui.showDuplicatePrompt();
            String choice = ui.readInputLine();
            if (choice == null) {
                return DuplicateResolution.cancel();
            }
            String normalized = choice.trim().toLowerCase(Locale.ROOT);
            if (isEditChoice(normalized)) {
                return resolveEdit(conflict);
            }
            if (isAddChoice(normalized)) {
                return DuplicateResolution.add();
            }
            if (isCancelChoice(normalized)) {
                return DuplicateResolution.cancel();
            }
            ui.showInvalidDuplicateChoice();
        }
    }

    /**
     * Resolves which matching task should be edited.
     *
     * @param conflict Duplicate task details.
     * @return Edit or cancel resolution.
     */
    private DuplicateResolution resolveEdit(DuplicateTaskConflict conflict) {
        if (conflict.matches().size() == 1) {
            return DuplicateResolution.edit(conflict.matches().get(0).index());
        }

        while (true) {
            ui.showDuplicateSelection();
            String input = ui.readInputLine();
            if (input == null) {
                return DuplicateResolution.cancel();
            }
            try {
                int selectedNumber = Integer.parseInt(input.trim());
                for (DuplicateTaskConflict.DuplicateMatch match : conflict.matches()) {
                    if (selectedNumber == match.index() + 1) {
                        return DuplicateResolution.edit(match.index());
                    }
                }
            } catch (NumberFormatException ignored) {
                // The prompt is repeated below for an invalid task number.
            }
            ui.showInvalidDuplicateSelection();
        }
    }

    /**
     * Returns whether the input is an edit choice.
     *
     * @param choice Normalized user choice.
     * @return True when the choice requests editing.
     */
    private static boolean isEditChoice(String choice) {
        return "e".equals(choice) || "ed".equals(choice) || "edi".equals(choice)
                || "edit".equals(choice);
    }

    /**
     * Returns whether the input is an add choice.
     *
     * @param choice Normalized user choice.
     * @return True when the choice requests adding.
     */
    private static boolean isAddChoice(String choice) {
        return "a".equals(choice) || "ad".equals(choice) || "add".equals(choice);
    }

    /**
     * Returns whether the input is a cancel choice.
     *
     * @param choice Normalized user choice.
     * @return True when the choice requests cancellation.
     */
    private static boolean isCancelChoice(String choice) {
        return "c".equals(choice) || "ca".equals(choice) || "can".equals(choice)
                || "canc".equals(choice) || "cance".equals(choice) || "cancel".equals(choice);
    }
}
