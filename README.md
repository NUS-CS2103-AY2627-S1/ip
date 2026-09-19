# Carl · Night-Shift Dispatcher

Carl is a keyboard-first desktop task manager presented as a Telegram-style conversation. It tracks todos, deadlines, and events, highlights command errors clearly, and saves changes locally.

![Carl's Telegram-style interface](docs/Ui.png)

- [User guide](docs/README.md)
- [Published product website](https://aster0.github.io/ip/)
- [GitHub releases](https://github.com/Aster0/ip/releases)

## Running Carl

Carl requires Java 25. JavaFX is included in the packaged application, so it does not need to be installed separately.

1. Download `carl.jar` from the latest GitHub release.
2. Place it in an empty folder where Carl can create `save.txt`.
3. Open a terminal in that folder.
4. Run:

   ```console
   java -jar carl.jar
   ```

See the [user guide](docs/README.md) for all commands and troubleshooting advice.

## Developing Carl

Use JDK 25 and open this repository as a Gradle project in IntelliJ IDEA. Run the `carl.Carl` main class to start the GUI from the IDE.

Useful Gradle commands:

```console
./gradlew test
./gradlew check
./gradlew shadowJar
```

On Windows, replace `./gradlew` with `gradlew.bat`. The packaged application is created at `build/libs/carl.jar`.

Keep Java source files under `src/main/java` and resources under `src/main/resources`, as expected by Gradle.

## AI assistance

Gemini and ChatGPT were used for the following work:

- Javadocs
- JavaFX UI
- [A-BetterGUI](https://github.com/Aster0/ip/commit/31484754b8bc298961c3d77eac9fe492ee3f6edd)
- [A-Personality](https://github.com/Aster0/ip/commit/4f126cef58584a7c64b191d1ef9977ce5283b599)
- [A-MoreErrorHandling](https://github.com/Aster0/ip/commit/ec72e8d23a3112303c1e8efa07dacb43795d5641)
- [A-MoreTesting](https://github.com/Aster0/ip/commit/f1882af9db74de5b8f99bab00a95506ee3508c68)
- [A-UserGuide](https://github.com/Aster0/ip/commit/3f1f11fda4f2fef036c83ee92ad5d1618d5890a9)
