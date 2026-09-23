package max.task;
import max.data.TaskType;
import max.data.Status;
import max.maxexception.InvalidDateRangeException;
import max.maxexception.MaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Event extends Task {
    private LocalDate start;
    private LocalDate end;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean hasTime;

    private static final DateTimeFormatter DISPLAY =
            DateTimeFormatter.ofPattern("MMM dd yyyy");


    public Event(String desc, LocalDate start, LocalDate end) throws MaxException {
        super(desc, TaskType.EVENT);
        validateDates(start, end);
        this.start = start;
        this.end = end;
        this.startTime = start.atStartOfDay();
        this.endTime = end.atStartOfDay();

    }

    public Event(String desc, Status status, LocalDate start, LocalDate end) throws MaxException {
        super(desc, TaskType.EVENT, status);
        validateDates(start, end);
        this.start = start;
        this.end = end;
        this.startTime = start.atStartOfDay();
        this.endTime = end.atStartOfDay();
    }

    public Event(String desc, LocalDateTime start, LocalDateTime end) throws MaxException {
        super(desc, TaskType.EVENT);
        validateDateTimes(start, end);
        this.startTime = start;
        this.endTime = end;
        this.start = start.toLocalDate();
        this.end = end.toLocalDate();
        this.hasTime = true;
    }

    public Event(String desc, Status status, LocalDateTime start, LocalDateTime end) throws MaxException {
        super(desc, TaskType.EVENT, status);
        validateDateTimes(start, end);
        this.startTime = start;
        this.endTime = end;
        this.start = start.toLocalDate();
        this.end = end.toLocalDate();
        this.hasTime = true;
    }

    private static void validateDateTimes(LocalDateTime start, LocalDateTime end) throws MaxException {
        if (start == null || end == null || end.isBefore(start)) {
            throw new InvalidDateRangeException();
        }
    }

    public LocalDateTime getStartDateTime() {
        return startTime;
    }

    public LocalDateTime getEndDateTime() {
        return endTime;
    }

    private static void validateDates(LocalDate start, LocalDate end) throws MaxException {
        if (start == null || end == null) {
            throw new InvalidDateRangeException();
        }
        if (end.isBefore(start)) {
            throw new InvalidDateRangeException();
        }
    }

    @Override
    public boolean isOn(LocalDate date) {
        return !date.isBefore(start) && !date.isAfter(end);
    }
    
    @Override
    public String getItemString() {
        return super.getItemString() + " | " + (hasTime ? this.startTime : this.start)
                + " | " + (hasTime ? this.endTime : this.end);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = hasTime
                ? DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm") : DISPLAY;
        return super.toString()
                    + "(from: " + formatStart(formatter)
                    + " to: " + formatEnd(formatter) + ")";
    }

    private String formatStart(DateTimeFormatter formatter) {
        return (hasTime ? startTime.format(formatter) : start.format(formatter)).replace("Sept", "Sep");
    }

    private String formatEnd(DateTimeFormatter formatter) {
        return (hasTime ? endTime.format(formatter) : end.format(formatter)).replace("Sept", "Sep");
    }
    
}
