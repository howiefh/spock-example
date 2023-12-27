# 简介

基于交互的测试是 2000 年代初在极限编程（XP）社区兴起的一种设计和测试技术。它关注对象的行为而非状态，探讨测试类中的被测对象如何通过方法调用与其协作者交互。

例如，假设我们有一个向其 "订阅者" 发送信息的 "发布者"：

```groovy
class Publisher {
  List<Subscriber> subscribers = []
  int messageCount = 0
  void send(String message){
    subscribers*.receive(message)
    messageCount++
  }
}

interface Subscriber {
  void receive(String message)
}

class PublisherSpec extends Specification {
  Publisher publisher = new Publisher()
}
```

我们要怎么测试发布者呢？通过基于状态的测试，我们可以验证发布者是否引用了订阅者。不过，更有趣的问题是，发布者发送的消息是否会被订阅者收到要怎么测试。要回答这个问题，我们需要一个特殊的 Subscriber 实现，来监听发布者和订阅者之间的交互。这种实现被称为Mock对象。

我们当然可以手工创建 Subscriber 的模拟实现，但随着方法数量和交互复杂性的增加，编写和维护这些代码可能会令人不快。这就是mock框架的用武之地： 它们提供了一种方法来描述被测对象与其协作者之间的预期交互，并生成协作者的模拟实现来验证这些预期。

Java领域不乏流行且成熟的模拟框架： JMock, EasyMock, Mockito 等等。这些工具都可以和 Spock 一起使用，但是更推荐使用Spock自带的mock框架。

# 测试替身

Spock提供了Mock/Stub/Spy三种类型的测试替身

Mock可以验证交互过程，验证某个函数是否被执行，被执行几次；可以指定返回结果。

Stub可以理解为Mock的子集，用于指定函数返回结果。

![mock](images/mock_stub.png)

Spy是基于真实对象的，自动把方法调用委托给真实对象，可以监听调用者和真实对象的交互。

# 创建 Mock 对象

通过`MockingApi.Mock()`方法创建 Mock 对象：

```groovy
def subscriber = Mock(Subscriber)
def subscriber2 = Mock(Subscriber)
```

还有另一种对集成开发环境更友好的写法：

```groovy
Subscriber subscriber = Mock()
Subscriber subscriber2 = Mock()
```

这里，mock 的类型是根据赋值左侧的变量类型推断出来的。

# Mock 对象的默认行为

调用 Mock 对象上的方法将返回方法返回类型的默认值，比如 `false`,`0`, or`null`，但`Object.toString`，`Object.hashCode`和`Object.equals`方法例外。一个Mock 对象只等于它自己，有一个唯一的哈希码，和一个包含它类型名称的字符串。

# 在被测对象中注入 Mock 对象

创建发布者及其订阅者后，我们需要让前者引用后者：

```groovy
class PublisherSpec extends Specification {
    Publisher publisher = new Publisher()
    Subscriber subscriber = Mock()
    Subscriber subscriber2 = Mock()

    def setup() {
        publisher.subscribers << subscriber // << 是 Groovy 中 List.add()的语法糖
        publisher.subscribers << subscriber2
    }
}
```

我们可以描述双方之间的预期互动了

# Mocking

```groovy
def "should send messages to all subscribers"() {
    when:
    publisher.send("hello")

    then:
    1 * subscriber.receive("hello")
    1 * subscriber2.receive("hello")
}
```

当发布者发送`'hello'`j消息时，两个订阅者都应准确地接收一次该消息

当`when`块中的代码运行时，对 mock 对象的所有调用都将与`then`码块中描述的交互进行匹配，如果其中一个交互未被满足，就会抛出一个`InteractionNotSatisfiedError`。这种验证过程是自动进行的，不需要额外代码。

## 互动

`then`块中的交互描述语句可以分为四个部分，分别是：

```
1 * subscriber.receive("hello")
|   |          |       |
|   |          |       参数约束
|   |        方法约束
|  目标约束
调用次数
```

## 调用次数

调用次数可以是一个具体值，也可以是范围：

