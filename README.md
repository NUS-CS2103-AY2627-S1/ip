# EV

EV is a task tracker you talk to: type a line, press Enter, and it keeps your todos,
deadlines and events, saved automatically between runs.

It runs as a JavaFX window, and the original text interface is still there for anyone who
prefers a terminal.

**[User Guide](https://Dai-yangjie.github.io/ip/)** · **[Download](https://github.com/Dai-yangjie/ip/releases)**

## Running it

Prerequisite: **JDK 25**.

```
gradlew run            # the window
gradlew shadowJar      # build build/libs/ev.jar, then: java -jar "ev.jar"
```

To use the text interface instead:

```
gradlew compileJava
java -cp build\classes\java\main ev.EV
```

Tasks are saved in `data/ev.txt`, relative to the folder EV is started from. The folder is
created on the first save, so the JAR needs nothing else to work.

## Working on the code

| Command | What it does |
| --- | --- |
| `gradlew build` | Compiles, runs the JUnit tests, and checks the coding standard |
| `gradlew test` | Runs the JUnit tests only |
| `gradlew checkstyleMain checkstyleTest` | Checks the coding standard only |

The text-interface regression tests are separate from Gradle. In PowerShell:

```
.\test\run-ui-tests.ps1
```

That replays every case in [test/ui-test-plan.md](test/ui-test-plan.md) against a freshly
compiled app and compares the output line by line.

### Setting up in IntelliJ

1. Open IntelliJ, close any open project, and choose `Open`.
2. Select the project directory and accept the defaults.
3. Configure the project to use **JDK 25**, with **Project language level** set to
   `SDK default`.
4. Locate `src/main/java/ev/gui/Launcher.java`, right-click it and choose
   `Run Launcher.main()`.

**Warning:** keep `src\main\java` as the source root. Gradle and the other tools look for
Java files there.

## Acknowledgements

**AI assistance.** This project was written with Claude (Anthropic) used as a pair
programmer throughout: the assistant proposed and wrote most of the Java code, the JUnit
tests, the text-interface test plan and this documentation, working from the requirements
of each iP increment, while I chose the design direction, reviewed each change, and made
all the Git commits. The use was widespread rather than localised, so it is cited here
rather than in individual code comments, as the course policy on reuse directs.

**Project template and tutorials.** The starting repository, the `build.gradle` used for
JavaFX, and the Checkstyle configuration come from course materials:
[se-edu/duke](https://github.com/se-edu/duke),
the [JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html), and
[se-edu/addressbook-level3](https://github.com/se-edu/addressbook-level3).

**Images.** The avatars and the wallpaper in `src/main/resources/images/` are Spider-Man
fan art found on Xiaohongshu:

- `ev.png` — https://xhslink.cn/o/1vL5DNY3g2J
- `user.png` — https://xhslink.cn/o/35MHV6dKihQ
- `wallpaper.jpg` — https://xhslink.cn/o/1sNoAZy2RSS

Spider-Man and related characters are trademarks of Marvel. These images are not released
under a free licence; they are used here only in a non-commercial student project.
