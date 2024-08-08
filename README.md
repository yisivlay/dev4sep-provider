# DEV4Sep
A Platform for RESTfull API Microservice with Spring Boot 3.3.1 and Java 21

## Requirements
* Spring Boot 3.3.1
* Java 21
* MySQL/Mariadb 11.4.2
* Apache Tomcat 10.1.24

<br> Instructions: How to run local development
===============================================
Project on repository: https://github.com/yisivlay/dev4sep-provider.git
>git clone https://github.com/yisivlay/dev4sep-provider.git

Docker images: Database and Keycloak
>docker pull sivlayyi/dev4sep-provider:latest 

Run the following commands:
1. `docker compose up`
2. `./gradlew createDB -PdbName=dev4sep-tenants`
3. `./gradlew createDB -PdbName=dev4sep-default`
4. `./gradlew bootRun`

MySQL/Mariadb admin portal:
--------------------------
https://lcoalhost:9001
> username: root, password: admin@2024!
----------------------------

Manual Database Connection:
---------------------------
```
spring.datasource.hikari.driverClassName=org.mariadb.jdbc.Driver
spring.datasource.hikari.jdbcUrl=jdbc:mariadb://localhost:3307/dev4sep-tenants
spring.datasource.hikari.username=root
spring.datasource.hikari.password=admin@2024!

dev4sep.tenant.host=localhost
dev4sep.tenant.port=3307
dev4sep.tenant.username=root
dev4sep.tenant.password=admin@2024!
dev4sep.tenant.name=dev4sep-default
```

Keycloak portal:
----------------
http://localhost:9000/admin/master/console/
> username: admin, password: admin@2024!

<br> Security
=============
HTTP Basic Authentication
-------------------------
By default, is configured with a HTTP Basic Authentication scheme, so you actually don't have to do anything if you want to use it.
1. `dev4sep.security.basicauth.enabled=true`
2. `dev4sep.security.oauth.enabled=false`

```
POST: https://localhost:8444/dev4sep/api/v1/authentication
--header 'Content-Type: application/json'
--header 'DEV4Sep-Platform-TenantId: default'
```

HTTP OAuth2 Authentication
-------------------------
1. `dev4sep.security.basicauth.enabled=false`
2. `dev4sep.security.oauth.enabled=true`

Note: These requirements check from Keycloak configuration
```
POST: http://localhost:9000/realms/dev4sep/protocol/openid-connect/token
--header 'Content-Type: application/x-www-form-urlencoded'
--data-urlencode 'username=admin' 
--data-urlencode 'password=admin@2024!'
--data-urlencode 'client_id=dev4sep-client'
--data-urlencode 'grant_type=password'
--data-urlencode 'client_secret=<enter the client secret from credentials tab>'
```

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
