package sanyueqi.ui;

class FixedDuration extends Task {
    private String duration;

    public FixedDuration(String desc) {
        super(false, desc);
    }

    public FixedDuration(boolean done, String desc, String duration) {
        super(done, desc);
        this.duration = duration;
    }

    @Override
    public String toString() {
        return String.format("[F][%s] %s (duration: %s)", super.isDone() ? "X" : " ", super.getDesc(), this.duration);
    }

    /**
     * Serializes a deadline for writing to a CSV.
     *
     * @return The serialized string.
     */
    @Override
    public String toCsv() {
        return String.format("F,%s,%s,%s", super.isDone() ? "1" : "0", super.serialise(super.getDesc()), super.serialise(this.duration));
    }

    /**
     * Makes a 'deadline' type Task.
     *
     * @param parts The parts of the deadline.
     * @return A deadline instance.
     */
    public static FixedDuration makeDuration(String[] parts) throws SYQException {
        StringBuilder desc = new StringBuilder();
        int i = 1;
        while (i < parts.length) {
            if (parts[i].equals("/duration")) {
                if (desc.isEmpty()) {
                    throw new SYQException("Sorry! Description cannot be empty!");
                }
                desc.deleteCharAt(desc.length() - 1);
                if (i == parts.length - 1) {
                    throw new SYQException("Sorry! Duration cannot be empty!");
                }
                FixedDuration fixedDuration = new FixedDuration(desc.toString());
                StringBuilder duration = new StringBuilder();
                for (int j = i + 1; j < parts.length; j++) {
                    duration.append(parts[j]);
                    if (j < parts.length - 1) duration.append(" ");
                }
                String formattedDuration;
                try {
                    String[] nums = duration.toString().split(":");
                    if (nums.length != 2) throw new Exception();
                    int hours = Integer.parseInt(nums[0]);
                    int mins = Integer.parseInt(nums[1]);
                    while (mins < 0) {
                        mins += 60;
                        hours -= 1;
                    }
                    while (mins > 60) {
                        mins -= 60;
                        hours += 1;
                    }
                    formattedDuration = String.format("%dh %dmin", hours, mins);
                } catch (Exception e) {
                    formattedDuration = duration.toString();
                }
                fixedDuration.duration = formattedDuration;
                return fixedDuration;
            } else {
                desc.append(parts[i]);
            }
            desc.append(" ");
            i += 1;
        }
        if (i == parts.length) {
            throw new SYQException("Sorry! You didn't indicate the duration!");
        }
        assert false;
        return null;
    }
}