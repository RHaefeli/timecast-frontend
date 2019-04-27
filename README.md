
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

Configurations (API, Port, Logging, etc.) can be found in the File<br>
``` src / main / resources / ```**``` template.application.properties ```**<br>
Copy that file, remove the prefix **```template.```** and configure the properties.
* Set the API URL

<br>

For using Thymeleaf Hot-Reload in IntelliJ<br>
Open the Settings --> Build-Execution-Deployment --> Compiler and enable:
<br>
``` Build Project Automatically. ```
<br>

Press **Ctrl + Shift + A** and search **Registry** Select **Registry...** and enable:<br>
``` compiler.automake.allow.when.app.running ``` 

<br>

#### SSL Certificate
Command to generate: ``` keytool -genkeypair -alias timecast -keyalg RSA -keysize 2048 -storetype PKCS12 -key
store timecast.p12 -validity 3650 ```<br>
(If you've been asked for Firstname and Lastname just enter your domain name, e.g. localhost)

Put the generated SSL certificate into to the truststore. Just put the generated certificate
Files in ```/src/main/resources/keystore/``` and adjust the ```application.properties``` if necessary.

To run the application without certificates just comment the ssl properties in ```application.properties```.



## References
* [Spring MVC Doc](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc)
* [Thymeleaf 2.1 Doc](https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html)
* [Bootstrap 4.3 Doc](https://getbootstrap.com/docs/4.3)
