package benbot;

/** Represents a non-task entity that can be recreated from a stored command. */
interface StorableEntity {
    /** Returns the command that recreates this entity. */
    String toStorageString();
}
