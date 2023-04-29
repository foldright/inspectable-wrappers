# 🪐 Wrain: Inspectable Wrapper Chain

<p align="center">
<a href="https://github.com/foldright/wrain/actions/workflows/ci.yml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/wrain/ci.yml?branch=main&logo=github&logoColor=white" alt="Github Workflow Build Status"></a>
<a href="https://app.codecov.io/gh/foldright/wrain/tree/main"><img src="https://img.shields.io/codecov/c/github/foldright/wrain/main?logo=codecov&logoColor=white" alt="Codecov"></a>
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/Java-8+-339933?logo=openjdk&logoColor=white" alt="Java support"></a>
<a href="https://www.apache.org/licenses/LICENSE-2.0.html"><img src="https://img.shields.io/github/license/foldright/wrain?color=4D7A97&logo=apache" alt="License"></a>
<a href="https://foldright.io/wrain/apidocs/"><img src="https://img.shields.io/github/release/foldright/wrain?label=javadoc&color=339933&logo=microsoft-academic&logoColor=white" alt="Javadocs"></a>
<a href="https://central.sonatype.com/artifact/io.foldright/inspectable-wrapper-chain/0.1.0/versions"><img src="https://img.shields.io/maven-central/v/io.foldright/inspectable-wrapper-chain?logo=apache-maven&logoColor=white" alt="Maven Central"></a>
<a href="https://github.com/foldright/wrain/releases"><img src="https://img.shields.io/github/release/foldright/wrain.svg" alt="GitHub Releases"></a>
<a href="https://github.com/foldright/wrain/stargazers"><img src="https://img.shields.io/github/stars/foldright/wrain" alt="GitHub Stars"></a>
<a href="https://github.com/foldright/wrain/fork"><img src="https://img.shields.io/github/forks/foldright/wrain" alt="GitHub Forks"></a>
<a href="https://github.com/foldright/wrain/issues"><img src="https://img.shields.io/github/issues/foldright/wrain" alt="GitHub Issues"></a>
<a href="https://github.com/foldright/cffu/wrain/contributors"><img src="https://img.shields.io/github/contributors/foldright/wrain" alt="GitHub Contributors"></a>
<a href="https://github.com/foldright/wrain"><img src="https://img.shields.io/github/repo-size/foldright/wrain" alt="GitHub repo size"></a>
<a href="https://gitpod.io/#https://github.com/foldright/wrain"><img src="https://img.shields.io/badge/Gitpod-ready to code-339933?label=gitpod&logo=gitpod&logoColor=white" alt="gitpod: Ready to Code"></a>
</p>

**Wrain**(Inspectable Wrapper Chain), a nano library provides the interfaces
to make wrapper instances as an **inspectable wrapper chain**.

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

- [`Wrapper`](src/main/java/io/foldright/wrain/Wrapper.java) is core interface, used to
  - enhance the wrapper instances as a wrapper chain
  - provides methods to inspect the wrapper chain.
- [`Attachable`](src/main/java/io/foldright/wrain/Attachable.java) interface is used to
  enhance the wrapper instances with the attachment storage ability.

## 🌰 Demo

Below use the `Executor Wrapper` to demonstrate the usage of `wrain` lib.  
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
  public Executor wrainUnwrap() {
    return executor;
  }
}

// another wrapper implementation
public class LazyExecutorWrapper implements Executor, Wrapper<Executor>, Attachable {
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
  public Executor wrainUnwrap() {
    return executor;
  }

  private final Attachable attachable = new AttachableDelegate();

  @Override
  public void wrainSet(String key, Object value) {
    attachable.wrainSet(key, value);
  }

  @Override
  public <V> V wrainGet(String key) {
    return attachable.wrainGet(key);
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
    lazy.wrainSet("busy", "very very busy!");

    final Executor chatty = new ChattyExecutorWrapper(lazy);

    ////////////////////////////////////////
    // inspect the wrapper chain
    ////////////////////////////////////////

    System.out.printf("chatty executor is LazyExecutor? %s\n",
        Wrapper.isInstanceOf(chatty, LazyExecutorWrapper.class));
    // print true

    String busy = Wrapper.getAttachment(chatty, "busy");
    System.out.printf("chatty executor is busy? %s\n", busy);
    // print true

    ////////////////////////////////////////
    // call executor
    ////////////////////////////////////////

    System.out.println();
    chatty.execute(() -> System.out.println("work!"));
  }
}
```

## 🍼 Java API Docs

The current version Java API documentation: <https://foldright.io/wrain/apidocs/>

## 🍪 Dependency

For `Maven` projects:

```xml

<dependency>
  <groupId>io.foldright</groupId>
  <artifactId>inspectable-wrapper-chain</artifactId>
  <version>0.1.0</version>
</dependency>
```

For `Gradle` projects:

```groovy
// Gradle Kotlin DSL
implementation("io.foldright:inspectable-wrapper-chain:0.1.0")

// Gradle Groovy DSL
implementation 'io.foldright:inspectable-wrapper-chain:0.1.0'
```

`wrain` has published to maven central, find the latest version at [central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/inspectable-wrapper-chain/0.1.0/versions).
