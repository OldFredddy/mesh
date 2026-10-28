package com.chsbk.mesh.service;

import com.chsbk.mesh.logging.TransferAuditLogger;
import com.chsbk.mesh.model.entity.Account;
import com.chsbk.mesh.model.entity.User;
import com.chsbk.mesh.repository.AccountRepository;
import com.chsbk.mesh.repository.UserRepository;
import com.chsbk.mesh.service.event.UsersChangedEvent;
import com.chsbk.mesh.service.impl.TransferServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransferAuditLogger transferAuditLogger;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TransferServiceImpl transferService;

    private Account senderAccount;
    private Account recipientAccount;

    @BeforeEach
    void setUp() {
        senderAccount = Account.builder().id(100L)
                .balance(new BigDecimal("100.00"))
                .initialBalance(new BigDecimal("100.00")).build();
        recipientAccount = Account.builder().id(200L)
                .balance(new BigDecimal("50.00"))
                .initialBalance(new BigDecimal("50.00")).build();
    }

    @Test
    void transferByUserIdUpdatesBalancesWhenAccountIdsDifferFromUserIds() {
        when(accountRepository.findByUser_Id(1L)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByUser_Id(2L)).thenReturn(Optional.of(recipientAccount));

        transferService.transfer(1L, "2", new BigDecimal("40.00"));

        assertThat(senderAccount.getBalance()).isEqualByComparingTo("60.00");
        assertThat(recipientAccount.getBalance()).isEqualByComparingTo("90.00");
        verify(transferAuditLogger).success(1L, 2L, new BigDecimal("40.00"));
        verify(eventPublisher).publishEvent(any(UsersChangedEvent.class));
    }

    @Test
    void transferByPhoneResolvesUser() {
        User user = User.builder().id(2L).build();
        when(userRepository.findByPhones_Phone("711111111111"))
                .thenReturn(Optional.of(user));
        when(accountRepository.findByUser_Id(1L)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByUser_Id(2L)).thenReturn(Optional.of(recipientAccount));

        transferService.transfer(1L, "711111111111", new BigDecimal("1.00"));

        assertThat(recipientAccount.getBalance()).isEqualByComparingTo("51.00");
    }

    @Test
    void insufficientFundsCannotChangeBalance() {
        when(accountRepository.findByUser_Id(1L)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByUser_Id(2L)).thenReturn(Optional.of(recipientAccount));

        assertThatThrownBy(() -> transferService.transfer(1L, "2", new BigDecimal("400.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Недостаточно средств");

        assertThat(senderAccount.getBalance()).isEqualByComparingTo("100.00");
        verify(eventPublisher, never()).publishEvent(any(UsersChangedEvent.class));
    }

    @Test
    void selfTransferIsRejected() {
        assertThatThrownBy(() -> transferService.transfer(1L, "1", new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(eventPublisher, never()).publishEvent(any(UsersChangedEvent.class));
    }

    @Test
    void zeroNegativeAndFractionalAmountAreRejected() {
        for (BigDecimal amount : new BigDecimal[]{BigDecimal.ZERO, new BigDecimal("-1"), new BigDecimal("0.001")}) {
            assertThatThrownBy(() -> transferService.transfer(1L, "2", amount))
                    .isInstanceOf(IllegalArgumentException.class);
        }
        verify(eventPublisher, never()).publishEvent(any(UsersChangedEvent.class));
    }

    @Test
    void missingRecipientAccountReturnsNotFound() {
        when(accountRepository.findByUser_Id(1L)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByUser_Id(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.transfer(1L, "2", BigDecimal.ONE))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
