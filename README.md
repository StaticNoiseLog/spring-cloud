Purpose
=======

Demonstrate some default practices for a Spring Boot application to be deployed in a cloud (and other environments).


Application Configuration
=========================

Properties
----------
If you have both YAML and properties files at the same time, Spring Boot picks .properties or .yaml files in the following sequence:

1. application-${profile}.yml
2. application-${profile}.properties
3. application.yml
4. application.properties

Note that properties already set earlier in the sequence are NOT overridden by later definitions.

There are more ways to configure properties with Spring Boot. See [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) for a full description.  
The most common ones seem to be command line arguments (`--server.port=8089)`, `SPRING_APPLICATION_JSON`, Java System properties (`-Dserver.port=8089`) and OS environment variables.
See the next section where these same mechanisms are demonstrated for setting a placeholder called APP_TITLE.

A list of [common application properties](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html) is documented for Spring.

Defining Properties at Deployment Time
--------------------------------------
The following syntax in an application properties file will cause the property `app.mandatory.property.title` to be set
to the value of `APP_TITLE` when the application is deployed. While you could set the property `app.mandatory.property.title`
directly with the mechanisms shown below, it may be useful to document properties by explicitly listing them in the application properties file.

    app.mandatory.property.title=${APP_TITLE}

Here are some ways how the value for a placeholder like APP_TITLE can be set as a property on deployment:

    java -jar ./build/libs/spring-cloud-0.0.1-SNAPSHOT.jar --APP_TITLE='App on DEV' --spring.profiles.active=dev

    java -DAPP_TITLE='App on DEV' -jar ./build/libs/spring-cloud-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

    APP_TITLE='App on DEV'
    export APP_TITLE
    java -jar ./build/libs/spring-cloud-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
    
An interesting option is setting properties through SPRING_APPLICATION_JSON, an inline JSON embedded in an environment variable or system property:

    java -jar ./build/libs/spring-cloud-0.0.1-SNAPSHOT.jar --spring.application.json='{"APP_TITLE":"App on DEV"}' --spring.profiles.active=dev

    java -DSPRING_APPLICATION_JSON='{"APP_TITLE":"App on DEV"}' -jar ./build/libs/spring-cloud-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

    SPRING_APPLICATION_JSON='{"APP_TITLE":"App on DEV"}'
    export SPRING_APPLICATION_JSON
    java -jar ./build/libs/spring-cloud-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev


Persistence
===========

Flyway
------
This dependency is enough to enable Flyway:

    runtimeOnly 'org.flywaydb:flyway-core'

The SQL scripts go in `resources/db.migration`.

With Spring Boot and the default setup, Hibernate (the default JPA implementation) plays nicely with Flyway:
The database is created by Flyway, Hibernate does not interfere.

If you want more control, for example use Hibernate to create an initial database for you as a starting point, here is a good article called 
["Best Practices for Flyway and Hibernate with Spring Boot"](https://rieckpil.de/howto-best-practices-for-flyway-and-hibernate-with-spring-boot/).                      
  
JPA
---
Use the `@Entity` annotation on the model classes.

Because this project wants to use MariaDB for cloud deployments and H2 for local tests, a way has to be found to generate primary keys that works in both scenarios. The way chosen was to use this annotation:

    @GeneratedValue(strategy = GenerationType.IDENTITY)

This requires auto increment for the primary key in the table definition in the DB:

    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY

Note that the strategy chosen for generating primary keys can have an impact on performance and should be evaluated depending on the technologies used. The article 
["How to generate primary keys with JPA and Hibernate"](https://thoughts-on-java.org/jpa-generate-primary-keys/) explains the available options.

Simply providing a `@RepositoryRestResource` repository interface gives you the standard set of JPA CRUD operations. 

H2
--
Used as the default when the application is run without any Spring profile.

It is enough to bring in the H2 dependency and JPA will use the in-memory H2 DB by default.
Spring Initializr has provided this dependency:

    runtimeOnly 'com.h2database:h2'

You do not have to set anything in `application.properties` for H2, but you could set `spring.datasource.* properties`
if you don't like the defaults.

Manually adding this property to `application.properties` enables the console at <http://localhost:8014/h2-console> (a web app served by your application, therefore it is available on the port defined with property `server.port`):

    spring.h2.console.enabled=true

By default you get an in-memory DB called "testdb". In the h2-console you have to use these settings:
* Driver Class: org.h2.Driver
* JDBC URL: jdbc:h2:mem:testdb
* User Name: sa
* Password: <leave empty>

(For the h2-console web app to work your application must act as a web server. The above listed dependencies seem to 
bring in all that is needed, essentially `spring-boot-starter-web`.)

MariaDB
-------
Required dependency:

    runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'

Setting the following properties with the shown values causes the application to use MariaDB:

    spring.datasource.url=jdbc:mariadb://localhost:3306/springboot_mariadb
    spring.datasource.username=root
    spring.datasource.password=root
    spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

While a database is automatically created when H2 is used, with MariaDB you have to provide an empty database before the first start of the application:

    DROP DATABASE IF EXISTS springboot_mariadb;
    CREATE DATABASE IF NOT EXISTS springboot_mariadb;

REST API
========

Easy HATEOAS
------------
With a single annotation `@RepositoryRestResource` your model class is exposed as a HATEOAS REST endpoint.

REST URI
--------
If you use the `@RepositoryRestResource` you get a plural URI for a class `Car`:  
<http://localhost:8014/cars>

And while this magically works for "Status" (../statuses, indeed), this automatism can also go terribly wrong:
For a `Passerby` class you get "http://localhost:8014/passerbies", which is grammatically unacceptable (it should be "passer**s**by").

See `ConfigController` for an example use of `@RequestMapping`  that lets you take control of the URI:  
<http://localhost:8014/config/app-title>


Spring Profiles
===============

List of Profiles
----------------
* no profile at all
* dev - running in the IDE
* prod-cloud - cloud deployment production
* prod-int-premises, prod-on-premises - deployment as WAR on a standalone web server like Tomcat

Configuration
-------------

Different properties can be set for each Spring profile using the naming convention `application-${profile}.properties`. With multiple profiles active, the property files are scanned and applied in alphabetically ascending order. Properties with the same name found in a later property file do NOT replace previously established values.

Choosing a Spring Profile
-------------------------

### IntelliJ ###
You can set profiles in "Active profiles" in the Run Configuration.

### Command line ###
Three ways to set Spring profiles on the command line:

    java -jar -Dspring.profiles.active=dev spring-cloud-0.0.1-SNAPSHOT.jar

    java -jar spring-cloud-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
    
    SPRING_PROFILES_ACTIVE="dev" java -jar ./build/libs/spring-cloud-0.0.1-SNAPSHOT.jar

### application.properties ###
    spring.profiles.active=dev
    
### manifest.yml ###
    applications:
      env:
        SPRING_PROFILES_ACTIVE: dev


Logging
=======

Overview
--------
[Logback](https://logback.qos.ch/) is the default for Spring Boot. And that's a good choice.

By default, logging goes to the console. This is what you want in a cloud environment.

Configuration
--------------
Use `logback-spring.xml` if you have special logging requirements.

In this example it assumed that the cloud application must also be deployed to legacy on-premises (= non-cloud) environments.
For this purpose the Spring profiles `int-on-premises` and `prod-on-premises` are used.
In `logback-spring.xml` it is specified that (only) the file-appender is used if one of these non-cloud Spring profiles is active.
The negation (no non-cloud profile active) is the standard case: Logging goes (only) to the console.

The name of the logfile is defined with `logging.file` in the corresponding properties files (`application-int-on-premises.properties` and `application-prod-on-premises.properties`).

For `int-on-premises` the DEBUG level is set with the `logging.level.root` property (noticeable slowdown).

Note that when you specify _both_ profiles, `int-on-premises` and `prod-on-premises`, at the same time you will get a mix of the properties.
The name `application-int-on-premises.properties` comes before `application-prod-on-premises.properties` in the alphabet and therefore any properties already set for "int-on-premises" will take precedence over those set in the other profile.
Of course this is purely hypothetical, in practice you would never use these Spring profiles together.

Demo
----
LogController: <http://localhost:8014/log>


Actuator
========

Activation
----------
Not enabled by default. To use you need this dependency:

    runtimeOnly 'org.springframework.boot:spring-boot-starter-actuator'

A property can be set to enable all actuator endpoints (due to security reasons this should not be done in production!):

    management.endpoints.web.exposure.include=*

Accessing
---------
Show all enabled endpoints:

<http://localhost:8014/actuator/>

The entire Spring environment:

<http://localhost:8014/actuator/env>


Deployment
==========

Cloud Foundry
-------------

### JAR ###

Deploy with app name `ulk` (but don't start it). This will result in the route <https://ulk.cfapps.io/> and that will be a conflict if another app on Cloud Foundry is using the same name.
A way to avoid this type of conflict is adding the option `--random-route` to the following command.

    cf push -p ./build/libs/spring-cloud-0.0.1-SNAPSHOT.jar ulk --no-start

Before starting the app set "User Provided Environment Variables" (in the Cloud Foundry web GUI):

    SPRING_PROFILES_ACTIVE      prod-cloud
    APP_TITLE                   "My App in the Cloud"

Note that there is no need to go trough the indirection of a `${VAR}` substitution with a properties file.
This is demonstrated with `application-prod-cloud.properties` which only contains the `app.mandatory.property.title` property.
And even that property technically does not have to appear in the properties file. It is just a convenient documentation that the application refers to a proprietary property.

Lets set the environment variables that will cause the app to connect to MariaDB (ClearDB):

    spring.datasource.url                 "jdbcUrl" from the service key
    spring.datasource.username            "username" from the service key
    spring.datasource.password            "password" from the service key
    spring.datasource.driver-class-name   org.mariadb.jdbc.Driver

This could be done in the web GUI, too, but for a change let's use the command line:

    cf set-env ulk spring.datasource.url "jdbc:mysql://us-cdbr-iron-east-02.cleardb.net/ad_e22d3fe44ce0542?user=bd50d0cdaa1c7b\u0026password=dbpassword"
    cf set-env ulk spring.datasource.username "bd50d0cdaa1c7b"
    cf set-env ulk spring.datasource.password "dbpassword"
    cf set-env ulk spring.datasource.driver-class-name "org.mariadb.jdbc.Driver"

About the driver class: Something must be used that is provided in the dependencies of the app itself.
In this case "org.mariadb.jdbc.Driver" is used and that works with ClearDB which currently is the MySQL service on
Cloud Foundry. But obviously you would want the best matching driver and you would have to add that in build.gradle
as a dependency.

Start/stop the app to test it:

    cf start ulk
    
### Manage App on Cloud Foundry ###

Restage a running app to make environment changes visible:

    cf restage ulk

Stop the app:
    cf stop ulk

Completely remove an application:

    cf delete -r ulk

### Docker ###
We need a JAR to build the Docker image:

    ./gradlew clean build

Start with a clean slate and delete all docker images, containers and volumes (careful!):

    docker system prune -a --volumes    # BE GONE, ye all!

Create the Docker image (tag is "greatest", more classical would be "latest"):

    docker build -t spring-cloud:greatest -f docker/Dockerfile .

To deploy a Docker image to Cloud Foundry it must live in a publicly accessible repository. If you have an
account on Docker Hub and are logged in as "staticnoiselog":

    docker tag spring-cloud:greatest staticnoiselog/spring-cloud:greatest
    docker push staticnoiselog/spring-cloud:greatest

Deploy Docker image to Cloud Foundry from Docker Hub. In this example environment variables are
 provided through a file `cf-manifest.yml`, but other mechanisms should work, too.
Note that in his setup the `server.port` must be set to 8080.

    CF_DOCKER_PASSWORD=docker-hub-password cf push --docker-image staticnoiselog/spring-cloud:greatest -f docker/cf-manifest.yml --no-start

    cf start dulk

Check it out:    
<https://dulk.cfapps.io/config/spring-profiles-active>    
