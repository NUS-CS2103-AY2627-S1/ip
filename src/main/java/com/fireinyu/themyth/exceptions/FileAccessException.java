package com.fireinyu.themyth.exceptions;

/**
 * Thrown when the app is unable to read or write to a disk file.
 *
 * @see TweakingException
 */
public class FileAccessException extends TweakingException {

    /**
     * Initializes a FileAccessException.
     *
     * @param filename name of file which cannot be accessed.
     * @see String
     */
    public FileAccessException(String filename) {
        super(String.format(
                "Ugh, I can't even get into \"%s\" right now! Going strictly into memory-only diva mode! 💅",
                filename));
    }
}
