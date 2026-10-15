package dude.command.duplicate;

/**
 * Collects a user's choice for a duplicate-task conflict.
 */
@FunctionalInterface
public interface DuplicateResolutionHandler {
    /**
     * Requests a resolution for the supplied conflict.
     *
     * @param conflict Duplicate task details.
     * @return User-selected resolution.
     */
    DuplicateResolution resolve(DuplicateTaskConflict conflict);
}
