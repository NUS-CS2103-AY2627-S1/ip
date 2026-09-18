package com.fireinyu.themyth.exceptions;

/**
 * Thrown when an argument has an unexpected type.
 */
public class WrongTypeException extends TweakingException {
    /**
     * Initializes an exception for an unexpected argument type.
     *
     * @param type the expected argument type.
     */
    public WrongTypeException(Class<?> type) {
        super(String.format("Oh darling, wrong vibe entirely! I specifically requested a %s! 💅", type.toString()));
    }
}
