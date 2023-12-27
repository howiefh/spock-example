Spock具有强大的扩展机制，允许钩入测试类的生命周期以丰富或改变其行为。本文将着重介绍部分实用的注解。

# Spock配置文件

有些扩展可以通过 Spock 配置文件中的选项进行配置。每个扩展的说明中都会提到如何配置。所有这些配置都在一个名为 SpockConfig.groovy 的 Groovy 文件中。

# 内置扩展

## Ignore

要暂时阻止某个测试方法被执行，可使用 `spock.lang.Ignore` 对其进行注释：

```groovy
@Ignore
def "my feature"() { ... }
```

为了便于记录，可以提供一个理由：

```groovy
@Ignore("TODO")
def "my feature"() { ... }
```

要忽略整个测试类，可注解其类：

```groovy
@Ignore
class MySpec extends Specification { ... }
```

在大多数执行环境中，被忽略的测试方法和测试类将被报告为 "跳过"。

默认情况下，`@Ignore` 只影响已注释的测试类，通过将继承设置为 true，可以将其配置为也适用于子类：

```groovy
@Ignore(inherited = true)
class MySpec extends Specification { ... }
class MySubSpec extends MySpec { ... }
```
在使用 `spock.lang.Stepwise` 注解的测试类中忽略测试方法时应小心谨慎，因为后面的测试方法可能取决于前面的测试方法是否已执行。

## IgnoreRest

要忽略除一小部分（通常是）方法之外的所有方法，可使用 `spock.lang.IgnoreRest` 进行注解：

```groovy
def "I'll be ignored"() { ... }

@IgnoreRest
def "I'll run"() { ... }

def "I'll also be ignored"( ) { ... }
```

在使用 `spock.lang.Stepwise` 注解的测试类中忽略测试方法时应小心谨慎，因为后面的测试方法可能取决于前面的测试方法是否已执行。

## IgnoreIf

要在特定条件下忽略某个测试方法或类，可使用 `spock.lang.IgnoreIf` 对其进行注解，并在后面加上谓词和可选的理由：

```groovy
@IgnoreIf({ System.getProperty("os.name").toLowerCase().contains("windows") })
def "I'll run everywhere but on Windows"() {
```

* `sys`包含所有 system 属性映射
* `env`包含所有环境变量的映射
* `os`包含操作系统信息，`spock.util.environment.OperatingSystem`类型
* `jvm`包含 JVM 信息，`spock.util.environment.Jvm`类型
* `data`当前迭代的所有数据变量的映射。

```groovy
@IgnoreIf({ os.windows })
def "I will run everywhere but on Windows"() {
```

## Requires

要在特定条件下执行一个测试方法，可使用 `spock.lang.Requires` 对其进行注解，之后再加上一个谓词：

```groovy
@Requires({ os.windows })
def "I'll only run on Windows"() {
```

`Requires` 的作用与 `IgnoreIf` 很像，只是谓词是相反的。一般来说，最好说明执行方法的条件，而不是忽略方法的条件。

如果存在多个 `@Requires` 注释，它们会被一个逻辑 "and" 有效地组合起来。

## PendingFeature

要表示功能尚未完全实现，不应作为错误报告，可使用 `spock.lang.PendingFeature` 注解。

用来标注那些尚未能运行但应该已经提交的测试。与Ignore的主要区别在于，测试会被执行，但是测试失败会被忽略。如果测试通过且没有错误，那么它将被报告为失败，因为应该移除`PendingFeature`注解。这样，这些测试将成为常规测试的一部分，而不是被永远忽略。

```groovy
@PendingFeature
def "not implemented yet"() { ... }
```

## PendingFeatureIf

要有条件地指出某个功能尚未完全实现，且不应作为错误报告，可以使用注解 `spock.lang.PendingFeatureIf`，并包含一个类似于 `IgnoreIf` 或 `Requires` 的前提条件。

如果条件表达式通过，则其行为与 `PendingFeature` 相同，否则什么也不做。

## Stepwise

要按测试方法声明的顺序执行功能，可使用 `spock.lang.Stepwise` 对测试类进行注解

```groovy
@Stepwise
class RunInOrderSpec extends Specification {
  def "I run first"()  { expect: true }
  def "I run second"() { expect: false }
  def "I will be skipped"() { expect: true }
}
```

## Timeout

要使超出给定执行时长的测试方法、固定方法或类失败，可以使用`spock.lang.Timeout` 注解，可以指定时长，以及可选的时间单位属性。默认时间单位是秒。

当应用于测试方法时，超时是每次迭代的执行时间，不包括在固定方法中花费的时间：

```groovy
@Timeout(5)
def "I fail if I run for more than five seconds"() { ... }

@Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
def "I better be quick" { ... }
```

将`Timeout`应用于测试类的效果与将其应用于每个未使用`Timeout`注解的测试方法相同，不包括在固定方法中花费的时间：

```groovy
@Timeout(10)
class TimedSpec extends Specification {
    def "I fail after ten seconds"() { ... }
    def "Me too"() { ... }

    @Timeout(value = 250, unit = MILLISECONDS)
    def "I fail much faster"() { ... }
}
```

当应用于固定方法时，超时是每次执行固定方法的时间。

## Retry

`@Retry` 扩展可用于不稳定的集成测试，因为远程系统有时会出现故障。默认情况下，如果出现异常（`Exception`）或断言错误（`AssertionError`），它会重试迭代 3 次，延迟时间为 0，但所有这些都是可配置的。此外，还可以使用可选的条件闭包来确定是否应重试某个功能。它还为数据驱动功能提供特殊支持，可以重试所有迭代，也可以只重试失败的迭代。

```groovy
class FlakyIntegrationSpec extends Specification {
  @Retry
  def retry3Times() { ... }

  @Retry(count = 5)
  def retry5Times() { ... }

  @Retry(exceptions=[IOException])
  def onlyRetryIOException() { ... }

  @Retry(condition = { failure.message.contains('foo') })
  def onlyRetryIfConditionOnFailureHolds() { ... }

  @Retry(condition = { instance.field != null })
  def onlyRetryIfConditionOnInstanceHolds() { ... }

  @Retry
  def retryFailingIterations() {
    ...
    where:
    data << sql.select()
  }

  @Retry(mode = Retry.Mode.FEATURE)
  def retryWholeFeature() {
    ...
    where:
    data << sql.select()
  }

  @Retry(delay = 1000)
  def retryAfter1000MsDelay() { ... }
}
```

该注解可以作用于测试类

## Title

要为测试类附加自然语言名称，可以使用 `spock.lang.Title`：

```groovy
@Title("This is easy to read")
class ThisIsHarderToReadSpec extends Specification {
...
}
```

同样的，要添加自然语言描述，可以使用 `spock.lang.Narrative`:

```groovy
@Narrative("""
As a user
I want foo
So that bar
""")
class GiveTheUserFooSpec() { ... }
```

## Subject

使用 `spock.lang.Subject` 表示一个或多个测试类的主题：

```groovy
@Subject([Foo, Bar])
class SubjectDocSpec extends Specification {
```

# 参考文档

本文选取了部分 [Spock文档](https://spockframework.org/spock/docs/2.3/extensions.html) 内容翻译，spock还可以自定义扩展，感兴趣的话可以阅读原文档。