```groovy
1 * subscriber.receive("hello")      // 有且仅有一次调用
0 * subscriber.receive("hello")      // 0 次调用
(1..3) * subscriber.receive("hello") // 1 到 3 次调用，包括 1 和 3
(1.._) * subscriber.receive("hello") // 至少一次调用
(_..3) * subscriber.receive("hello") // 最多三次调用
_ * subscriber.receive("hello")      // 任意次数的调用，包括 0 次（很少需要）
```

## 目标约束

```groovy
1 * subscriber.receive("hello") // 调用 'subscriber'
1 * _.receive("hello")          // 调用任何mock object
```

## 方法约束

```groovy
1 * subscriber.receive("hello") // 'receive' 方法将会被调用
1 * subscriber./r.*e/("hello")  // 名字匹配正则表达式 r.*e 的方法将会被调用
```

当期望调用 getter 方法时，可以使用 Groovy 属性语法代替方法语法：

```groovy
1 * subscriber.status // 等同于：1 * subscriber.getStatus()
```

在期待调用 setter 方法时，只能使用方法语法：

```groovy
1 * subscriber.setStatus("ok") // 不可以写成：1 * subscriber.status = "ok"
```

## 参数约束

```groovy
1 * subscriber.receive("hello")     // 调用将会传入参数 "hello"
1 * subscriber.receive(!"hello")    // 调用将会传入一个不是 "hello" 的参数
1 * subscriber.receive()            // 调用不会传入任何参数
1 * subscriber.receive(_)           // 调用将会传入一个值任意的参数（包括 null）
1 * subscriber.receive(*_)          // 调用将会传入任意数量（包括 0），任意值的参数
1 * subscriber.receive(!null)       // 调用将会传入一个任意的非 null 的参数
1 * subscriber.receive(_ as String) // 调用将会传入一个 String 类型的非 null 的参数
1 * subscriber.receive({ it.size() > 3 && it.contains('a') }) // 调用将会传入一个参数，该参数能使 lambda 表达式返回 true 这里是指传入参数的长度将会大于 3且包含'a'
```

对于有多个参数的方法，参数约束会如预期一样起作用：

```groovy
1 * process.invoke("ls", "-a", _, !null, { ["abcdefghiklmnopqrstuwx1"].contains(it) })
```

等式约束使用 Groovy 等式来检查参数，即`参数 == 约束`。

此外还有Hamcrest 约束、通配符约束、代码约束、否定约束、类型约束。

## 匹配任意方法调用

```groovy
1 * subscriber._(*_)     // 匹配 subscriber mock 对象上的任意方法，参数数量和值都任意
1 * subscriber._         // 上一行的简便写法，更推荐
1 * _._                  // 匹配任意 mock 对象上的任意方法，参数数量和值都任意
1 * _                    // 上一行的简便写法，更推荐
```

## 严格的 Mocking

严格模拟方式不允许除显式声明之外的任何交互：

```groovy
when:
publisher.publish("hello")

then:
1 * subscriber.receive("hello") // 期望 'subscriber' mock 对象上的一次 'receive' 调用
_ * auditing._                  //'auditing' mock 对象上的任何调用都是允许的
0 * _                           // 除上面两条描述以外的其他任何行为都是不允许的，该描述必须是最后一条描述
```

## 在何处声明交互

交互除了可以放在`then`块，放在`when`块之前的任何地方，比如`given`块。此外，放在辅助方法中也是可以的。

如果调用与多个交互匹配，则未达到调用上限的最早声明的交互将优先匹配。这条规则有一个例外：在 `then:` 代码块中声明的交互会在任何其他交互之前进行匹配。这样就可以用 `then:` 代码块中声明的交互覆盖`setup`方法中声明的交互。

## 创建 mock 对象时声明交互

如果一个mock有一组 "基本 "交互，而且这些交互不会发生变化，那么就可以在创建mock对象时直接声明这些交互：

```groovy
Subscriber subscriber = Mock {
   1 * receive("hello")
   1 * receive("goodbye")
}
```

## 对交互进行分组

共享同一目标约束的交互可以在 `Specification.with` 块中分组。与在创建 Mock 时声明交互类似，这样就无需重复目标约束：

```groovy
with(subscriber) {
    1 * receive("hello")
    1 * receive("goodbye")
}
```

## 交互描述语句和条件语句混合

