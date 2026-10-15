# Marquee

[![CLI Build](https://github.com/vutuanlong07/ip/actions/workflows/build-cli.yaml/badge.svg)](https://github.com/vutuanlong07/ip/releases)
[![GUI Build](https://github.com/vutuanlong07/ip/actions/workflows/build-gui.yaml/badge.svg)](https://github.com/vutuanlong07/ip/releases)

## Overview

Marquee is a task-keeping assistant that tries to mimic natural language to give you a smooth conversation.

This repo comes with a CLI build out of the box for Marquee.

For more information, go to the [project website](https://vutuanlong07.github.io/ip/)

## Table of Contents

* [Features](#features)
* [Installation](#installation)
* [Usage](#usage)

## Features

- [x] Manage tasks, deadlines and events
- [x] Search for upcoming tasks
- [x] Update tasks
- [x] Batch task operations
- [x] Uses CSV file format compatible with spreadsheet programs
- [x] (CLI) Recognize many date-time formats
- [x] (GUI) Create tags on-the-fly and add tags seamlessly

## Installation

All downloadables are located in [GitHub Release](https://github.com/vutuanlong07/ip/releases).

### CLI application

#### Use the JAR (cross-platform)

1. Make sure you have installed Java Runtime 25+
2. Download the JAR from Releases
3. Open the folder containing the JAR in your command prompt
4. Run the following command:

```bash
java -jar marquee-cli-v1.2.0-jar.jar
```

#### Use the platform-specific binaries

1. Download the binary for your platform
2. Run the binary normally (in file explorer, double-click or right click then select "Open")

### GUI application

1. Make sure you have installed Java Runtime 25+
2. Download the JAR from Releases
3. Open the folder containing the JAR in your command prompt
4. Run the following command:

```bash
java -jar marquee-cli-v1.1.0-crossplatform.jar
```

### API package

1. Download the JAR from Releases
2. Add the following to your `build.gradle` or `build.gradle.kts`:

#### Groovy

```groovy
dependencies {
    implementation files('/path/to/jar/marquee-core-v1.0.0.jar')
}
```

#### Kotlin

```kotlin
dependencies {
    implementation(files("/path/to/jar/marquee-core-v1.0.0.jar"))
}
```
