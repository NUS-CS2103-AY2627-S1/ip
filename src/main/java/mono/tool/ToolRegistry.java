package mono.tool;

import java.util.Map;

import mono.exception.UnknownToolException;

/**
 * Stores and resolves the tools available to Mono.
 */
public class ToolRegistry {
    private final Map<String, Tool> tools;

    /**
     * Creates a registry containing all built-in Mono tools.
     */
    public ToolRegistry() {
        this.tools = Map.of(
                "bye", new ByeTool(),
                "delete", new DeleteTool(),
                "deadline", new DeadlineTool(),
                "event", new EventTool(),
                "find", new FindTasksTool(),
                "list", new ListTool(),
                "mark", new MarkTool(),
                "todo", new TodoTool(),
                "unmark", new UnmarkTool()
        );
    }

    /**
     * Finds a tool by the name entered by the user.
     *
     * @param name tool name
     * @return matching tool
     * @throws UnknownToolException if no tool has the given name
     */
    public Tool get(String name) throws UnknownToolException {
        Tool tool = this.tools.get(name);
        if (tool == null) {
            throw new UnknownToolException("Tool is unknown.");
        }
        return tool;
    }
}
