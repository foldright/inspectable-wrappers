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

- [🍡 Files](#-files)
- [🌰 Usage Demo](#-usage-demo)
  - [demo wrapper implementations in your application code](#demo-wrapper-implementations-in-your-application-code)
  - [inspection of the wrapper chain](#inspection-of-the-wrapper-chain)
- [🌰 Integration Demo](#-integration-demo)
  - [the demo existed wrapper which cannot be modified](#the-demo-existed-wrapper-which-cannot-be-modified)
  - [the integration code](#the-integration-code)
- [🌰 Integration Demo using `WrapperAdapterUtils`](#-integration-demo-using-wrapperadapterutils)
- [🍼 Java API Docs](#-java-api-docs)
- [🍪 Dependency](#-dependency)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

--------------------------------------------------------------------------------

## 🍡 Files

- The core interfaces/specification interfaces:
  - [`Wrapper`](src/main/java/io/foldright/inspectablewrappers/Wrapper.java) interface is used to
    be implemented by wrapper classes, make an **inspectable wrapper chain**(linked list)
  - [`Attachable`](src/main/java/io/foldright/inspectablewrappers/Attachable.java) interface is used to
    enhance the wrapper instances with the attachment storage ability
  - [`WrapperAdapter`](src/main/java/io/foldright/inspectablewrappers/WrapperAdapter.java) interface is used to
    adapt an existed wrapper instance to type `Wrapper` without modifying it
- The [`Inspector`](src/main/java/io/foldright/inspectablewrappers/Inspector.java) class is used to
  inspect the **wrapper chain**
- The utility classes:
  - [`AttachableDelegate`](src/main/java/io/foldright/inspectablewrappers/utils/AttachableDelegate.java) class
    provides a simple `Attachable` delegate implementation
  - [`WrapperAdapterUtils`](src/main/java/io/foldright/inspectablewrappers/utils/WrapperAdapterUtils.java) class
    provides utility methods for creating `WrapperAdapter` instances
    without writing boilerplate codes of creating new adapter classes

## 🌰 Usage Demo

Below use the `Executor Wrapper` to demonstrate the usage.

### demo wrapper implementations in your application code

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
  public Executor unwrap_() {
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
  public Executor unwrap_() {
    return executor;
  }

  private final Attachable<String, String> attachable = new AttachableDelegate<>();

  @Override
  public void setAttachment_(String key, String value) {
    attachable.setAttachment_(key, value);
  }

  @Override
  public String getAttachment_(String key) {
    return attachable.getAttachment_(key);
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
        containsInstanceTypeOnWrapperChain(executor, LazyExecutorWrapper.class));
    // print true

    String busy = getAttachmentFromWrapperChain(executor, "busy");
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
    lazy.setAttachment_("busy", "very, very busy!");

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

Integrate an existed wrapper instance to type `Wrapper` without modifying it.

### the demo existed wrapper which cannot be modified

```java
public class ExistedExecutorWrapper implements Executor {
  private final Executor executor;

  public ExistedExecutorWrapper(Executor executor) {
    this.executor = executor;
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

    System.out.println("Is executor ExistedExecutorWrapper? " +
        containsInstanceTypeOnWrapperChain(executor, ExistedExecutorWrapper.class));
    // print true
    String adaptAttachment = getAttachmentFromWrapperChain(executor, "adapted-existed-executor-wrapper-msg");
    System.out.println("Adapted existed executor wrapper msg: " + adaptAttachment);
    // print "I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~."

    ////////////////////////////////////////
    // call executor(wrapper chain)
    ////////////////////////////////////////

    System.out.println();
    executor.execute(() -> System.out.println("I'm working."));
  }

  private static Executor buildExecutorChain() {
    final Executor base = Runnable::run;
    final ExistedExecutorWrapperAdapter adapter = createExistedExecutorWrapperAdapter(base);
    return new ChattyExecutorWrapper(adapter);
  }

  private static ExistedExecutorWrapperAdapter createExistedExecutorWrapperAdapter(Executor base) {
    final ExistedExecutorWrapper existed = new ExistedExecutorWrapper(base);
    final ExistedExecutorWrapperAdapter adapter = new ExistedExecutorWrapperAdapter(base, existed);
    adapter.setAttachment_("adapted-existed-executor-wrapper-msg", "I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~.");
    return adapter;
  }

  /**
   * Adaption an existed wrapper(`ExistedExecutorWrapper`) without modifying it.
   */
  private static class ExistedExecutorWrapperAdapter implements Executor, WrapperAdapter<Executor>, Attachable<String, String> {
    private final Executor base;
    private final Executor adaptee;

    public ExistedExecutorWrapperAdapter(Executor base, Executor adaptee) {
      this.base = base;
      this.adaptee = adaptee;
    }

    @Override
    public Executor unwrap_() {
      return base;
    }

    @Override
    public Executor adaptee_() {
      return adaptee;
    }

    @Override
    public void execute(Runnable command) {
      adaptee.execute(command);
    }

    private final Attachable<String, String> attachable = new AttachableDelegate<>();

    @Override
    public void setAttachment_(String key, String value) {
      attachable.setAttachment_(key, value);
    }

    @Nullable
    @Override
    public String getAttachment_(String key) {
      return attachable.getAttachment_(key);
    }
  }
}

/*
demo output:

Is executor ExistedExecutorWrapper? true
Adapted existed executor wrapper msg: I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~.

BlaBlaBla...
I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~.
I'm working.
 */
```

> Runnable demo codes in project: [`IntegrationDemo.java`](src/test/java/io/foldright/demo/integration/IntegrationDemo.java)

## 🌰 Integration Demo using `WrapperAdapterUtils`

Uses `WrapperAdapterUtils` to create `WrapperAdapter` instances without writing boilerplate codes of creating new adapter classes.

```java
public class IntegrationDemoUsingWrapperAdapterUtils {
  public static void main(String[] args) {
    final Executor executor = buildExecutorChain();

    ////////////////////////////////////////
    // inspect the executor(wrapper chain)
    ////////////////////////////////////////

    System.out.println("Is executor ExistedExecutorWrapper? " +
        containsInstanceTypeOnWrapperChain(executor, ExistedExecutorWrapper.class));
    // print true
    String adaptAttachment = getAttachmentFromWrapperChain(executor, "adapted-existed-executor-wrapper-msg");
    System.out.println("Adapted existed executor wrapper msg: " + adaptAttachment);
    // print "I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~."

    ////////////////////////////////////////
    // call executor(wrapper chain)
    ////////////////////////////////////////

    System.out.println();
    executor.execute(() -> System.out.println("I'm working."));
  }

  private static Executor buildExecutorChain() {
    final Executor base = Runnable::run;
    final Executor adapter = createExistedExecutorWrapperAdapter(base);
    return new ChattyExecutorWrapper(adapter);
  }

  private static Executor createExistedExecutorWrapperAdapter(Executor base) {
    final Executor existed = new ExistedExecutorWrapper(base);

    Attachable<String, String> attachable = new AttachableDelegate<>();
    attachable.setAttachment_("adapted-existed-executor-wrapper-msg", "I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~.");

    return WrapperAdapterUtils.createWrapperAdapter(Executor.class, base, existed, attachable);
  }
}

/*
demo output:

Is executor ExistedExecutorWrapper? true
Adapted existed executor wrapper msg: I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~.

BlaBlaBla...
I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~.
I'm working.
 */
```

> Runnable demo codes in project: [`IntegrationDemoUsingWrapperAdapterUtils.java`](src/test/java/io/foldright/demo/integration/IntegrationDemoUsingWrapperAdapterUtils.java)

## 🍼 Java API Docs

The current version Java API documentation: <https://foldright.io/inspectable-wrappers/apidocs/>

## 🍪 Dependency

For `Maven` projects:

```xml

<dependency>
  <groupId>io.foldright</groupId>
  <artifactId>inspectable-wrappers</artifactId>
  <version>0.5.5</version>
</dependency>
```

For `Gradle` projects:

```groovy
// Gradle Kotlin DSL
implementation("io.foldright:inspectable-wrappers:0.5.5")
```

```groovy
// Gradle Groovy DSL
implementation 'io.foldright:inspectable-wrappers:0.5.5'
```

`inspectable-wrappers` has published to maven central, find the latest version at [central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/inspectable-wrappers/0.3.0/versions).
