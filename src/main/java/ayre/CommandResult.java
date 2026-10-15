package ayre;

import ayre.enums.AyreStatus;

/**
 * A record to pass the reply message of the completed Command and the resulting process status.
 * Record message is passed to the GUI via Ayre::getResponse().
 *
 * @param message Contextual information or a request to print something via a Command.
 * @param status Signal to the process to CONTINUE or TERMINATE.
 */
public record CommandResult(String message, AyreStatus status) {}
