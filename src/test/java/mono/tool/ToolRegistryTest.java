package mono.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import mono.exception.UnknownToolException;

/**
 * Tests resolution of built-in command names.
 */
public class ToolRegistryTest {
    @Test
    public void get_knownToolNames_returnsMatchingTools() throws UnknownToolException {
        ToolRegistry registry = new ToolRegistry();

        assertInstanceOf(ByeTool.class, registry.get("bye"));
        assertInstanceOf(DeleteTool.class, registry.get("delete"));
        assertInstanceOf(DeadlineTool.class, registry.get("deadline"));
        assertInstanceOf(EventTool.class, registry.get("event"));
        assertInstanceOf(FindTasksTool.class, registry.get("find"));
        assertInstanceOf(ListTool.class, registry.get("list"));
        assertInstanceOf(MarkTool.class, registry.get("mark"));
        assertInstanceOf(TodoTool.class, registry.get("todo"));
        assertInstanceOf(UnmarkTool.class, registry.get("unmark"));
    }

    @Test
    public void get_unknownToolName_throwsUnknownToolException() {
        UnknownToolException exception = assertThrows(
                UnknownToolException.class, () -> new ToolRegistry().get("remind"));

        assertEquals("Tool is unknown.", exception.getMessage());
    }
}
