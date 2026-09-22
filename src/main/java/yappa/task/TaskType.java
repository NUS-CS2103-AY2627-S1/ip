package yappa.task;

import yappa.exception.YappaException;

/** Identifies the task subtype used in storage records. */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String code;

    TaskType(String code) {
        this.code = code;
    }

    /**
     * Returns the storage code for this task type.
     *
     * @return Storage code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the task type represented by a storage code.
     *
     * @param code Storage code.
     * @return Matching task type.
     * @throws YappaException If the code is unknown.
     */
    public static TaskType fromStorageCode(String code) throws YappaException {
        for (TaskType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new YappaException("Unknown task type: " + code);
    }
}
