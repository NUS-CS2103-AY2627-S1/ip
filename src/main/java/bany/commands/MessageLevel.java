package bany.commands;

/** Indicates how a response message should be presented to the user. */
public enum MessageLevel {
    /** Normal information or confirmation. */
    INFO,
    /** Nonfatal issue that needs the user's attention. */
    WARNING,
    /** Failure that prevented an operation from completing. */
    ERROR,
}
