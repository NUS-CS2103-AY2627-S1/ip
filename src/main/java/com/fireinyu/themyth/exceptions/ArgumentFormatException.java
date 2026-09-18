package com.fireinyu.themyth.exceptions;

/**
 * Thrown when the user provides badly-formatted input arguments.
 */
public class ArgumentFormatException extends TweakingException {

    /**
     * Initializes a ArgumentFormatException caused by a malformed argument to a Request.
     *
     * @param type name of type of argument that is malformed.
     * @param badArg malformed argument.
     * @param format expected argument format.
     * @see String
     * @see com.fireinyu.themyth.requests.Request
     */
    public ArgumentFormatException(String type, String badArg, String format) {
        super(String.format(
                "Babe, your %s input \"%s\" is giving total chaos! Serve it to me in %s format or not at all! 💁‍♀️",
                type, badArg, format));
    }
}
