# Packaged GUI smoke checks

Use Java 25 and a desktop session with JavaFX support. Build `shadowJar`, then run:

```text
python test/gui/run_gui_smoke.py
```

The runner uses the packaged JAR and temporary working directories. It tests
Send and Enter submission, search numbering, marking/deleting, malformed commands,
corrupt-file startup, failed saves, rollback, and recovery. Windows are transparent
and closed automatically. Real task data is not read or written.

These checks require a graphical environment; the regular Gradle JUnit suite
and documented console UI tests do not.
