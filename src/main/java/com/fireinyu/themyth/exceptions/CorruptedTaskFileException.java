package com.fireinyu.themyth.exceptions;

/**
 * Thrown when attempting to read a disk file with corrupted data.
 */
public class CorruptedTaskFileException extends TweakingException {

    /**
     * Initializes a CorruptedTaskFileException.
     *
     * @param filename name of file with corrupted data.
     * @see String
     */
    public CorruptedTaskFileException(String filename) {
        super(String.format(
                "Ew! File \"%s\" is a total disaster zone of corrupted nonsense! Memory-only diva mode activated! 💋",
                filename));
    }
}
