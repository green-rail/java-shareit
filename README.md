# java-shareit
A RESTful multi-module web-service for sharing items with Gateway-Server architecture. Users can add new items, search for items, create requests for items, rent items and comment on completed rentals. The application consists of three microservices: gateway, business logic server and the database. Each microservice is deployed in its own Docker container.


### Stack
Java 11, Spring Boot 2.7, Maven, JPA(Hibernate) PostgreSQL, Mockito, Docker.

### Build and run
 - mvn clean package
 - docker-compose up -d
