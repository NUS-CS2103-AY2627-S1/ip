package elsa.task;

/**
 * One unfinished deadline the user should be reminded about, together with
 * where it sits in the list and how soon it is due.
 *
 * <p>The position is kept so that a reminder can show the number the task has
 * in the full list, which is the number "mark" and "delete" expect. The days
 * until it is due are worked out once, against the date the reminders were
 * gathered for, so that sorting them and wording them use the same answer.
 *
 * @param index        the deadline's position in the full list, counted from 0
 * @param deadline     the deadline itself
 * @param daysUntilDue how many days from that date it is due; negative when overdue
 */
public record Reminder(int index, Deadline deadline, long daysUntilDue) {
}
