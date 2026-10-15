package luckynoslacky;

import java.util.List;
import java.util.Objects;

import luckynoslacky.luckytask.TaskView;

/**
 * Represents a response containing task data and the surrounding prose.
 *
 * @param message complete response text used by the command-line interface
 * @param leadingLines prose shown before the task cards
 * @param taskViews immutable task views shown as cards
 * @param trailingLines prose shown after the task cards
 */
public record TaskContent(
        String message,
        List<String> leadingLines,
        List<TaskView> taskViews,
        List<String> trailingLines) implements ResponseContent {
    /** Validates and defensively copies the structured response fields. */
    public TaskContent {
        Objects.requireNonNull(message, "Response message cannot be null.");
        leadingLines = List.copyOf(leadingLines);
        taskViews = List.copyOf(taskViews);
        trailingLines = List.copyOf(trailingLines);
    }
}
