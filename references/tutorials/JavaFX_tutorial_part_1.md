---
title: "JavaFX tutorial part 1"
source: "https://se-education.org/guides/tutorials/javaFxPart1.html"
author:
published:
created: 2026-09-04
description:
tags:
  - "clippings"
---
## Guides for SE student projects »

## JavaFX tutorial part 1 – Getting started

This tutorial takes you through the steps of building a typical Java FX application, using a chatbot application called Duke as a running example. Given below is what the end result can look like if you follow this tutorial until the end:

<video width="700px" controls=""><source src="https://se-education.org/guides/tutorials/videos/javafx/DynamicStyleDemo.mp4" type="video/mp4"></video>

Let's get started!

## Setting up the project

1. Ensure you are using JDK 25. **Mac users need to use [this precise distribution of the JDK 25](https://se-education.org/guides/tutorials/javaInstallationMac.html)** which comes bundled with JavaFX support.
2. Fork [this starter repo](https://github.com/se-edu/javafx-tutorial), and clone it onto your computer.
3. Open the project in your favorite code editor. If you are using an IDE, configure it to use the JDK 25 ([how to configure JDK in Intellij IDEA](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk)).
4. Continue to the section below to configure the project to use JavaFX

## Setting up Java FX

Note: this tutorial assumes you will be using [Gradle](https://se-education.org/guides/tutorials/gradle.html) to manage dependencies of your project.

Update your `build.gradle` to include the following lines:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    String javaFxVersion = '17.0.7'

    implementation("org.openjfx:javafx-base:${javaFxVersion}:win")
    implementation("org.openjfx:javafx-base:${javaFxVersion}:mac")
    implementation("org.openjfx:javafx-base:${javaFxVersion}:linux")
    implementation("org.openjfx:javafx-controls:${javaFxVersion}:win")
    implementation("org.openjfx:javafx-controls:${javaFxVersion}:mac")
    implementation("org.openjfx:javafx-controls:${javaFxVersion}:linux")
    implementation("org.openjfx:javafx-fxml:${javaFxVersion}:win")
    implementation("org.openjfx:javafx-fxml:${javaFxVersion}:mac")
    implementation("org.openjfx:javafx-fxml:${javaFxVersion}:linux")
    implementation("org.openjfx:javafx-graphics:${javaFxVersion}:win")
    implementation("org.openjfx:javafx-graphics:${javaFxVersion}:mac")
    implementation("org.openjfx:javafx-graphics:${javaFxVersion}:linux")
}
```

Also note the following:

Gradle Tutorial → After updating the `build.gradle` file (extract)

## Writing your first Java FX program

A JavaFX application is like a play you are directing. Instead of creating props, you create `Node` s (`Node` s are the fundamental building blocks of a JavaFX application), and place them onto a `Scene` (a scene is a graph of `Node` s). Then, you set your `Scene` on a `Stage` provided by JavaFX. When you call `Stage#show()` method, JavaFX renders a window with your `Stage` on it.

![](https://se-education.org/guides/tutorials/images/javafx/JavaFxHierarchy.png)

More specifically,

- the `Stage` is like a window in a desktop application. It is the top-level container for a JavaFX application.
- a `Scene` is a container for `Node` s. A stage contains multiple scenes, and shows different scenes at different times based on programme state, user actions, etc.
- a `Node` is a component that can be added to a `Scene`. `Node` s can be simple controls like `Label`, `Button`, etc., or complex controls like `TableView`, `TreeView`, etc. A `Node` can contain other `Node` s too.
- a `Root Node` is the topmost `Node` in a `Scene`, the parent of all other `Node` s. It is usually a pane like `StackPane`, `BorderPane`, etc.

Well, that's a very high-level view of how JavaFX works. The actual implementation of a Java FX is a bit more (ahem...) "involved". Not to worry; we'll tackle that one step at a time.

As is customary, let’s start off with a simple “Hello World” program. Let's say you have a class named `Duke` that you want to make a GUI for. Let's call this GUI class `Main`. When using Java FX, this GUI class needs to extend `javafx.application.Application` which in turn requires you to override the abstract `Application#start(Stage)` method and provide a concrete implementation. The parameter `Stage` is the *primary stage* that JavaFX provides.

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label helloWorld = new Label("Hello World!"); // Creating a new Label control
        Scene scene = new Scene(helloWorld); // Setting the scene to be our Label

        stage.setScene(scene); // Setting the stage to show our scene
        stage.show(); // Render the stage.
    }
}
```

Note how we have created a `Label` to contain the text that we want to show. We then create the `Scene` and set its content. Finally, we set the stage and show it.

Next, we create another Java class, `Launcher`, as an entry point to our application (this class is needed to work around a classpath issue -- we can ignore the reason for now, but if you want to know more, you can refer to [this commit from another project](https://github.com/se-edu/addressbook-level3/commit/12bb91903e71ea1109e04f7369c2169f1c7be39a)).

The `Launcher` class is reproduced below in its entirety.

```java
import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
```

Now that we have changed the entry point to our application, we need to update the `mainClass` attribute in `build.gradle` accordingly to point at the `Launcher` class.

```groovy
// ...

application {
   mainClass.set("Launcher")
}

// ...
```

Now, run the application (e.g., run `./gradlew run` command in the terminal) and you should see something like this:

![](https://se-education.org/guides/tutorials/images/javafx/HelloWorld.png)

Congratulations! You have created your first GUI application!

[**ToC**](https://se-education.org/guides/tutorials/javaFx.html) | **What's next?** [JavaFX tutorial part 2 - **Creating a GUI for Duke**](https://se-education.org/guides/tutorials/javaFxPart2.html)

---

**Authors:**

- Initial Version: Jeffry Lum