很多情况下，需要用一组不同的输入和输出来测试同一份代码。Spock 对数据驱动测试提供了大量支持。

# 简介

假设要测试`Math.max()`方法：

```groovy
class MathSpec extends Specification{
    def "maximum of two numbers"() {
        expect:
        Math.max(1, 3) == 3
        Math.max(7, 4) == 7
        Math.max(0, 0) == 0
    }
}
```

这种写法会有一些潜在的问题。

* 代码和数据混合在一起，不易独立更改
* 不能轻易自动生成数据或从外部获取数据
* 为了多次使用相同的代码，必须复制代码或将其提取到单独的方法中

下面将通过数据表重构成数据驱动的测试方法。

# 数据表

数据表是使用一组固定数据值执行测试方法的便捷方式：

```groovy
class MathSpec extends Specification {
  def "maximum of two numbers"(int a, int b, int c) {
    expect:
    Math.max(a, b) == c

    where:
    a | b | c
    1 | 3 | 3
    7 | 4 | 7
    0 | 0 | 0
  }
}
```

表格的第一行称为表头，用于声明数据变量。后面的行称为表行，保存相应的值（每一行相当于是一个测试用例）。对于每一行，测试方法都将执行一次；我们称之为方法的一次迭代。如果迭代失败，仍将执行剩余的迭代。所有失败都会被报告。

数据表必须至少有两列。单列表可以写成

```groovy
where:
a | _
1 | _
7 | _
0 | _
```

两个或多个下划线可以用来将一个宽数据表分割成多个窄数据表。下面的写法和开始本小节开始处的写法是等效的。

```groovy
where:
a | _
1 | _
7 | _
0 | _
__

b | c
1 | 2
3 | 4
5 | 6
```

# 迭代的隔离执行

迭代之间是隔离的（数据表的每一行的测试用例是隔离的），每次迭代都会获得自己的测试类实例，并在每次迭代之前和之后分别调用`setup()`和`cleanup()`方法。

# 用例之间的对象共享

只能通过共享字段和静态字段在迭代（每一行的测试用例）间共享对象。只有共享字段和静态字段才能在`where`块中访问。

请注意，这些对象也将与其他方法共享。目前还没有只在同一方法的迭代之间共享对象的好方法。如果你认为这是一个问题，可以考虑将每个方法放入一个单独的测试类中，所有测试类都可以保存在同一个文件中。这样可以实现更好的隔离，但也要付出一些模板代码的代价。

# 语法改进

首先，由于 `where:` 块已经声明了所有数据变量，因此可以省略方法参数。

其次，输入和预期输出可以用双管道符号 `||` 分开，以便在视觉上将它们区分开来。

除了使用`|`也可以使用`;`分割数据列，但是不要混用。

```groovy
class MathSpec extends Specification {
  def "maximum of two numbers"() {
    expect:
    Math.max(a, b) == c

    where:
    a | b || c
    1 | 3 || 3
    7 | 4 || 7
    0 | 0 || 0
  }
}
```

# `@Rollup`

Spock 2.0后默认每个测试方法就是`@Unroll`的，增加了 `@Rollup` 注解用于合并测试结果报告。这种默认行为可以在配置文件种改变。

`@Unroll`注解作用是报告测试结果时会对每行测试用例生成一个结果:

```groovy
@Unroll
def "maximum of two numbers"() { ... }
```

`@Rollup`，`@Unroll`也可以标记测试类，相当于标记了该测试类中的所有测试方法。

# 数据管道

数据表并不是为数据变量提供数值的唯一方法。事实上，数据表只是一个或多个数据管道的语法糖：

```groovy
...
where:
a << [1, 7, 0]
b << [3, 4, 0]
c << [3, 7, 0]
```

数据管道用左移（`<<`）操作符声明，左边是测试变量，右边是数据提供者。数据提供者不一定就是列表，可以是任意的可迭代对象，比如集合、字符串、文本文件，数据库或电子表格等。

# 多值数据管道

如果数据提供者每次迭代返回多个值，则可以同时赋值给多个数据变量。其语法与 Groovy 的多重赋值有些类似，但左侧使用的是方括号而不是圆括号：

```groovy
@Shared sql = Sql.newInstance("jdbc:h2:mem:", "org.h2.Driver")

def "maximum of two numbers"() {
  expect:
  Math.max(a, b) == c

  where:
  [a, b, c] << sql.rows("select a, b, c from maxdata")
}
```

不关心的数据值可以用`_`忽略：

```groovy
...
where:
[a, b, _, c] << sql.rows("select * from maxdata")
```

当然也可以使用Groovy的标准多变量赋值语法，注意这里是圆括号

```groovy
...
where:
row << sql.rows("select * from maxdata")
(a, b, _, c) = row
```

