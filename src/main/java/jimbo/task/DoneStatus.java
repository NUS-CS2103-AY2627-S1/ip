package jimbo.task;

/**
 * Represents whether a {@link Task} has been completed.
 * Used instead of a bare {@code boolean} so that call sites (e.g.
 * {@code setTaskDone(tasks, indexArg, DoneStatus.DONE)}) are self-explanatory
 * without needing to check the method signature.
 */
public enum DoneStatus {
    DONE,
    NOT_DONE
}
