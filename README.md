# ACID

A small Spring Boot application that demonstrates the **ACID** properties of a relational
database through a minimal banking use case: transferring money between two accounts.

## Tech stack

- Java 25
- Spring Boot 4.1.1
- Spring Web (REST)
- Spring Data JPA / Hibernate
- H2 in-memory database
- Maven
- JUnit 5 + Mockito

## Prerequisites

- JDK 25 (a full JDK with `javac`, not only a JRE)
- Maven 3.9+

Build and run with:

```bash
export JAVA_HOME=/path/to/jdk-25
mvn spring-boot:run
```

## Running the application

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`. Two accounts are seeded at startup:

| id | owner | balance |
|----|-------|---------|
| 1  | Alice | 100.00  |
| 2  | Bob   | 50.00   |

## API

List accounts:

```bash
curl http://localhost:8080/accounts
```

Transfer money:

```bash
curl -X POST "http://localhost:8080/accounts/transfer?fromAccountId=1&toAccountId=2&amount=30"
```

After the transfer Alice has `70.00` and Bob has `80.00`. Transferring more than the
available balance, using an unknown account id, a non-positive amount or the same account
as source and target all fail with `400 Bad Request` and leave the data unchanged.

## Tests

```bash
mvn test
```

## ACID properties in the code

### Atomicity

`AccountService.transferMoney` is annotated with `@Transactional`. The debit and the
credit are executed in the same transaction, so either both are committed or, if any step
throws, the transaction is rolled back and neither balance changes.

```java
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
    ...
    fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
    toAccount.setBalance(toAccount.getBalance().add(amount));

    accountRepository.save(fromAccount);
    accountRepository.save(toAccount);
}
```

### Consistency

Business invariants are enforced before any write:

- the amount must be positive
- source and target accounts must differ
- the source account must have enough balance

Because the method is transactional, a violation aborts the transaction and the database
stays in a valid state. The `Account` entity also models the persisted state with a
numeric balance.

### Isolation

The transaction uses `Isolation.SERIALIZABLE`, the strictest isolation level. Concurrent
transfers involving the same accounts are serialized by the database, preventing lost
updates and other read anomalies. With a lower level (for example `READ_COMMITTED`) two
concurrent transfers could both read the same balance and overwrite each other.

### Durability

Committed transactions are persisted by the underlying database (H2 in this demo, a
production RDBMS in real deployments). Once `transferMoney` returns successfully the new
balances survive process restarts and crashes.

## Where to find things

- `src/main/java/.../model/Account.java` - JPA entity.
- `src/main/java/.../repository/AccountRepository.java` - Spring Data JPA repository.
- `src/main/java/.../service/AccountService.java` - transactional business logic.
- `src/main/java/.../controller/AccountController.java` - REST endpoints.
- `src/main/resources/data.sql` - seed data for the demo.
- `src/test/java/.../service/AccountServiceTest.java` - unit tests with Mockito.

## Notes

- H2 runs in memory, so data is reset on every restart. This keeps the demo reproducible.
- The `SERIALIZABLE` isolation level is chosen to make the isolation guarantee explicit;
  it may reduce throughput under heavy concurrency in a real system.