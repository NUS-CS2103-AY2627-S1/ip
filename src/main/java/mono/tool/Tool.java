package mono.tool;

import mono.MonoBot;
import mono.exception.MonoException;

/**
 * Represents one user-facing tool that Mono can execute.
 */
public interface Tool {
    /**
     * Validates the arguments and performs the tool's operation.
     *
     * @param arguments text following the tool name
     * @param bot chatbot state and task operations
     * @return whether the main loop should continue or exit
     * @throws MonoException if the arguments or requested task are invalid
     */
    ToolSignal invoke(String arguments, MonoBot bot) throws MonoException;
}
