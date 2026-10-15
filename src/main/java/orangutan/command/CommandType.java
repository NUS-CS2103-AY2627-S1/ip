package orangutan.command;

import orangutan.OrangutanException;

/**
 * Enum type denoting different types of methods, how their respective errors are to be checked,
 * and what parameters are to be passed to each of them.
 */
public enum CommandType {
    INIT {
        /**
         * Runs InitCommand, but only upon program startup.
         * @param action String array comprised of command name and title / index.
         * @param params String array comprised of any other parameters (e.g. from, by).
         * @param context Context in which to run command.
         * @return The chatbot's String reply.
         * @throws OrangutanException If user tries to call it in the chat.
         */
        public String checkAndRun(String[] action, String[] params, Context context) throws OrangutanException {
            if (!context.isRun()) {
                return new InitCommand().run(context);
            } else {
                throw CommandError.unknownCommandError();
            }
        }
    },

    TODO {
        /**
         * Verifies TodoCommand input values and runs the command.
         * @param action String array comprised of command name and title / index.
         * @param params String array comprised of any other parameters (e.g. from, by).
         * @param context Context in which to run command.
         * @return The chatbot's String reply.
         * @throws OrangutanException If ErrorChecker.checkMissingTitle() fails.
         */
        public String checkAndRun(String[] action, String[] params, Context context) throws OrangutanException {
            ErrorChecker.checkMissingTitle(action);
            String todoTitle = action[1];
            return new TodoCommand(todoTitle, false).run(context);
        }
    },

    DEADLINE {
        /**
         * Verifies DeadlineCommand input values and runs the command.
         * @param action String array comprised of command name and title / index.
         * @param params String array comprised of any other parameters (e.g. from, by).
         * @param context Context in which to run command.
         * @return The chatbot's String reply.
         * @throws OrangutanException If ErrorChecker.checkMissingTitle() fails or no by parameter given.
         */
        public String checkAndRun(String[] action, String[] params, Context context) throws OrangutanException {
            ErrorChecker.checkMissingTitle(action);
            ErrorChecker.checkMissingDeadlineParam(params);

            String by = params[0];
            String deadlineTitle = action[1];
            return new DeadlineCommand(deadlineTitle, by, false).run(context);
        }
    },

    EVENT {
        /**
         * Verifies EventCommand input values and runs the command.
         * @param action String array comprised of command name and title / index.
         * @param params String array comprised of any other parameters (e.g. from, by).
         * @param context Context in which to run command.
         * @return The chatbot's String reply.
         * @throws OrangutanException If ErrorChecker.checkMissingTitle() fails or from and/or to parameter missing.
         */
        public String checkAndRun(String[] action, String[] params, Context context) throws OrangutanException {
            ErrorChecker.checkMissingTitle(action);
            ErrorChecker.checkMissingEventParam(params);

            String from = params[0];
            String to = params[1];
            String eventTitle = action[1];
            return new EventCommand(eventTitle, from, to, false).run(context);
        }
    },

    LIST {
        /**
         * Runs listCommand.
         * @param action String array comprised of command name and title / index.
         * @param params String array comprised of any other parameters (e.g. from, by).
         * @param context Context in which to run command.
         * @return The chatbot's String reply.
         * @throws OrangutanException Never.
         */
        public String checkAndRun(String[] action, String[] params, Context context) throws OrangutanException {
            return new ListCommand().run(context);
        }
    },

    FIND {
        /**
         * Verifies FindCommand input values and runs the command.
         * @param action String array comprised of command name and title / index.
         * @param params String array comprised of any other parameters (e.g. from, by).
         * @param context Context in which to run command.
         * @return The chatbot's String reply.
         * @throws OrangutanException If ErrorChecker.VerifyActionIndex() fails.
         */
        public String checkAndRun(String[] action, String[] params, Context context) throws OrangutanException {
            ErrorChecker.checkMissingFindKeyword(action);
            return new FindCommand(action[1]).run(context);
        }
    },

    DELETE {
        /**
         * Verifies DeleteCommand input values and runs the command.
         * @param action String array comprised of command name and title / index.
         * @param params String array comprised of any other parameters (e.g. from, by).
         * @param context Context in which to run command.
         * @return The chatbot's String reply.
         * @throws OrangutanException If ErrorChecker.VerifyActionIndex() fails.
         */
        public String checkAndRun(String[] action, String[] params, Context context) throws OrangutanException {
            ErrorChecker.checkMissingIndex(context, action);
            return new DeleteCommand(action[1]).run(context);
        }
    },

    MARK {
        /**
         * Verifies MarkCommand input values and runs the command.
         * @param action String array comprised of command name and title / index.
         * @param params String array comprised of any other parameters (e.g. from, by).
         * @param context Context in which to run command.
         * @return The chatbot's String reply.
         * @throws OrangutanException If ErrorChecker.VerifyActionIndex() fails.
         */
        public String checkAndRun(String[] action, String[] params, Context context) throws OrangutanException {
            ErrorChecker.checkMissingIndex(context, action);
            return new MarkCommand(action[1]).run(context);
        }
    },

    UNMARK {
        /**
         * Verifies UnmarkCommand input values and runs the command.
         * @param action String array comprised of command name and title / index.
         * @param params String array comprised of any other parameters (e.g. from, by).
         * @param context Context in which to run command.
         * @return The chatbot's String reply.
         * @throws OrangutanException If ErrorChecker.VerifyActionIndex() fails.
         */
        public String checkAndRun(String[] action, String[] params, Context context) throws OrangutanException {
            ErrorChecker.checkMissingIndex(context, action);
            return new UnmarkCommand(action[1]).run(context);
        }
    },

    SORT {
        /**
         * Verifies SortCommand input values and runs the command.
         * @param action String array comprised of command name and title / index.
         * @param params String array comprised of any other parameters (e.g. from, by).
         * @param context Context in which to run command.
         * @return The chatbot's String reply.
         * @throws OrangutanException If no sorting method provided.
         */
        public String checkAndRun(String[] action, String[] params, Context context) throws OrangutanException {
            ErrorChecker.checkMissingSortMethod(action);
            return new SortCommand(action[1]).run(context);
        }
    },

    BYE {
        /**
         * Runs ByeCommand.
         * @param action String array comprised of command name and title / index.
         * @param params String array comprised of any other parameters (e.g. from, by).
         * @param context Context in which to run command.
         * @return The chatbot's String reply.
         * @throws OrangutanException Never.
         */
        public String checkAndRun(String[] action, String[] params, Context context) throws OrangutanException {
            return new ByeCommand().run(context);
        }
    };

    public abstract String checkAndRun(String[] action, String[] params, Context context) throws OrangutanException;
}
