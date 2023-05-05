# <div align="center"><a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/236442451-81509618-a741-4f8a-b958-8947c7025041.png" alt="🪐 Inspectable Wrappers"></a></div>

<p align="center">
<a href="https://github.com/foldright/inspectable-wrappers/actions/workflows/ci.yml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/inspectable-wrappers/ci.yml?branch=main&logo=github&logoColor=white" alt="Github Workflow Build Status"></a>
<a href="https://app.codecov.io/gh/foldright/inspectable-wrappers/tree/main"><img src="https://img.shields.io/codecov/c/github/foldright/inspectable-wrappers/main?logo=codecov&logoColor=white" alt="Codecov"></a>
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/Java-8+-339933?logo=openjdk&logoColor=white" alt="Java support"></a>
<a href="https://www.apache.org/licenses/LICENSE-2.0.html"><img src="https://img.shields.io/github/license/foldright/inspectable-wrappers?color=4D7A97&logo=apache" alt="License"></a>
<a href="https://foldright.io/inspectable-wrappers/apidocs/"><img src="https://img.shields.io/github/release/foldright/inspectable-wrappers?label=javadoc&color=339933&logo=microsoft-academic&logoColor=white" alt="Javadocs"></a>
<a href="https://central.sonatype.com/artifact/io.foldright/inspectable-wrappers/0.3.0/versions"><img src="https://img.shields.io/maven-central/v/io.foldright/inspectable-wrappers?logo=apache-maven&logoColor=white" alt="Maven Central"></a>
<a href="https://github.com/foldright/inspectable-wrappers/releases"><img src="https://img.shields.io/github/release/foldright/inspectable-wrappers.svg" alt="GitHub Releases"></a>
<a href="https://github.com/foldright/inspectable-wrappers/stargazers"><img src="https://img.shields.io/github/stars/foldright/inspectable-wrappers" alt="GitHub Stars"></a>
<a href="https://github.com/foldright/inspectable-wrappers/fork"><img src="https://img.shields.io/github/forks/foldright/inspectable-wrappers" alt="GitHub Forks"></a>
<a href="https://github.com/foldright/inspectable-wrappers/issues"><img src="https://img.shields.io/github/issues/foldright/inspectable-wrappers" alt="GitHub Issues"></a>
<a href="https://github.com/foldright/cffu/inspectable-wrappers/contributors"><img src="https://img.shields.io/github/contributors/foldright/inspectable-wrappers" alt="GitHub Contributors"></a>
<a href="https://github.com/foldright/inspectable-wrappers"><img src="https://img.shields.io/github/repo-size/foldright/inspectable-wrappers" alt="GitHub repo size"></a>
<a href="https://gitpod.io/#https://github.com/foldright/inspectable-wrappers"><img src="https://img.shields.io/badge/Gitpod-ready to code-339933?label=gitpod&logo=gitpod&logoColor=white" alt="gitpod: Ready to Code"></a>
</p>

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/235301326-fd1c5da3-269a-4741-9851-88d57dc9034b.png" width="30%" align="right" alt="inspectable-wrappers" /></a>

The purpose of **Inspectable Wrappers** is to provide a standard for wrapper chain with inspection ability.

--------------------------------------------------------------------------------

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [🥑 Core Classes](#-core-classes)
- [🌰 Demo](#-demo)
  - [wrapper implementations in your application code](#wrapper-implementations-in-your-application-code)
  - [inspection of the wrapper chain](#inspection-of-the-wrapper-chain)
- [🍼 Java API Docs](#-java-api-docs)
- [🍪 Dependency](#-dependency)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

--------------------------------------------------------------------------------

## 🥑 Core Classes

- [`Wrapper`](src/main/java/io/foldright/inspectablewrappers/Wrapper.java) is core interface, used to
  - identifies the wrapper instances as a wrapper chain
  - provides static entry methods to inspect the wrapper chain
- [`Attachable`](src/main/java/io/foldright/inspectablewrappers/Attachable.java) interface is used to
  enhance the wrapper instances with the attachment storage ability

## 🌰 Demo

Below use the `Executor Wrapper` to demonstrate the usage.
(Runnable demo codes in project: [`Demo.java`](src/test/java/io/foldright/demo/Demo.java))

### wrapper implementations in your application code

```java
// a wrapper implementation
public class ChattyExecutorWrapper implements Executor, Wrapper<Executor> {
  private final Executor executor;

  public ChattyExecutorWrapper(Executor executor) {
    this.executor = executor;
  }

  @Override
  public void execute(Runnable command) {
    System.out.println("BlaBlaBla...");
    executor.execute(command);
  }

  @Override
  public Executor unwrap() {
    return executor;
  }
}

// another wrapper implementation
public class LazyExecutorWrapper implements Executor, Wrapper<Executor>, Attachable<String, String> {
  private final Executor executor;

  public LazyExecutorWrapper(Executor executor) {
    this.executor = executor;
  }

  @Override
  public void execute(Runnable command) {
    System.out.println("I'm lazy, sleep before work");
    sleep();

    executor.execute(command);
  }

  @Override
  public Executor unwrap() {
    return executor;
  }

  private final Attachable<String, String> attachable = new AttachableDelegate<>();

  @Override
  public void setAttachment(String key, String value) {
    attachable.setAttachment(key, value);
  }

  @Override
  public String getAttachment(String key) {
    return attachable.getAttachment(key);
  }
}
```

### inspection of the wrapper chain

```java
public class Demo {
  public static void main(String[] args) {
    ////////////////////////////////////////
    // prepare executor instance and wrappers
    ////////////////////////////////////////

    final Executor executor = Runnable::run;

    final LazyExecutorWrapper lazy = new LazyExecutorWrapper(executor);
    lazy.setAttachment("busy", "very, very busy!");

    final Executor chatty = new ChattyExecutorWrapper(lazy);

    ////////////////////////////////////////
    // inspect the wrapper chain
    ////////////////////////////////////////

    System.out.println("Is chatty executor LazyExecutor? " +
        Wrapper.isInstanceOf(chatty, LazyExecutorWrapper.class));
    // print true

    String busy = Wrapper.getAttachment(chatty, "busy");
    System.out.println("Is chatty executor busy? " + busy);
    // print "very, very busy!"

    ////////////////////////////////////////
    // call executor
    ////////////////////////////////////////

    System.out.println();
    chatty.execute(() -> System.out.println("work!"));
  }
}
```

## 🍼 Java API Docs

The current version Java API documentation: <https://foldright.io/inspectable-wrappers/apidocs/>

## 🍪 Dependency

For `Maven` projects:

```xml

<dependency>
  <groupId>io.foldright</groupId>
  <artifactId>inspectable-wrappers</artifactId>
  <version>0.3.0</version>
</dependency>
```

For `Gradle` projects:

```groovy
// Gradle Kotlin DSL
implementation("io.foldright:inspectable-wrappers:0.3.0")

// Gradle Groovy DSL
implementation 'io.foldright:inspectable-wrappers:0.3.0'
```

`inspectable-wrappers` has published to maven central, find the latest version at [central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/inspectable-wrappers/0.3.0/versions).
