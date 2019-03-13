
## Timecast Frontend Setup

#### Technologies used in this project
* Java
* Gradle
* SpringMVC
* SpringBoot
* Thymeleaf 2.1
* Bootstrap 4.3

<br>

#### Environment
Run the application with
<br>
``` gradlew bootRun ```

<br>

Configurations (Port, Logging, etc.) can be found in the File<br>
``` src / main / resources / ```**``` application.properties ```**

<br>

For using Thymeleaf Hot-Reload in IntelliJ<br>
Open the Settings --> Build-Execution-Deployment --> Compiler and enable:
<br>
``` Build Project Automatically. ```
<br>

Press **Ctrl + Shift + A** and search **Registry** Select **Registry...** and enable:<br>
``` compiler.automake.allow.when.app.running ``` 

<br>


## References
* [Spring MVC Doc](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc)
* [Thymeleaf 2.1 Doc](https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html)
* [Bootstrap 4.3 Doc](https://getbootstrap.com/docs/4.3)