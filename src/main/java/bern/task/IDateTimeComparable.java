package bern.task;

import bern.datetime.DateTime;

/**
 * Represents objects ordered by a reference date and optional time.
 */
public interface IDateTimeComparable {
    /**
     * Returns the reference date and optional time used to order this object.
     *
     * @return The reference date and optional time.
     */
    DateTime getReferenceDateTime();

    /**
     * Compares this object's reference date and time with another object's reference.
     * On the same date, an omitted time sorts before any specified time.
     *
     * @param timeComparable The object to compare with.
     * @return A negative value, zero, or a positive value when this reference is earlier, equal, or later.
     */
    int compareDateTime(IDateTimeComparable timeComparable);
}
