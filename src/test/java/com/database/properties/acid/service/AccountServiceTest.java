package com.database.properties.acid.service;

import com.database.properties.acid.model.Account;
import com.database.properties.acid.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void transferMoneySuccessfully() {
        Account fromAccount = new Account("O1", new BigDecimal("100.00"));
        Account toAccount = new Account("O2", new BigDecimal("50.00"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        accountService.transferMoney(1L, 2L, new BigDecimal("30.00"));

        verify(accountRepository).save(fromAccount);
        verify(accountRepository).save(toAccount);
    }

    @Test
    void transferMoneyInsufficientBalance() {
        Account fromAccount = new Account("O1", new BigDecimal("20.00"));
        Account toAccount = new Account("O2", new BigDecimal("50.00"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        assertThrows(IllegalArgumentException.class, () -> accountService.transferMoney(1L, 2L, new BigDecimal("30.00")));
    }

    @Test
    void transferMoneyInvalidFromAccountId() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> accountService.transferMoney(1L, 2L, new BigDecimal("30.00")));
    }

    @Test
    void transferMoneyInvalidToAccountId() {
        Account fromAccount = new Account("O1", new BigDecimal("100.00"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> accountService.transferMoney(1L, 2L, new BigDecimal("30.00")));
    }
}