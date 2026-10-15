package orangutan.command;

import orangutan.OrangutanException;

class ErrorChecker {
    private static int DEADLINE_PARAM_COUNT = 1;
    private static int EVENT_PARAM_COUNT = 2;

    static void checkEmptyList(Context context, String action) throws OrangutanException {
        if (context.getList().getLength() == 0) {
            throw CommandError.emptyListError(action);
        }
    }

    static void checkIndexOutOfBounds(Context context, int index) throws OrangutanException {
        if (index < 1 || index > context.getList().getLength()) {
            throw CommandError.indexOutOfBoundsError(context.getList().getLength());
        }
    }

    /**
     * Verifies that action title exists.
     *
     * @param action String array, expected to be an (action, title) pair (e.g. {"todo", "lunch"})
     * @throws OrangutanException if title is missing.
     */
    static void checkMissingTitle(String[] action) throws OrangutanException {
        if (action.length < 2) {
            throw CommandError.missingTitleError(action[0]);
        }
    }

    /**
     * Verifies that action index exists.
     *
     * @param action String array, expected to be an (action, index) pair (e.g. {"mark", "1"}
     * @throws OrangutanException if index is missing.
     */
    static void checkMissingIndex(Context context, String[] action) throws OrangutanException {
        if (action.length < 2) {
            throw CommandError.missingIndexError(action[0], context.getList().getLength());
        }
    }

    /**
     * Verifies that find command has keyword.
     *
     * @throws OrangutanException If no keyword is found.
     */
    static void checkMissingFindKeyword(String[] action) throws OrangutanException {
        if (action.length < 2) {
            throw CommandError.missingFindKeywordError();
        }
    }

    /**
     * Verifies that sort command has sorting method.
     *
     * @param action Command action, e.g. {"sort", "none"}
     * @throws OrangutanException If no sorting method is found.
     *      note that the second element in action is assumed to be the sorting method.
     */
    static void checkMissingSortMethod(String[] action) throws OrangutanException {
        if (action.length < 2) {
            throw CommandError.missingSortMethodError();
        }
    }

    /**
     * Verifies that deadline has the /by parameter.
     *
     * @param params String array, expected to contain the /by date and time string (e.g. {"20260831 1900"}).
     * @throws OrangutanException If there are insufficient parameters.
     *      note that it will ignore any extra parameters.
     */
    static void checkMissingDeadlineParam(String[] params) throws OrangutanException {
        if (params.length < DEADLINE_PARAM_COUNT) {
            throw CommandError.missingDeadlineParamError();
        }
    }

    /**
     * Verifies that event has the /from and /to parameters.
     *
     * @param params String array, expected to contain the /by and /from dates and times
     *      (e.g. {"20260831 1900", "20260831 2200"}).
     * @throws OrangutanException If there are insufficient parameters.
     *      note that it will ignore any extra parameters.
     */
    static void checkMissingEventParam(String[] params) throws OrangutanException {
        if (params.length < EVENT_PARAM_COUNT) {
            throw CommandError.missingEventParamError();
        }
    }
}
