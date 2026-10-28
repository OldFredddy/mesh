package com.chsbk.mesh.service;

import com.chsbk.mesh.model.entity.Account;
import com.chsbk.mesh.model.entity.User;
import com.chsbk.mesh.repository.AccountRepository;
import com.chsbk.mesh.service.event.UsersChangedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceSchedulerTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BalanceScheduler balanceScheduler;

    @Test
    void increaseBalanceRespectsMaximumAndScale() {
        Account account = Account.builder()
                .user(User.builder().id(7L).build())
                .balance(new BigDecimal("200.00"))
                .initialBalance(new BigDecimal("100.00"))
                .build();
        when(accountRepository.findAll()).thenReturn(List.of(account));

        balanceScheduler.increaseBalances();

        assertThat(account.getBalance()).isEqualByComparingTo("207.00");
        assertThat(account.getBalance().scale()).isEqualTo(2);
        verify(eventPublisher).publishEvent(any(UsersChangedEvent.class));
    }
}
