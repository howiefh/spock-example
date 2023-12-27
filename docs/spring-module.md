Spock Spring 模块可与 Spring TestContext 框架集成。它支持 Spring 注解 `@ContextConfiguration` 和 `@ContextHierarchy`。此外，它还支持元注解 `@BootstrapWith`，因此任何使用 `@BootstrapWith` 进行注解的注解也能生效，如 `@SpringBootTest`、`@WebMvcTest`。

# 模拟

## Java Config

Spock 1.1 引入了 `DetachedMockFactory` 和 `SpockMockFactoryBean`，允许在测试类之外创建 Spock mock。但是只能在测试类内正常工作。不推荐这种方式，推荐后边直接用注解的方式。

```groovy
class DetachedJavaConfig {
  def mockFactory = new DetachedMockFactory()

  @Bean
  GreeterService serviceMock() {
    return mockFactory.Mock(GreeterService)
  }

  @Bean
  GreeterService serviceStub() {
    return mockFactory.Stub(GreeterService)
  }

  @Bean
  GreeterService serviceSpy() {
    return mockFactory.Spy(GreeterServiceImpl)
  }

  @Bean
  FactoryBean<GreeterService> alternativeMock() {
    return new SpockMockFactoryBean(GreeterService)
  }
}
```

要使用mock，只需像注入其他 Bean 一样注入mock。

```groovy
@Autowired @Named('serviceMock')
GreeterService serviceMock

@Autowired @Named('serviceStub')
GreeterService serviceStub

@Autowired @Named('serviceSpy')
GreeterService serviceSpy

@Autowired @Named('alternativeMock')
GreeterService alternativeMock

def "mock service"() {
  when:
  def result = serviceMock.greeting

  then:
  result == 'mock me'
  1 * serviceMock.getGreeting() >> 'mock me'
}

def "sub service"() {
  given:
  serviceStub.getGreeting() >> 'stub me'

  expect:
  serviceStub.greeting == 'stub me'
}

def "spy service"() {
  when:
  def result = serviceSpy.greeting

  then:
  result == 'Hello World'
  1 * serviceSpy.getGreeting()
}

def "alternative mock service"() {
  when:
  def result = alternativeMock.greeting

  then:
  result == 'mock me'
  1 * alternativeMock.getGreeting() >> 'mock me'
}
```

## 注解

Spock 1.2 新增了将mock从测试类注入到 `ApplicationContext` 的支持。其灵感来源于 Spring Boot 的 `@MockBean`（通过 Mockito 实现），但经过调整以适应 Spock 风格。它不需要任何 Spring Boot 依赖项，但需要 Spring Framework 4.3.5 或更高版本才能运行。

### 使用 `@SpringBean`

将 mock/stub/spy 注册为测试上下文中的 Spring Bean。

使用 `@SpringBean` 时，必须使用强类型字段， `def`或`Object` 将无法使用。此外，还需要使用标准 Spock 语法将 Mock/Stub/Spy 直接赋值给字段。

`@SpringBean` 定义可以替换 `ApplicationContext` 中现有的 Bean。

```groovy
@SpringBean
Service1 service1 = Mock()

@SpringBean
Service2 service2 = Stub() {
  generateQuickBrownFox() >> "blubb"
}

def "injection with stubbing works"() {
  expect:
  service2.generateQuickBrownFox() == "blubb"
}

def "mocking works was well"() {
  when:
  def result = service1.generateString()

  then:
  result == "Foo"
  1 * service1.generateString() >> "Foo"
}
```

### 使用 `@SpringSpy`

如果你想监视现有的Bean，可以使用`@SpringSpy`注解。与 `@SpringBean` 一样，字段必须是你想要监视的类型，但不能初始化。

```groovy
@SpringSpy
Service2 service2

@Autowired
Service1 service1

def "default implementation is used"() {
  expect:
  service1.generateString() == "The quick brown fox jumps over the lazy dog."
}

def "mocking works was well"() {
  when:
  def result = service1.generateString()

  then:
  result == "Foo"
  1 * service2.generateQuickBrownFox() >> "Foo"
}
```

### 使用 `@StubBeans`

`@StubBeans` 在 `ApplicationContext` 中注册普通存根实例。如果你只需要满足一些依赖关系，而不需要对这些存根做任何实际操作，可以使用此方法。如果需要控制存根，例如配置返回值，则使用 `@SpringBean` 代替。与 `@SpringBean` 一样，`@StubBeans` 也可以替换现有的 `BeanDefinition`，因此你可以用它来从 `ApplicationContext` 中移除真正的 `Bean`。`@StubBeans` 可以被 `@SpringBean` 替换，你可以利用这一点替换父类中定义的 `@StubBeans`。

```groovy
@StubBeans(Service2)
@ContextConfiguration(classes = DemoMockContext)
class StubBeansExamples extends Specification 
```

# 作用域

Spock 默认会忽略不是单例（在单例作用域中）的 Bean。要使 mock 适用于作用域内的 Bean，需要在测试类添加 `@ScanScopedBeans`注解，并确保作用域允许在设置阶段访问 Bean。

你可以使用 `@ScanScopedBeans` 的`value`属性限制只扫描某些作用域。

# 共享字段注入

由于某些限制，对共享字段的注入默认不启用，但可以在测试类上添加`org.spockframework.spring.EnableSharedInjection` 注解启用。有关详细信息，请参阅 `org.spockframework.spring.EnableSharedInjection` 的 javadoc。

# 参考文档

本文翻译自 [Spock文档](https://spockframework.org/spock/docs/2.3/modules.html)