一个 `then:` 代码块可以同时包含交互和条件表达式。虽然没有严格要求，但习惯上是在条件表达式之前声明交互作用：

```groovy
when:
publisher.send("hello")

then:
1 * subscriber.receive("hello")
publisher.messageCount == 1
```

## 显式交互描述代码块

在内部，Spock 必须在交互发生之前就掌握有关预期交互的全部信息。那么，怎么能在 `then:` 块中声明交互呢？是因为Spock 会将在 `then:` 代码块中声明的交互移到前面的 `when:` 代码块之前。在大多数情况下，这样做效果很好，但有时也会导致问题：

```groovy
when:
publisher.send("hello")

then:
def message = "hello" // 1
1 * subscriber.receive(message) // 2
```

执行顺序是先`1 * subscriber.receive(message)`再`when`最后`def message = "hello"`。这将在运行时导致`MissingPropertyException`。一种解决办法是将message定义在`when`块前或`where`块中，另一种办法就是将变量定义语句和交互绑定：

```groovy
when:
publisher.send("hello")

then:
interaction {// 由于 MockingApi.interaction 块总是被整体移动，因此代码现在可以按预期运行。
  def message = "hello"
  1 * subscriber.receive(message)
}
```

## 交互的作用域

在 `then:` 代码块中声明的交互作用的作用域是前面的 `when:` 代码块：

```groovy
when:
publisher.send("message1")

then:
1 * subscriber.receive("message1")

when:
publisher.send("message2")

then:
1 * subscriber.receive("message2")
```

上面的代码确保在第一个`when`块执行时，`subscriber` mock 对象收到消息`"message1"`，在第二个`when`块执行时收到消息`"message2"`。

定义在`then`块外的交互的作用域是从它的定义开始，直到包含它的测试方法结束。

交互只能在测试方法中定义，不能定义在静态方法，`setupSpec`方法和`cleanupSpec`方法中。同样，mock 对象也不能作为静态字段和`@Shared`字段。

## 验证交互

基于模拟的测试有两种主要失败方式： 交互匹配的调用次数可能多于允许的次数，或者匹配的调用次数少于要求的次数。前一种情况会在调用发生时被检测到，并导致 `TooManyInvocationsError`：

```
Too many invocations for:

2 * subscriber.receive(_) (3 invocations)
```

为了更容易诊断为何有太多调用匹配，Spock 会显示所有与相关交互匹配的调用：

```
Matching invocations (ordered by last occurrence):

2 * subscriber.receive("hello")   <-- this triggered the error
1 * subscriber.receive("goodbye")
```

根据输出，其中一次`receive("hello")`调用引发了`TooManyInvocationsError`错误。注意，由于像 `subscriber.receive("hello")` 难以区分的两次调用被汇总到一行输出，第一次 `receive("hello")` 很可能发生在 `receive("goodbye")` 之前。

第二种情况（调用次数少于要求）只有在 `when` 块执行完毕后才能检测到。这将导致 `TooFewInvocationsError`：

```
Too few invocations for:
1 * subscriber.receive("hello") (0 invocations)
```

注意，无论该方法是根本没有被调用、用不同的参数调用了相同的方法、在不同的 mock 对象上调用了相同的方法，还是 "代替 "该方法调用了不同的方法；无论哪种情况，都会出现 `TooFewInvocationsError` 错误。

为了更容易诊断 "代替 "缺失调用的情况，Spock 会显示与任何交互不匹配的所有调用，并按其与相关交互的相似度排序。特别是，除了交互的参数外，与其他内容匹配的调用将首先显示：

```
Unmatched invocations (ordered by similarity):

1 * subscriber.receive("goodbye")
1 * subscriber2.receive("hello")
```

## 调用顺序

通常情况下，具体的方法调用顺序并不重要，而且可能会随着时间的推移而改变。为避免过度指定，Spock 默认允许任何调用顺序，只要最终满足指定的交互：

```groovy
then:
2 * subscriber.receive("hello")
1 * subscriber.receive("goodbye")
```

在这里，`"hello""hello""goodbye"`,`"hello""goodbye""hello"`和`"goodbye""hello""hello"`中的任何一个调用序列都能满足指定的交互。

