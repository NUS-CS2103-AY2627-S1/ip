package ben;

/** A task without any date/time attached, e.g. "visit new theme park". */
class Todo extends Task {
    Todo(String description) {
        super(description);
    }

    @Override
    String getTypeIcon() {
        return "T";
    }
}
