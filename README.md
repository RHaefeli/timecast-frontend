
## Timecast Frontend Setup

Technologies used in this project:
* Gradle
* SpringBoot
* Rocker
<br>

Rocker templates have to be compiled into Java classes before they can be used in code.
<br>Compile Rocker HTMLs using the Gradle Task
<br>
```gradlew compileRocker```
<br>
<br>

Compile Code using the Gradle Task
<br>
```gradlew compileJava```
<br>
<br>

Both Gradle Tasks will also be done when starting the SpringBoot application using the Gradle Task
<br>
```gradlew bootRun```

<br>

## References
* [Rocker Syntax](https://github.com/fizzed/rocker/blob/master/docs/SYNTAX.md)
* [Spring MVC](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc)
