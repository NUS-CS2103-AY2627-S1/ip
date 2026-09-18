package com.fireinyu.themyth.exceptions;

import java.util.Collection;

/**
 * Thrown when the user provides the wrong number or names of input arguments.
 */
public class ArugmentMismatchException extends TweakingException {

    /**
     * Initializes an ArugmentMismatchException caused by wrong number of positional arguments supplied in a Request.
     *
     * @param expectedPosCount expected number of positional arguments.
     * @param givenPosCount number of positional arguments supplied.
     * @see com.fireinyu.themyth.requests.Request
     */
    public ArugmentMismatchException(int expectedPosCount, int givenPosCount) {
        super(String.format(
                "Excuse me? I asked for %d juicy arguments and you dared hand me %d? The audacity! 🙄",
                expectedPosCount, givenPosCount));
    }

    /**
     * Initializes an ArugmentMismatchException caused by wrong set of keyword arguments supplied in a Request.
     *
     * @param expectedKwargs expected keyword arguments.
     * @param givenKwargs keyword arguments supplied.
     * @see com.fireinyu.themyth.requests.Request
     */
    public ArugmentMismatchException(Collection<String> expectedKwargs, Collection<String> givenKwargs) {
        super(String.format(
                "Sweetie, I ordered {%s}, but you handed me {%s}? That is NOT what was on the menu! 💅",
                String.join(", ", expectedKwargs),
                String.join(", ", givenKwargs)
        ));
    }
}
