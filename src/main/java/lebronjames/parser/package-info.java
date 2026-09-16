/**
 * Turning what the user typed into a command object.
 *
 * <p>{@link lebronjames.parser.Parser} holds all the string handling in the
 * application: finding the command word, splitting arguments apart, and
 * reporting anything it cannot make sense of. Everything downstream therefore
 * receives ready-made values and never inspects raw input.
 */
package lebronjames.parser;
