---
name: build-jar
description: Build and verify the distributable fat JAR for this chatbot with Gradle's Shadow plugin, so that JavaFX is bundled inside and the app runs on machines without JavaFX installed. Use when asked to generate/create/rebuild the JAR file, to do the A-Jar increment, to check that dependencies are bundled correctly, to check why the JAR is under 5MB or fails with "JavaFX runtime components are missing", or to package the app for release.
---

# Building the distributable JAR

The deliverable is a single *fat* JAR (also called an uber JAR or shadow JAR):
one file that holds the app's own classes **and** every library it depends on.
A plain `jar` task produces a *lean* JAR with only the app's classes, so it
fails on any computer that does not already have JavaFX installed.

## Why JavaFX needs special care

JavaFX is not part of the JDK any more. It ships as ordinary Maven artifacts
that are **platform-specific**: each module has a separate `:win`, `:mac` and
`:linux` classifier, because each one carries native libraries (`.dll`,
`.dylib`, `.so`) for that operating system. Declaring all three classifiers
makes the JAR run anywhere; declaring only your own would give a JAR that works
on your laptop and nowhere else.

## The current setup

`build.gradle` already contains everything this increment needs. Read it before
changing anything — the job is usually to verify, not to re-add:

* The `com.gradleup.shadow` plugin, which contributes the `shadowJar` task.
* JavaFX `base`, `controls`, `fxml` and `graphics`, each declared three times
  (`:win`, `:mac`, `:linux`).
* `application { mainClass = 'davidgoggins.Launcher' }`, which Shadow copies
  into the JAR manifest as `Main-Class`.
* `jar { enabled = false }`, which switches off the lean JAR nobody needs. It
  still needs `archiveClassifier = 'without-dependencies'`, because Gradle
  refuses to let two tasks write the same output file even when one is disabled.
* `shadowJar { archiveFileName = 'davidgoggins.jar' }` for a clean filename
  instead of the default `ip-1.0-SNAPSHOT-all.jar`.

`Launcher` must not extend `Application`. JavaFX refuses to start when the class
extending `Application` is the one launched from the classpath, so `Launcher`
calls `Application.launch(Main.class, args)` instead. Do not "simplify" this
away — the JAR stops working, even though `./gradlew run` still succeeds.

## Running it

Use Java 25. On macOS, switch first if needed:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew clean shadowJar
```

The JAR lands at `build/libs/davidgoggins.jar`.

## Verifying it

Run the checks in one go from the repository root:

```bash
bash .claude/skills/build-jar/scripts/verify-jar.sh
```

It fails loudly if any of these is not true:

1. **Size is at least 5 MB.** A smaller JAR means JavaFX was left out. Expect
   roughly 10-11 MB here, since three platforms' natives are included.
2. **`Main-Class` is set** in `META-INF/MANIFEST.MF`.
3. **JavaFX classes are present** (`javafx/**` entries).
4. **Native libraries for all three platforms are present** (`.dll`, `.dylib`,
   `.so`).
5. **The natives are built for this machine's CPU.** See the next section --
   entry names alone do not prove this, so the check unpacks them and runs
   `file`.

## The CPU architecture trap

`:win`, `:mac` and `:linux` are **x86-64** builds. ARM machines -- every Apple
Silicon Mac -- need the separate `:mac-aarch64` and `:linux-aarch64`
classifiers.

You cannot simply add them. Both `javafx-graphics:mac` and
`javafx-graphics:mac-aarch64` contain a file called `libglass.dylib`, at the
same path. A fat JAR is flat, so Shadow keeps whichever it merges first and
silently drops the other. One fat JAR therefore supports **one CPU
architecture per operating system**.

Pick the architecture your users actually have, by swapping the classifier
rather than adding to it:

```groovy
// Apple Silicon instead of Intel Macs:
implementation("org.openjfx:javafx-graphics:${javaFxVersion}:mac-aarch64")
```

This is easy to miss, because a JDK that bundles JavaFX -- such as SDKMAN's
`25.0.3.fx-zulu` -- loads its *own* JavaFX modules from the runtime image in
preference to anything on the classpath. The app then runs on your machine no
matter what the JAR contains. To see what a user with a plain JDK would get,
exclude the JDK's copy:

```bash
java --limit-modules java.se -jar build/libs/davidgoggins.jar
```

A mismatch shows up as `UnsatisfiedLinkError: ... incompatible architecture
(have 'x86_64', need 'arm64')`.

The more advanced fix, if you genuinely need every platform, is one JAR per
platform: a separate Gradle configuration per classifier and a `shadowJar` task
per target. That is standard for real distribution, but it is more machinery
than this project needs.

Then launch it by hand, from a directory that is *not* the project, to prove it
does not quietly depend on anything left in the build tree:

```bash
cd /tmp && java -jar ~/Desktop/repos/ip/build/libs/davidgoggins.jar
```

The GUI window should open. Send a command, confirm the reply, and close it.

## Interpreting failures

| Symptom | Cause |
| --- | --- |
| JAR under 5 MB | JavaFX dependencies missing, or `jar` built instead of `shadowJar` |
| `JavaFX runtime components are missing` | main class extends `Application`; launch via `Launcher` |
| `no main manifest attribute` | `application { mainClass }` not set |
| Works on your machine only | only one platform classifier declared |
| `UnsatisfiedLinkError`, `incompatible architecture` | wrong CPU classifier; see the trap above |
| Runs locally but fails for others | your JDK bundles JavaFX and masks the problem |

## Committing

`build/` is git-ignored, so the JAR itself is never committed — only build
configuration is. If `build.gradle` did need a change, follow the
`seedu-git-standard` skill for the commit message. Do not commit unless asked.
