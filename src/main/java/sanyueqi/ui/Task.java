package sanyueqi.ui;

abstract class Task {
    private String desc;
    private boolean done;

    public Task(boolean done, String desc) {
        this.desc = desc;
        this.done = done;
    }

    public String getDesc() {
        return this.desc;
    }

    public boolean isDone() {
        return this.done;
    }

    public void markDone(boolean done) {
        this.done = done;
    }

    /**
     * Serializes a description.
     *
     * @param s The string to serialize.
     * @return The serialized string.
     */
    public String serialise(String s) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    /**
     * Serializes a task for writing to a CSV.
     *
     * @return The serialized string.
     */
    public abstract String toCsv();
}