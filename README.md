# DEV4Sep
A Platform for RESTful API Microservice with Spring Boot 3.2.6 and Java 17

## Requirements
* Spring Boot 3.2.6
* Java 17
* MySQL 8.4
* Apache Tomcat/10.1.24

<br> Instructions: How to run local development
=================
Clone project:
>git clone https://github.com/yisivlay/dev4sep-provider.git

Run the following commands:
1. `./gradlew createDB -PdbName=dev4sep-tenants`
2. `./gradlew createDB -PdbName=dev4sep-default`
3. `./gradlew bootRun`
