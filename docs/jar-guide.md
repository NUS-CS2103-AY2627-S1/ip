# Creating and Running the Fat JAR File

This guide explains how to build and run the Gunna application as a standalone JAR file.

## What is a Fat JAR?

A fat JAR (also called an uber JAR) is a single JAR file that contains:
- All your compiled application classes
- All dependencies bundled inside
- A manifest file specifying the main class

This makes the JAR file completely self-contained and executable without needing to set up a classpath.

## Configuration

The project uses the Gradle Shadow plugin to create fat JARs. The configuration in `build.gradle` includes:

```gradle
plugins {
    id 'com.gradleup.shadow' version '9.5.1'  // Shadow plugin for fat JARs
}

application {
    mainClass = 'Launcher'  // Entry point of the application
}

shadowJar {
    archiveFileName = 'gunna.jar'  // Output file name
    manifest {
        attributes 'Main-Class': 'Launcher'  // Makes JAR executable
    }
}
```

## How to Create the Fat JAR

Run the following command in the project root directory:

```bash
./gradlew shadowJar
```

This command:
1. Compiles all Java source files
2. Packages compiled classes and dependencies into a single JAR
3. Creates the JAR file with the proper manifest

## Locating the JAR File

After building, the fat JAR file is located at:

```
build/libs/gunna.jar
```

The file size is approximately 24KB (may vary based on dependencies).

## Running the JAR File

To run the application, use:

```bash
java -jar build/libs/gunna.jar
```

You can also run it from any directory by providing the full or relative path:

```bash
java -jar /path/to/project/build/libs/gunna.jar
```

## Distributing the JAR

The `gunna.jar` file is completely self-contained and can be:
- Copied to any location
- Shared with others
- Run on any system with Java 21+ installed

Example distribution:
```bash
# Copy to a distribution folder
cp build/libs/gunna.jar ~/Desktop/

# Run from the new location
java -jar ~/Desktop/gunna.jar
```

## Cleaning and Rebuilding

To ensure a fresh build:

```bash
./gradlew clean shadowJar
```

This removes all previously compiled files before building the new JAR.

## Troubleshooting

**Issue**: JAR doesn't run or shows "no main manifest attribute"
- **Solution**: Ensure the `manifest` block in `shadowJar` configuration includes the Main-Class attribute

**Issue**: JAR file not found after build
- **Solution**: Check that the build was successful and look in `build/libs/` directory

**Issue**: Application doesn't start
- **Solution**: Verify you have Java 21 or higher installed: `java -version`
