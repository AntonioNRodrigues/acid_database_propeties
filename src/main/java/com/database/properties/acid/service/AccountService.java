package com.database.properties.acid.service;

import com.database.properties.acid.model.Account;
import com.database.properties.acid.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business logic for the banking demo.
 *
 * <p>The {@link #transferMoney(Long, Long, BigDecimal)} method is the main example of an
 * ACID transaction: it reads two accounts, validates the business rule (sufficient
 * balance), updates both rows and commits. If any step fails the whole transaction is
 * rolled back (Atomicity), the balance invariant is preserved (Consistency), concurrent
 * transfers are serialized (Isolation) and committed changes survive restarts (Durability).
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Transfers {@code amount} from one account to another inside a single transaction.
     *
     * @throws IllegalArgumentException if an account id does not exist, the amount is not
     *         positive, or the source account has insufficient balance.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Source and target accounts must differ");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid from account ID: " + fromAccountId));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid to account ID: " + toAccountId));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // Both updates happen in the same transaction: either both are committed or none.
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    @Transactional(readOnly = true)
    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }
}