多重赋值还可以嵌套

```groovy
...
where:
[a, [b, _, c]] << [
  ['a1', 'a2'].permutations(),
  [
    ['b1', 'd1', 'c1'],
    ['b2', 'd2', 'c2']
  ]
].combinations()
```

## 命名结构数据管道

从Spock 2.2起，多变量数据管道也可以从映射中解构。当数据提供者返回一个带有命名键的映射时，这就非常有用了。或者，如果你的数值较长，无法很好地放入数据表中，那么使用映射会更容易读取。

```groovy
...
where:
[a, b, c] << [
  [
    a: 1,
    b: 3,
    c: 5
  ],
  [
    a: 2,
    b: 4,
    c: 6
  ]
]
```

你可以对嵌套数据管道使用命名解构，但仅限于最内层的嵌套层。

```groovy
...
where:
[a, [b, c]] << [
  [1, [b: 3, c: 5]],
  [2, [c: 6, b: 4]]
]
```

# 测试变量赋值

数据变量可以直接赋值：

```groovy
...
where:
a = 3
b = Math.random() * 100
c = a > b ? a : b
```

赋值的右侧可以引用其他数据变量，同样的数据表中也可以引用前几列

```groovy
...
where:
a | b
3 | a + 1
7 | a + 2
0 | a + 3
```

# 数据表，数据管道和变量赋值混用

数据表、数据管道和变量赋值可根据需要进行组合：

```groovy
...
where:
a | b
1 | a + 1
7 | a + 2
0 | a + 3

c << [3, 4, 0]

d = a > c ? a : c
```

# 类型转换

数据变量值会强制转换为已声明的参数类型。

```groovy
def "type coercion for data variable values"(Integer i) {
  expect:
  i instanceof Integer
  i == 10

  where:
  i = "10"
}
```

# 迭代次数

迭代次数取决于可用数据的多少。同一方法的连续执行会产生不同的迭代次数。多种方式混用的情况下，如果某个数据提供者比其他数据提供者更早耗尽数据值，就会出现异常。变量赋值不会影响迭代次数。只包含赋值的 `where:` 块会产生一次迭代。

# 关闭数据提供者

所有迭代完成后，所有拥有零参数`close`方法的数据提供者都会调用该方法。

# 迭代名称

```groovy
def "maximum of #a and #b is #c"() {
...
```

该方法名使用占位符（以`#` 表示）来引用数据变量 a、b 和 c。在输出中，占位符将被替换为具体值。和Groovy GString类似，但有以下不同

* 使用`#`开头代替`$`, 没有`${…}`语法。
* 表达式只支持属性访问和零参数方法调用。

```groovy
def "#person is #person.age years old"() { // 访问属性
def "#person.name.toUpperCase()"() { // 调用0参数方法
```

以上是有效的方法名，以下是无效的方法名称：

```groovy
def "#person.name.split(' ')[1]" {  // 不能有方法参数
def "#person.age / 2" {  // 不能使用操作符
```

此外，数据变量还支持 `#featureName` 和 `#iterationIndex` 标记。

```groovy
def"#person is #person.age years old [#iterationIndex]"() {
```

除了将 unroll-pattern 作为方法名指定外，还可以将其作为 `@Unroll` 注解的参数，该注解优先于方法名：

```groovy
@Unroll("#featureName[#iterationIndex] (#person.name is #person.age years old)")
def "person age should be calculated properly"() {
// ...
```

将报告为

```
╷
└─ Spock ✔
   └─ PersonSpec ✔
      └─ person age should be calculated properly ✔
         ├─ person age should be calculated properly[0] (Fred is 38 years old) ✔
         ├─ person age should be calculated properly[1] (Wilma is 36 years old) ✔
         └─ person age should be calculated properly[2] (Pebbles is 5 years old) ✔
```

这样做的好处是，你可以为测试方法创建一个描述性的方法名，同时为每个迭代创建一个单独的模板。此外，测试方法名称中不会出现占位符，因此可读性更好。

## 特殊标记

这是特殊标记的完整列表：

* `#featureName` 是方法名称
* `#iterationIndex` 是当前迭代索引
* `#dataVariables` 列出本次迭代的所有数据变量，例如 `x: 1, y: 2, z: 3`
* `#dataVariablesWithIndex` 与 `#dataVariables` 相同，但在末尾加了一个索引，例如 `x：1, y：2, z：3, #0`

## 配置

可以在classpath路径添加配置文件 `SpockConfig.groovy` 设置默认 unroll-pattern

```groovy
unroll {
    defaultPattern '#featureName[#iterationIndex]'
}
```

# 参考文档

本文翻译自 [Spock文档](https://spockframework.org/spock/docs/2.3/data_driven_testing.html)