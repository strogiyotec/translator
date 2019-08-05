# translator
Rest api to translate text 

This is the test project that uses Yandex API in order to translate text

In order to run use the following command

```groovy
mvn spring-boot:run
```

If you want to contribute please check that your code doesn't have any checkstyle errors

```groovy

mvn:checkstyle:checkstyle
```
Also check that all tests work 

```groovy
mvn test
```

Configurable properties: in order to configure project you can use **application.properties** file (src/resources)

1) **api.key** - api key for yandex translator api
2) **port** - server port

This project uses H2 db.
In order to do database migrations Flyway is used

How to use?

Example:

```groovy
 curl -X GET 'http://localhost:8080/translate?languageTo=ru&languageFrom=en&text=hello'
```