# <div align="center"><a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/236442451-81509618-a741-4f8a-b958-8947c7025041.png" alt="🪐 Inspectable Wrappers"></a></div>

<p align="center">
<a href="https://github.com/foldright/inspectable-wrappers/actions/workflows/ci.yml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/inspectable-wrappers/ci.yml?branch=main&logo=github&logoColor=white" alt="Github Workflow Build Status"></a>
<a href="https://app.codecov.io/gh/foldright/inspectable-wrappers/tree/main"><img src="https://img.shields.io/codecov/c/github/foldright/inspectable-wrappers/main?logo=codecov&logoColor=white" alt="Codecov"></a>
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/Java-8+-339933?logo=openjdk&logoColor=white" alt="Java support"></a>
<a href="https://www.apache.org/licenses/LICENSE-2.0.html"><img src="https://img.shields.io/github/license/foldright/inspectable-wrappers?color=4D7A97&logo=apache" alt="License"></a>
<a href="https://foldright.io/inspectable-wrappers/apidocs/"><img src="https://img.shields.io/github/release/foldright/inspectable-wrappers?label=javadoc&color=339933&logo=microsoft-academic&logoColor=white" alt="Javadocs"></a>
<a href="https://central.sonatype.com/artifact/io.foldright/inspectable-wrappers/0.3.0/versions"><img src="https://img.shields.io/maven-central/v/io.foldright/inspectable-wrappers?logo=apache-maven&logoColor=white" alt="Maven Central"></a>
<a href="https://github.com/foldright/inspectable-wrappers/releases"><img src="https://img.shields.io/github/release/foldright/inspectable-wrappers.svg" alt="GitHub Releases"></a>
<a href="https://github.com/foldright/inspectable-wrappers/stargazers"><img src="https://img.shields.io/github/stars/foldright/inspectable-wrappers?style=flat" alt="GitHub Stars"></a>
<a href="https://github.com/foldright/inspectable-wrappers/fork"><img src="https://img.shields.io/github/forks/foldright/inspectable-wrappers?style=flat" alt="GitHub Forks"></a>
<a href="https://github.com/foldright/inspectable-wrappers/issues"><img src="https://img.shields.io/github/issues/foldright/inspectable-wrappers" alt="GitHub Issues"></a>
<a href="https://github.com/foldright/cffu/inspectable-wrappers/contributors"><img src="https://img.shields.io/github/contributors/foldright/inspectable-wrappers" alt="GitHub Contributors"></a>
<a href="https://github.com/foldright/inspectable-wrappers"><img src="https://img.shields.io/github/repo-size/foldright/inspectable-wrappers" alt="GitHub repo size"></a>
<a href="https://gitpod.io/#https://github.com/foldright/inspectable-wrappers"><img src="https://img.shields.io/badge/Gitpod-ready to code-339933?label=gitpod&logo=gitpod&logoColor=white" alt="gitpod: Ready to Code"></a>
</p>

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/235301326-fd1c5da3-269a-4741-9851-88d57dc9034b.png" width="30%" align="right" alt="inspectable-wrappers" /></a>

The purpose of **Inspectable Wrappers** is to provide a standard for wrapper chain with the inspection ability.