在调用顺序很重要的情况下，可以通过将交互拆分成多个 `then:` 块来强加一个顺序：

```groovy
then:
2 * subscriber.receive("hello")

then:
1 * subscriber.receive("goodbye")
```

Spock 将验证两个`"hello"`都在 `"goodbye"`之前收到

## 模拟类

除接口外，Spock 还支持模拟类。模拟类的工作原理与模拟接口相同，唯一的额外要求是在类路径中加入 `byte-buddy`1.9+或 `cglib-nodep` 3.2.0+。如果类路径中缺少这两个库中的任何一个，Spock 会提示。

# Stubbing

存根是让协作者以某种方式响应方法调用的行为。在对方法进行存根处理时，你并不关心该方法是否会被调用以及会被调用多少次；你只想让它在被调用时返回一些值或执行一些副作用。

在下面的示例中，让我们修改订阅者的 receive 方法，使其返回一个状态代码，告诉我们订阅者是否能够处理消息：

```groovy
interface Subscriber {
    String receive(String message)
}
```

现在，让 receive 方法在每次调用时都返回 `"ok"`：

```groovy
subscriber.receive(_) >> "ok"
```

与模拟交互相比，存根交互在左端没有调用次数，但在右端添加了一个响应生成器：

```
subscriber.receive(_) >> "ok"
|          |       |     |
|          |       |     响应生成器
|          |       参数约束
|          方法约束
目标约束
```

存根交互要么在 `then:` 块内部，要么在 `when:` 块之前的任何地方。如果 mock 对象仅用于存根，通常在创建 mock 时或在`given:`块中声明。

## 返回固定值

我们已经看到使用右移 (`>>`) 操作符来返回固定值：

```groovy
subscriber.receive(_) >> "ok"
```

要为不同的调用返回不同的值，请使用多重交互：

```groovy
subscriber.receive("message1") >> "ok"
subscriber.receive("message2") >> "fail"
```

只要收到 `"message1"`，该方法就会返回 `"ok"`；只要收到 `"message2"`，该方法就会返回 `"fail"`。对于可以返回的值没有限制，只要它们与方法声明的返回类型兼容即可。

## 返回值序列

要在连续调用时返回不同的值，可使用三重右移 (`>>>`) 操作符：

```groovy
subscriber.receive(_) >>> ["ok", "error", "error", "ok"]
```

第一次调用将返回 `"ok"`，第二次和第三次调用将返回 `"error"`，其余所有调用都将返回 `"ok"`。右侧必须是一个 Groovy 知道如何遍历的值；在本例中，我们使用了一个列表。

## 计算返回值

要根据方法的参数计算返回值，可将右移 (`>>`) 运算符与闭包一起使用。如果闭包声明了一个无类型的参数，就会传递给它方法的参数列表：

```groovy
subscriber.receive(_) >> { args -> args[0].size() > 3 ? "ok" : "fail" }
```

在这里，如果消息长度超过三个字符，则返回 `"ok"`，否则返回 `"fail"`。

在大多数情况下，直接访问方法的参数会更方便。如果闭包声明了多个参数或单个带类型参数，方法参数将逐一映射到闭包参数中：

```groovy
subscriber.receive(_) >> { String message -> message.size() > 3 ? "ok" : "fail" }
```

该响应生成器的行为与前一个相同，但可读性更强。

