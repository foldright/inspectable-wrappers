# 🪐 Wrain: Inspectable Wrapper Chain

<p align="center">
<a href="https://github.com/foldright/wrain/actions/workflows/ci.yml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/wrain/ci.yml?branch=main&logo=github&logoColor=white" alt="Github Workflow Build Status"></a>
<a href="https://app.codecov.io/gh/foldright/wrain/tree/main"><img src="https://img.shields.io/codecov/c/github/foldright/wrain/main?logo=codecov&logoColor=white" alt="Codecov"></a>
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/Java-8+-339933?logo=openjdk&logoColor=white" alt="Java support"></a>
<a href="https://www.apache.org/licenses/LICENSE-2.0.html"><img src="https://img.shields.io/github/license/foldright/wrain?color=4D7A97&logo=apache" alt="License"></a>
<a href="https://github.com/foldright/wrain/stargazers"><img src="https://img.shields.io/github/stars/foldright/wrain" alt="GitHub Stars"></a>
<a href="https://github.com/foldright/wrain/fork"><img src="https://img.shields.io/github/forks/foldright/wrain" alt="GitHub Forks"></a>
<a href="https://github.com/foldright/wrain/issues"><img src="https://img.shields.io/github/issues/foldright/wrain" alt="GitHub Issues"></a>
<a href="https://github.com/foldright/wrain"><img src="https://img.shields.io/github/repo-size/foldright/wrain" alt="GitHub repo size"></a>
<a href="https://gitpod.io/#https://github.com/foldright/wrain"><img src="https://img.shields.io/badge/Gitpod-ready to code-339933?label=gitpod&logo=gitpod&logoColor=white" alt="gitpod: Ready to Code"></a>
</p>

**Wrain**(Inspectable Wrapper Chain) is a nano library provides the interfaces
to make wrapper instances as an **inspectable wrapper chain**.

- [`Wrapper`](src/main/java/io/foldright/wrain/Wrapper.java) is core interface, used to
  - enhance the wrapper instances as a wrapper chain
  - provides methods to inspect the wrapper chain.
- [`Attachable`](src/main/java/io/foldright/wrain/Attachable.java) interface is used to
  enhance the wrapper instances with the attachment storage ability.

## Demo

Below use the `Executor Wrapper` to demonstrate the usage of `wrain` lib.  
(Runnable demo codes in project: [`Demo.java`](src/test/java/io/foldright/demo/Demo.java))

### Wrapper implementation in your application code

```java
// a wrain implementation
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

// another wrain implementation
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

  private final ConcurrentMap<String, Object> attachments = new ConcurrentHashMap<>();

  @Override
  public void wrainSet(String key, Object value) {
    attachments.put(key, value);
  }

  @Override
  public <V> V wrainGet(String key) {
    return (V) attachments.get(key);
  }
}
```

### demo code of inspecting the wrapper chain

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
