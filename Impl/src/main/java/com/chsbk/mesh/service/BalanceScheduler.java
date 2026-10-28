package com.chsbk.mesh.service;

import com.chsbk.mesh.model.entity.Account;
import com.chsbk.mesh.repository.AccountRepository;
import com.chsbk.mesh.service.event.UsersChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "mesh.scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BalanceScheduler {

    private static final BigDecimal INCREASE = new BigDecimal("1.10");
    private static final BigDecimal MAXIMUM_MULTIPLIER = new BigDecimal("2.07");

    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedRateString = "${mesh.scheduler.interval-milliseconds:30000}")
    @Transactional
    public void increaseBalances() {
        Set<Long> changedUserIds = new HashSet<>();
        for (Account account : accountRepository.findAll()) {
            BigDecimal newBalance = calculateNewBalance(account);
            if (newBalance.compareTo(account.getBalance()) > 0) {
                account.setBalance(newBalance);
                changedUserIds.add(account.getUser().getId());
            }
        }
        if (!changedUserIds.isEmpty()) {
            eventPublisher.publishEvent(new UsersChangedEvent(changedUserIds));
        }
    }

    private BigDecimal calculateNewBalance(Account account) {
        BigDecimal maximumBalance = account.getInitialBalance()
                .multiply(MAXIMUM_MULTIPLIER)
                .setScale(2, RoundingMode.HALF_EVEN);
        if (account.getBalance().compareTo(maximumBalance) >= 0) {
            return account.getBalance();
        }
        BigDecimal grownBalance = account.getBalance().multiply(INCREASE)
                .setScale(2, RoundingMode.HALF_EVEN);
        return grownBalance.min(maximumBalance);
    }
}
