package yourhand;

/** Contains the user-facing response and outcome of one command execution. */
public record ExecutionResult(String message, boolean successful) {
}
