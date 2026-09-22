package stewie.ui.gui;

import java.util.List;

/**
 * Represents a command reply without depending on JavaFX controls.
 *
 * @param messages Assistant messages, in display order.
 * @param tasks Formatted task cards to display after the messages.
 * @param isInteractive Whether task cards may change the current list.
 */
record GuiCommandResult(List<String> messages, List<String> tasks, boolean isInteractive) {
    /**
     * Copies reply contents so rendering cannot change the command result.
     */
    GuiCommandResult {
        messages = List.copyOf(messages);
        tasks = List.copyOf(tasks);
    }

    /**
     * Creates a reply with no task cards.
     */
    static GuiCommandResult message(String message) {
        return new GuiCommandResult(List.of(message), List.of(), false);
    }
}
