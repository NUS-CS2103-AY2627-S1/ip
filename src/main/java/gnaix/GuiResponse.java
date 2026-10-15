package gnaix;

import java.util.Collections;
import java.util.List;

/**
 * Represents a Gnaix response with optional structured task data for the GUI.
 */
public class GuiResponse {
    private final String text;
    private final String heading;
    private final List<TaskDisplay> tasks;
    private final String footer;
    private final boolean isError;

    private GuiResponse(String text, String heading,
                        List<TaskDisplay> tasks, String footer, boolean isError) {
        this.text = text;
        this.heading = heading;
        this.tasks = List.copyOf(tasks);
        this.footer = footer;
        this.isError = isError;
    }

    /**
     * Creates a plain text response with no structured task content.
     *
     * @param text Response text.
     * @return GUI response containing only text.
     */
    public static GuiResponse plain(String text) {
        return new GuiResponse(text, text, Collections.emptyList(), "", false);
    }

    /**
     * Creates an error response with no structured task content.
     *
     * @param text Error text.
     * @return GUI response marked as an error.
     */
    public static GuiResponse error(String text) {
        return new GuiResponse(text, text, Collections.emptyList(), "", true);
    }

    /**
     * Creates a response with task entries for rich GUI rendering.
     *
     * @param text Plain text response used by non-structured displays.
     * @param heading Text shown before the task cards.
     * @param tasks Tasks to display as cards.
     * @param footer Text shown after the task cards.
     * @return GUI response containing structured task data.
     */
    public static GuiResponse withTasks(String text, String heading,
                                        List<TaskDisplay> tasks, String footer) {
        return new GuiResponse(text, heading, tasks, footer, false);
    }

    public String getText() {
        return text;
    }

    public String getHeading() {
        return heading;
    }

    public List<TaskDisplay> getTasks() {
        return tasks;
    }

    public String getFooter() {
        return footer;
    }

    /**
     * Returns whether this response should use error styling.
     *
     * @return True if this response represents an error.
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Returns whether this response has task cards to render.
     *
     * @return True if at least one task is available.
     */
    public boolean hasTasks() {
        return !tasks.isEmpty();
    }
}
