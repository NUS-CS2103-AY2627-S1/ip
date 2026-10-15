module org.cs2103t.marquee.cli {
    requires transitive org.cs2103t.marquee.core;

    exports org.cs2103t.marquee.cli.task;
    exports org.cs2103t.marquee.cli.command;
    exports org.cs2103t.marquee.cli;

    opens org.cs2103t.marquee.cli.task to org.cs2103t.marquee.core;
}
