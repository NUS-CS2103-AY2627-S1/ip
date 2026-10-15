package sanyueqi.ui;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;

class Deadline extends Task {
    private String dueDate;

    public Deadline(String desc) {
        super(false, desc);
    }

    public Deadline(boolean done, String desc, String dueDate) {
        super(done, desc);
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return String.format("[D][%s] %s (by: %s)", super.isDone() ? "X" : " ", super.getDesc(), this.dueDate);
    }

    /**
     * Serializes a deadline for writing to a CSV.
     *
     * @return The serialized string.
     */
    @Override
    public String toCsv() {
        return String.format("D,%s,%s,%s", super.isDone() ? "1" : "0", super.serialise(super.getDesc()), super.serialise(this.dueDate));
    }

    /**
     * Makes a 'deadline' type Task.
     *
     * @param parts The parts of the deadline.
     * @return A deadline instance.
     */
    public static Deadline makeDeadline(String[] parts) throws SYQException {
        StringBuilder desc = new StringBuilder();
        int i = 1;
        while (i < parts.length) {
            if (parts[i].equals("/by")) {
                if (desc.isEmpty()) {
                    throw new SYQException("Sorry! Description cannot be empty!");
                }
                desc.deleteCharAt(desc.length() - 1);
                if (i == parts.length - 1) {
                    throw new SYQException("Sorry! Deadline cannot be empty!");
                }
                Deadline deadline = new Deadline(desc.toString());
                StringBuilder dueDate = new StringBuilder();
                for (int j = i + 1; j < parts.length; j++) {
                    dueDate.append(parts[j]);
                    if (j < parts.length - 1) dueDate.append(" ");
                }
                String formattedDueDate;
                try {
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
                    formattedDueDate = LocalDate.parse(dueDate.toString(), inputFormatter).format(outputFormatter);
                } catch (DateTimeParseException e) {
                    formattedDueDate = dueDate.toString();
                }
                deadline.dueDate = formattedDueDate;
                return deadline;
            } else {
                desc.append(parts[i]);
            }
            desc.append(" ");
            i += 1;
        }
        if (i == parts.length) {
            throw new SYQException("Sorry! You didn't indicate the deadline!");
        }
        assert false;
        return null;
    }
}