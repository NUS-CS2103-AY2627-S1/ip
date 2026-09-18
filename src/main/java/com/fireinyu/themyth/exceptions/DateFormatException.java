package com.fireinyu.themyth.exceptions;

import com.fireinyu.themyth.Defaults;

/**
 * Thrown when the user provides badly-formatted date input arguments.
 */
public class DateFormatException extends ArgumentFormatException {

    /**
     * Initializes a DateFormatException caused by a malformed datetime argument to a Request.
     *
     * @param badArg malformed datetime String.
     * @see String
     * @see com.fireinyu.themyth.requests.Request
     * @see com.fireinyu.themyth.util.MythDateTime
     */
    public DateFormatException(String badArg) {
        super("date", badArg, Defaults.DATE_INPUTFORMAT);
    }
}
