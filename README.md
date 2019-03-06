
## Timecast Frontend Setup

Technologies used in this project:
* Gradle
* SpringBoot
* Rocker

Rocker templates have to be compiled into Java classes before they can be used in code.
Compile Rocker HTMLs using the Gradle Task
```gradlew compileRocker```


Compile Code using the Gradle Task
```gradlew compileJava```


Both Gradle Tasks will also be done when starting the SpringBoot application using the Gradle Task
```gradlew bootRun```


## References
* [Rocker Syntax](https://github.com/fizzed/rocker/blob/master/docs/SYNTAX.md)