如果你需要比参数更多的方法调用信息，请查看 `org.spockframework.mock.IMockInvocation`。在这个接口中声明的所有方法都可以在闭包中使用，而无需为它们加上前缀(在 Groovy 术语中，闭包委托给 `IMockInvocation` 的一个实例）。

## 抛出异常

有时，除了计算返回值，你可能还想做更多的事情。一个典型的例子就是抛出异常。同样，闭包也能帮上忙：

```groovy
subscriber.receive(_) >> { throw new InternalError("ouch") }
```

## 链式响应生成器

```groovy
subscriber.receive(_) >>> ["ok", "fail", "ok"] >> { throw new InternalError() } >> "ok"
```

前三次调用将分别返回 `"ok"`、`"fail"`和 `"ok"`，第四次调用将抛出 `InternalError`，以后的调用都将返回`"ok"`。

## 返回默认响应

如果你并不关心返回什么，但必须返回一个非空值，可以使用 `_`。这将使用与 Stub 相同的逻辑来计算响应，因此它只对 Mock 和 Spy 实例有用。

```groovy
subscriber.receive(_) >> _
```

它的一个应用是让模拟表现得像存根，但仍能进行断言。如果方法的返回类型可从 mock 类型赋值，默认响应将返回 mock 本身。这在处理流式的 API（如builder）时非常有用，否则模拟起来会非常痛苦。

```groovy
given:
ThingBuilder builder = Mock() {
  _ >> _
}

when:
Thing thing = builder
  .id("id-42")
  .name("spock")
  .weight(100)
  .build()

then:
1 * builder.build() >> new Thing(id: 'id-1337')
thing.id == 'id-1337'
```

# 结合 Mocking 和 Stubbing

mocking 和 stubbing 可以按如下方式结合：

```groovy
1 * subscriber.receive("message1") >> "ok"
1 * subscriber.receive("message2") >> "fail"
```

不能像Mockito一样拆开，同一方法调用的模拟和存根必须在同一交互中进行。

# 其他

到目前为止，我们已经使用 `MockingApi.Mock` 方法创建了模拟对象。除了这个方法外，`MockingApi` 类还提供了其他几个工厂方法，用于创建更特殊的模拟对象。其实大部分情况用Mock就可以了。

## Stubs

存根通过 `MockingApi.Stub` 工厂方法创建：

```groovy
Subscriber subscriber = Stub()
```

Mock既可用于存根，也可用于模拟，而Stub只能用于存根。将协作者限制为存根，可以向测试类的读者传达它的作用。

存根默认情况下的返回值更加宽松

* 对于原始类型，会返回其默认值
* 对于非原始数值（如 `BigDecimal`），将返回 0。
* 如果值可以通过存根实例赋值，则返回该实例（例如builder模式）。
* 对于非数值，将返回一个 "空 "或 "假 "对象。这可能是指一个空字符串、一个空集合、一个通过默认构造函数构造的对象

## Spies

使用此功能前请三思。最好的办法是改变代码的设计

```groovy
SubscriberImpl subscriber = Spy(constructorArgs: ["Fred"])
```

spy总是基于真实对象。因此，你必须提供一个类类型而不是接口类型，以及该类型的任何构造函数参数。如果没有提供构造函数参数，将使用该类型的无参数构造函数。

对间谍对象的方法调用会自动委托给真实对象。同样，真实对象方法返回的值也会通过间谍对象传回给调用者。

创建间谍后，就可以监听间谍底层的调用者和真实对象之间的调用交互：

```groovy
1 * subscriber.receive(_)
```

在间谍身上存根方法时，真正的方法不再被调用：

```groovy
subscriber.receive(_) >> "ok"
```

有时，我们希望既能执行某些代码，又能委托实际方法：

```groovy
subscriber.receive(_) >> { String message -> callRealMethod(); message.size() > 3 ? "ok" : "fail" }
```

# 高级功能

大多数情况应该用不到这些高级功能

## 自定义模拟

说到底，`Mock()`、`Stub()` 和 `Spy()` 工厂方法只是创建具有特定配置的 mock 对象的方法。如果你想对 mock 的配置进行更精细的控制，可以看看 `org.spockframework.mock.IMockConfiguration` 接口。该接口的所有属性都可以作为命名参数传递给 `Mock()` 方法。例如

```groovy
def person = Mock(name: "Fred", type: Person, defaultResponse: ZeroOrNullResponse.INSTANCE, verified: false)
```

## 检测模拟对象

要确定某个特定对象是否是 Spock 模拟对象，可使用 `org.spockframework.mock.MockUtil.MockUtil` 中的函数：

```groovy
MockUtil mockUtil = new MockUtil()
List list1 = []
List list2 = Mock()

expect:
!mockUtil.isMock(list1)
mockUtil.isMock(list2)
```

还可以获取更多信息

```groovy
IMockObject mock = mockUtil.asMock(list2)

expect:
mock.name == "list2"
mock.type == List
mock.nature == MockNature.MOCK
```

# 参考文档

本文翻译自 [Spock文档](https://spockframework.org/spock/docs/2.3/interaction_based_testing.html)