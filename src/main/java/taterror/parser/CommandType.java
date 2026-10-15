package taterror.parser;

/**
 * The kind of command a line of user input represents, as recognized by
 * {@link Parser#parseCommandType(String)}.
 */
public enum CommandType {
    BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND, PRIORITY, UNKNOWN
}
