package epi;

import java.util.List;

/**
 * Carries complete response text and an explicit error flag shared by user interfaces.
 *
 * @param lines Response lines in display order.
 * @param error Whether the command failed without keeping task changes.
 */
public record CommandResult(List<String> lines, boolean error) {
    /**
     * Copies the response lines so later changes to the supplied list cannot change the result.
     *
     * @param lines Response lines in display order, with no null elements.
     * @param error Whether the command failed without keeping task changes.
     * @throws NullPointerException If the list or any response line is null.
     */
    public CommandResult {
        lines = List.copyOf(lines);
    }
}
