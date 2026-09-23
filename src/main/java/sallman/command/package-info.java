/**
 * The things the user can ask the chatbot to do.
 * <p>
 * {@link sallman.command.CommandType} lists the keywords that are recognized.
 * {@link sallman.command.Command} is one request, with a subclass per command
 * that carries itself out, so the main loop can run any command without
 * knowing which one it holds.
 */
package sallman.command;
