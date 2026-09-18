package com.fireinyu.themyth.exceptions;

/**
 * Thrown when user attempts to disable, destroy or otherwise deface a Software Engineering quiz.
 */
public class CheekySlackerException extends TweakingException {

    /**
     * Initializes a CheekySlackerException.
     *
     * @see String
     */
    public CheekySlackerException() {
        super("You're really trying to skip software engineering? You better take that back right now!");
    }
}
