package laby.command;

import java.time.LocalDateTime;

/** Stores the parsed action and arguments for one user command. */
public class Command {
    private final CommandType commandType;
    private final int id;
    private final String description;
    private final String phone;
    private final String email;
    private final LocalDateTime firstTime;
    private final LocalDateTime secondTime;

    /**
     * Creates a command with its optional task, contact, and time arguments.
     *
     * @param commandType Type of command.
     * @param id Zero-based task index.
     * @param description Task description, if applicable.
     * @param phone Contact phone number, if applicable.
     * @param email Contact email address, if applicable.
     * @param firstTime First time argument, if applicable.
     * @param secondTime Second time argument, if applicable.
     */
    public Command(CommandType commandType, int id, String description,
                   String phone, String email, LocalDateTime firstTime,
                   LocalDateTime secondTime) {
        assert commandType != null : "command type must be present";

        switch (commandType) {
            case BYE, LIST -> assertNoArguments(id, description, phone, email, firstTime, secondTime);
            case MARK, UNMARK, DELETE_TASK, DELETE_CONTACT -> {
                assert id >= 0 : "command index must be non-negative";
                assert description == null && phone == null && email == null
                        && firstTime == null && secondTime == null
                        : "task modification commands must not have extra arguments";
            }
            case TODO, FIND -> {
                assert description != null && !description.isBlank()
                        : "description must be present";
                assert phone == null && email == null && firstTime == null && secondTime == null
                        : "text commands must not have time arguments";
            }
            case DEADLINE -> {
                assert description != null && !description.isBlank()
                        : "description must be present";
                assert phone == null && email == null && firstTime != null && secondTime == null
                        : "deadline must have exactly one time argument";
            }
            case EVENT -> {
                assert description != null && !description.isBlank()
                        : "description must be present";
                assert phone == null && email == null && firstTime != null && secondTime != null
                        : "event must have two time arguments";
            }
            case CONTACT -> {
                assert id == 0 : "contact commands must use the default index";
                assert description != null && !description.isBlank()
                        : "contact name must be present";
                assert phone != null && !phone.isBlank() : "contact phone must be present";
                assert email != null && !email.isBlank() : "contact email must be present";
                assert firstTime == null && secondTime == null
                        : "contact commands must not have time arguments";
            }
            case UNKNOWN -> {
                assert false : "unknown commands must not be constructed";
            }
            default -> {
                assert false : "command type is not handled";
            }
        }

        this.commandType = commandType;
        this.id = id;
        this.description = description;
        this.phone = phone;
        this.email = email;
        this.firstTime = firstTime;
        this.secondTime = secondTime;
    }

    /**
     * Verifies that a command without arguments has no argument values.
     *
     * @param id Task index value stored in the command.
     * @param description Description value stored in the command.
     * @param phone Phone value stored in the command.
     * @param email Email value stored in the command.
     * @param firstTime First time value stored in the command.
     * @param secondTime Second time value stored in the command.
     */
    private static void assertNoArguments(int id, String description,
                                          String phone, String email,
                                          LocalDateTime firstTime, LocalDateTime secondTime) {
        assert id == 0 : "argument-free commands must use the default task index";
        assert description == null && phone == null && email == null
                && firstTime == null && secondTime == null
                : "argument-free commands must not have arguments";
    }

    /**
     * Returns the command's type.
     *
     * @return Command type.
     */
    public CommandType getCommandType() {
        return this.commandType;
    }

    /**
     * Returns the zero-based task index associated with the command.
     *
     * @return Zero-based task index.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns the task description, when one was supplied.
     *
     * @return Task description, or {@code null} when none was supplied.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the phone number associated with a contact command.
     *
     * @return Contact phone number, or {@code null} when not applicable.
     */
    public String getPhone() {
        return this.phone;
    }

    /**
     * Returns the email address associated with a contact command.
     *
     * @return Contact email address, or {@code null} when not applicable.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Returns the first time argument, when one was supplied.
     *
     * @return First time argument, or {@code null} when none was supplied.
     */
    public LocalDateTime getFirstTime() {
        return this.firstTime;
    }

    /**
     * Returns the second time argument, when one was supplied.
     *
     * @return Second time argument, or {@code null} when none was supplied.
     */
    public LocalDateTime getSecondTime() {
        return this.secondTime;
    }
}
