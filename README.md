# DEV4Sep
A Platform for RESTfull API Microservice with Spring Boot 3.2.6 and Java 17

## Requirements
* Spring Boot 3.2.6
* Java 17
* MySQL 8.4
* Apache Tomcat/10.1.24

<br> Instructions: How to run local development
=================
Project on repository: https://github.com/yisivlay/dev4sep-provider.git
>git clone https://github.com/yisivlay/dev4sep-provider.git

Run the following commands:
1. `docker compose up`
2. `./gradlew createDB -PdbName=dev4sep-tenants`
3. `./gradlew createDB -PdbName=dev4sep-default`
4. `./gradlew bootRun`

<br> Manage Apache2 Licenses 
============================
Run the following commands:
* Added Apache License to Header of Class
1. `./gradlew licenseFormat`
* Check Apache License
2. `./gradlew license`
* Download Apache License
3. `./gradlew downloadLicenses`
* Generate License Report
4. `./gradlew generateLicenseReport`