--------------------------------------------------------------------------------

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [🥑 Core Classes](#-core-classes)
- [🌰 Usage Demo](#-usage-demo)
  - [wrapper implementations in your application code](#wrapper-implementations-in-your-application-code)
  - [inspection of the wrapper chain](#inspection-of-the-wrapper-chain)
- [🌰 Integration Demo](#-integration-demo)
  - [the demo existed wrapper cannot be modified](#the-demo-existed-wrapper-cannot-be-modified)
  - [the integration demo](#the-integration-demo)
- [🍼 Java API Docs](#-java-api-docs)
- [🍪 Dependency](#-dependency)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

--------------------------------------------------------------------------------

## 🥑 Core Classes

- [`Wrapper`](src/main/java/io/foldright/inspectablewrappers/Wrapper.java) is core interface, used to
  - identify the wrapper instances as a wrapper chain
  - provide static entry methods to inspect the wrapper chain
- [`Attachable`](src/main/java/io/foldright/inspectablewrappers/Attachable.java) interface is used to
  enhance the wrapper instances with the attachment storage ability

## 🌰 Usage Demo

Below use the `Executor Wrapper` to demonstrate the usage.

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
    System.out.println("I'm lazy, sleep before work.");
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
    final Executor executor = buildExecutorChain();

    ////////////////////////////////////////
    // inspect the executor(wrapper chain)
    ////////////////////////////////////////

    System.out.println("Is executor lazy? " +
        Wrapper.isInstanceOf(executor, LazyExecutorWrapper.class));
    // print true

    String busy = Wrapper.getAttachment(executor, "busy");
    System.out.println("Is executor busy? " + busy);
    // print "very, very busy!"

    ////////////////////////////////////////
    // call executor(wrapper chain)
    ////////////////////////////////////////

    System.out.println();
    executor.execute(() -> System.out.println("I'm working."));
  }

  /**
   * prepare executor instances/wrappers, build the executor/wrapper chain
   **/
  private static Executor buildExecutorChain() {
    final Executor base = Runnable::run;

    final LazyExecutorWrapper lazy = new LazyExecutorWrapper(base);
    lazy.setAttachment("busy", "very, very busy!");

    return new ChattyExecutorWrapper(lazy);
  }
}

/*
demo output:

Is executor lazy? true
Is executor busy? very, very busy!

BlaBlaBla...
I'm lazy, sleep before work.
I'm working.
 */
```

> Runnable demo codes in project: [`Demo.java`](src/test/java/io/foldright/demo/Demo.java)

## 🌰 Integration Demo

Integrate an existed executor wrapper without modification.

### the demo existed wrapper cannot be modified

```java
public class ExistedExecutorWrapper implements Executor {
  private final Executor executor;

  public ExistedExecutorWrapper(Executor executor) {
    this.executor = executor;
  }

  public Executor getExecutor() {
    return executor;
  }

  @Override
  public void execute(Runnable command) {
    System.out.println("I'm existed executor, have nothing to do with ~inspectable~wrappers~.");
    executor.execute(command);
  }
}
```

### the integration code

```java
public class IntegrationDemo {
  public static void main(String[] args) {
    final Executor executor = buildExecutorChain();

    ////////////////////////////////////////
    // inspect the executor(wrapper chain)
    ////////////////////////////////////////

    System.out.println("Is executor chatty? " +
        Wrapper.isInstanceOf(executor, ChattyExecutorWrapper.class));
    // print true
    System.out.println("Is executor IntegrateExistedExecutor? " +
        Wrapper.isInstanceOf(executor, IntegrateExistedExecutorWrapper.class));
    // print true

    ////////////////////////////////////////
    // call executor(wrapper chain)
    ////////////////////////////////////////

    System.out.println();
    executor.execute(() -> System.out.println("I'm working."));
  }

  private static Executor buildExecutorChain() {
    final Executor base = Runnable::run;

    final ExistedExecutorWrapper existed = new ExistedExecutorWrapper(base);
    final IntegrateExistedExecutorWrapper integrate = new IntegrateExistedExecutorWrapper(existed);

    return new ChattyExecutorWrapper(integrate);
  }

  /**
   * Integrate an existed executor wrapper(`ExistedExecutorWrapper`) without modification
   */
  private static class IntegrateExistedExecutorWrapper implements Executor, Wrapper<Executor> {
    private final ExistedExecutorWrapper existedExecutorWrapper;

    public IntegrateExistedExecutorWrapper(ExistedExecutorWrapper existedExecutorWrapper) {
      this.existedExecutorWrapper = existedExecutorWrapper;
    }

    @Override
    public Executor unwrap() {
      return existedExecutorWrapper.getExecutor();
    }

    @Override
    public void execute(Runnable command) {
      existedExecutorWrapper.execute(command);
    }
  }
}

/*
demo output:

Is executor chatty? true
Is executor IntegrateExistedExecutor? true

BlaBlaBla...
I'm existed executor, have nothing to do with ~inspectable~wrappers~.
I'm working.
 */
```

> Runnable demo codes in project: [`IntegrationDemo.java`](src/test/java/io/foldright/demo/integration/IntegrationDemo.java)

## 🍼 Java API Docs

The current version Java API documentation: <https://foldright.io/inspectable-wrappers/apidocs/>

## 🍪 Dependency

For `Maven` projects:

```xml

<dependency>
  <groupId>io.foldright</groupId>
  <artifactId>inspectable-wrappers</artifactId>
  <version>0.3.1</version>
</dependency>
```

For `Gradle` projects:

```groovy
// Gradle Kotlin DSL
implementation("io.foldright:inspectable-wrappers:0.3.1")
```

```groovy
// Gradle Groovy DSL
implementation 'io.foldright:inspectable-wrappers:0.3.1'
```

`inspectable-wrappers` has published to maven central, find the latest version at [central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/inspectable-wrappers/0.3.0/versions).
