
## Timecast Frontend
This application is made in order of a school project.

The application is made to manage employees with their corresponding projects. It is possible to create new Employees
and new Projects. An Employee needs a Contract to be able to be assigned to a project. With creating Allocations an
employee can then be assigned to a project according to the employees pensum.

<br>

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
* Set server address and port
* Set the API URL
* Define the correct keys for authentication and SSL (See also the chapters below)
* Set logging level

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

<br>

#### Using JWT
To use the application it is necessary that the respective backend API is providing the authentication in form of a JWT
Token. Because the signature will be checked this Token must be signed using RSA512 algorithm.

(If it's necessary to change that behaviour the respective code in **```wodss.timecastfrontend.security.JwtUtil```** file.)

Command to generate RSA keys:  ```openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:2048```

Command to extract public key from private key: ```openssl rsa -pubout -in private_key.pem -out public_key.pem```

<br>

#### Project Structure

Java class structure

    .
    └──timecastfrontend
       ├── domain                              # The entities used in the application
       ├── dto                                 # The DTO entities which are used for the communication to the backend
       ├── exceptions                          # The Exception definitions which are specific for the application
       ├── security                            # All Security related classes, such as Authentication, JWT and RSA utils.
       ├── services                            # The abstraction layer for the communication to the backend
       │   └── mocks                           # Service Mocks were only used while developing so no API access was needed.
       │                                         (The mocks are not needed when running in production)
       ├── util                                # Additional utils specific for the application
       ├── web                                 # The Web Controllers which are entry point for the frontend requests.
       │
       ├── TimecastFrontendApplication.java    # The main application class ( + Resttemplate configuration).
       └── WebMvcConfig.java                   # Additional application configuration (Security, Interceptors).

<br>
Example keystore structure

    .
    └──keystore
       ├── jwt_privkey.pem                     # The entities used in the application
       ├── jwt_pubkey.pem                      # The DTO entities which are used for the communication to the backend
       ├── timecast_backend.p12                # The Exception definitions which are specific for the application
       └── timecast_frontend.p12               # All Security related classes, such as Authentication, JWT and RSA utils.

<br>
Template structure

    .
    └──templates
       ├── allocations                         # Create, Update and List htmls for Allocations
       ├── contracts                           # Update and List htmls for Contracts (Create is in employees)
       ├── employees                           # Create, Update and List htmls for Employees
       │   └── contracts                       # Create html for contracts since contracts can only be created for a specific employee.
       ├── errors                              # Simple Error htmls (403, 404, 500 and other generic error)
       ├── projects                            # Show, Create, Update and List htmls for Projects
       │
       ├── about.html                          # A short description about the application
       ├── header.html                         # The header html for each page
       ├── layout.html                         # The main layout. Can be referenced by any other content page and
       │                                         automatically sets header and navigation to that page.
       ├── login.html                          # The login page
       ├── myprofile.html                      # Page which shows the own profile information and contracts
       ├── myprojects.html                     # Page which shows the projects where the current logged in user is assigned to.
       └── navigation.html                     # Navigation side bar which contains the links to the other available pages.


<br>

## Changes in the default Spring Configuration behaviour
#### Spring Security
Classes
* ```wodss.timecastfrontend.```**```WebMvcConfig```**
* ```wodss.timecastfrontend.security.```**```CustomAuthenticationProvider```**

In the WebMvcConfig is configured which URLs are permitted to which Role, the login and the logout behaviour. Also the
CustomAuthenticationProvider will be set there as Authentication Provider.

In the CustomAuthenticationProvider is configured how SpringSecurity should authenticate a user when he tries to login.
For the authentication a Token request will then be sent to the API.

See also [SpringSecurity Authentication Provider](https://www.baeldung.com/spring-security-authentication-provider)

<br>

#### RestTemplate
Classes
* ```wodss.timecastfrontend.```**```TimecastFrontendApplication```**
* ```wodss.timecastfrontend.exceptions.```**```RestTemplateResponseErrorHandler```**

In the TimecastFrontendApplication is configured how the RestTemplate should behave. Therefore is defined that SSL should
be used and Error Responses should be mapped to Application Internal Exceptions, defined in the RestTemplateResponseErrorHandler.

See also [Spring HttpClient with SSL](https://www.baeldung.com/httpclient-ssl)

In the RestTemplateResponseErrorHandler the default behaviour of the ErrorHandler from the RestTemplate will be overriden,
such that Application Internal Exceptions will be thrown instead of HttpClientErrorExceptions.

See also [Spring RestTemplate Error Handling](https://www.baeldung.com/spring-rest-template-error-handling)

<br>

#### Exception Handling
Classes
* ```wodss.timecastfrontend.```**```TimecastFrontendApplication```**
* ```wodss.timecastfrontend.web.```**```CustomErrorController```**
* ```wodss.timecastfrontend.web.```**```GlobalExceptionHandler```**

In the TimecastFrontendApplication is an ErrorHandlerResolver configured which handles errors that occur in Interceptors
or Filters outside of a Controller.

In the CustomErrorController is the default behaviour of Spring Error Handling outside of controller overriden. Errors
like when no controller is found will result in an application defined error pages, in this example in 404.

See also [SpringBoot Customize Whitelabel Error Page](https://www.baeldung.com/spring-boot-custom-error-page)

In the GlobalExceptionHandler is defined which exception thrown by a controller will lead to which error page.

See also [Error Handling for REST with Spring](https://www.baeldung.com/exception-handling-for-rest-with-spring)

<br>

## Documentation References
* [Spring MVC Doc](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc)
* [Thymeleaf 2.1 Doc](https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html)
* [Bootstrap 4.3 Doc](https://getbootstrap.com/docs/4.3)
