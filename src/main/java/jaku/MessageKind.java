package jaku;

/** Classifies a command result so each user interface can present it appropriately. */
public enum MessageKind {
    /** A normal Jaku reply, including confirmations and the farewell. */
    REPLY,

    /** A response explaining invalid input or an unavailable capability. */
    ERROR
}
