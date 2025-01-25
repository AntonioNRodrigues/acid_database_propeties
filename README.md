# acid_database_properties
Project that demonstrates the ACID properties of database

## ACID Properties of Database

This project demonstrates the ACID properties of a database using a simple banking application. The ACID properties are:

- **Atomicity**: Ensures that all operations within a transaction are completed successfully. If any operation fails, the entire transaction is rolled back.
- **Consistency**: Ensures that a transaction brings the database from one valid state to another, maintaining database invariants.
- **Isolation**: Ensures that concurrently executing transactions do not affect each other.
- **Durability**: Ensures that once a transaction is committed, it remains so, even in the event of a system failure.

### Where to Find in the Code

- **Atomicity**: Managed by the transaction management in the `AccountService` class.
- **Consistency**: Ensured by the business logic in the `AccountService` class and the constraints defined in the `Account` entity.
- **Isolation**: Handled by the transaction isolation levels configured in the Spring Boot application.
- **Durability**: Guaranteed by the database management system (DBMS) used in the project.

### Code Examples

- `src/main/java/com/database/properties/acid/model/Account.java`: Defines the `Account` entity.
- `src/main/java/com/database/properties/acid/service/AccountService.java`: Contains the business logic for transferring money between accounts.
- `src/test/java/com/database/properties/acid/service/AccountServiceTest.java`: Contains unit tests for the `AccountService` class.

