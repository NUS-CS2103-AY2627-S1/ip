package ayre;

import ayre.enums.AyreStatus;
import ayre.enums.ReplyType;

/**
 * A record to pass process or GUI relevant information from Ayre to MainWindow to render.
 *
 * @param message The result message from running the user-provided command.
 * @param type The message type to distinguish which dialog box should be rendered.
 * @param status Next process status after running the command.
 */
public record AyreResponse(String message, ReplyType type, AyreStatus status) {}
