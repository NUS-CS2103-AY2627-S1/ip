package brucli.task;

import java.time.LocalDateTime;

/**
 * Describes a date condition applied by the list command.
 */
public record ListFilter(Type type, LocalDateTime boundary) {
    /**
     * Supported directions for comparing task dates with the boundary.
     */
    public enum Type {
        BEFORE,
        AFTER
    }
}
