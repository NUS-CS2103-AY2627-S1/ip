package max.task;

import max.data.Status;
import max.data.TaskType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {
    private LocalDate date;
    private LocalDateTime dateTime;
    private boolean hasTime;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");

    public Deadline(String desc, LocalDate date) {
        super(desc, TaskType.DEADLINE);
        assert date != null : "A deadline must have a date";
        this.date = date;
        this.dateTime = date.atStartOfDay();
    }
    public Deadline(String desc, Status status, LocalDate date) {
        super(desc, TaskType.DEADLINE, status);
        assert date != null : "A deadline must have a date";
        this.date = date;
        this.dateTime = date.atStartOfDay();
    }

    public Deadline(String desc, LocalDateTime dateTime) {
        super(desc, TaskType.DEADLINE);
        assert dateTime != null : "A deadline must have a date";
        this.dateTime = dateTime;
        this.date = dateTime.toLocalDate();
        this.hasTime = true;
    }

    public Deadline(String desc, Status status, LocalDateTime dateTime) {
        super(desc, TaskType.DEADLINE, status);
        assert dateTime != null : "A deadline must have a date";
        this.dateTime = dateTime;
        this.date = dateTime.toLocalDate();
        this.hasTime = true;
    }

    /** Returns the date by which this task should be completed. */
    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    @Override
    public boolean isOn(LocalDate date) {
        return this.date.equals(date);
    }

    @Override
    public String getItemString() {
        
        return super.getItemString() + " | " + (hasTime ? this.dateTime : this.date);
    }
    @Override
    public String toString() {
        DateTimeFormatter displayFormatter = hasTime ? timeFormatter : formatter;
        return super.toString() + "(by: " + formatDateTime(displayFormatter) + ")";
    }

    private String formatDateTime(DateTimeFormatter displayFormatter) {
        return dateTime.format(displayFormatter).replace("Sept", "Sep");
    }
}
