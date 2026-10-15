package bern.logic;

import java.util.List;

import javafx.scene.Node;

/**
 * Encapsulates a controller response as text and JavaFX nodes for the main window to display.
 */
public abstract class Response {
    /**
     * Contains the response message, excluding task details rendered in separate nodes.
     */
    protected String text;

    /**
     * Creates a base response whose message is initialized by its concrete subclass.
     */
    public Response() {
    }

    /**
     * Creates the JavaFX nodes that display this response.
     *
     * @return The response nodes in display order.
     */
    public abstract List<Node> getResponseNodes();

    /**
     * Returns the response message without any separately rendered task details.
     *
     * @return The message, or an empty string for a response containing only a task.
     */
    public String getResponseText() {
        return text;
    }
}
