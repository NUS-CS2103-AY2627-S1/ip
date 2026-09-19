package yawned.alias;

/** Represents the outcome of an alias creation or removal request. */
public enum AliasResult {
    SUCCESS,
    INVALID_NAME,
    RESERVED_NAME,
    INVALID_TARGET,
    NOT_FOUND,
    SAVE_FAILED
}
