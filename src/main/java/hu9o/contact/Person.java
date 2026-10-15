package hu9o.contact;

import hu9o.task.TaskList;

/**
 * Represents one person in Hu9o's contact network: their name, contact
 * details, minimal biodata, and their own todo list.
 */
public class Person {
    private final String name;
    private final String phone;
    private final String email;
    private final String dob;
    private final String notes;
    private final TaskList tasks;

    /**
     * Creates a person with an empty todo list.
     *
     * @param name  the person's name; used to address them in select/link/connections.
     * @param phone the person's phone number.
     * @param email the person's email address.
     * @param dob   the person's date of birth, or {@code ""} if not given.
     * @param notes free-text notes about the person, or {@code ""} if not given.
     */
    public Person(String name, String phone, String email, String dob, String notes) {
        // Nothing here is user-optional in the Java sense: absent biodata arrives
        // as "", never null, so every field is required to be non-null.
        assert name != null && phone != null && email != null && dob != null && notes != null
                : "Person fields must never be null; use \"\" for an absent optional field";
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.dob = dob;
        this.notes = notes;
        this.tasks = new TaskList();
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getDob() {
        return dob;
    }

    public String getNotes() {
        return notes;
    }

    /**
     * Returns this person's own todo list.
     *
     * @return the live, mutable task list belonging to this person.
     */
    public TaskList getTasks() {
        return tasks;
    }

    /**
     * Returns this person as a pipe-delimited record for the save file, such
     * as {@code John Tan|91234567|john@example.com||}. Their tasks are saved
     * separately, in their own file.
     *
     * @return the persistence record for this person.
     */
    public String compressionString() {
        return name + "|" + phone + "|" + email + "|" + dob + "|" + notes;
    }

    @Override
    public String toString() {
        StringBuilder details = new StringBuilder(name)
                .append(" (Phone: ").append(phone)
                .append(", Email: ").append(email);
        if (!dob.isEmpty()) {
            details.append(", DOB: ").append(dob);
        }
        if (!notes.isEmpty()) {
            details.append(", Notes: ").append(notes);
        }
        return details.append(")").toString();
    }
}
