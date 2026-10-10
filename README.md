# Zsiggy

A Java 25 task-management chatbot with a JavaFX GUI, persistent todos, deadlines, and events.

- [User Guide](https://luziyue525.github.io/ip/)
- [Download](https://github.com/luziyue525/ip/releases/latest)

## Build and test

Use JDK 25. Run `./gradlew clean check shadowJar` (Windows: `gradlew.bat clean check shadowJar`).
The distributable is `build/libs/zsiggy.jar`. Launch it with `java -jar build/libs/zsiggy.jar`.

Tests use temporary folders and do not modify your saved tasks. See [the test plan](tests/test-plan.md).

## Credits

Based on the [SE-EDU iP template](https://github.com/se-edu/duke) and
[JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html).
JavaFX's [NativeLibLoader](https://github.com/openjdk/jfx/blob/jfx17/modules/javafx.graphics/src/main/java/com/sun/glass/utils/NativeLibLoader.java)
informed the architecture-specific Mac library loading.

Thanks to nje14, ooixs, and kimjunkuno for the smoke-test reports in
[forum issue 443](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/443).
AI assistance was used for the final error-handling, GUI, packaging, regression-test, and documentation improvements.
