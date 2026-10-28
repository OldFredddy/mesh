package com.chsbk.mesh.service.impl;

import com.chsbk.mesh.model.entity.Account;
import com.chsbk.mesh.repository.AccountRepository;
import com.chsbk.mesh.repository.UserRepository;
import com.chsbk.mesh.service.TransferService;
import com.chsbk.mesh.logging.TransferAuditLogger;
import com.chsbk.mesh.service.event.UsersChangedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private static final Pattern USER_ID_PATTERN = Pattern.compile("\\d{1,10}");
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("\\d{11,13}");

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransferAuditLogger transferAuditLogger;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transfer(long senderUserId, String recipient, BigDecimal amount) {
        transferAuditLogger.start(senderUserId, null, amount);
        Long recipientUserId = null;

        try {
            validateAmount(amount);
            recipientUserId = resolveRecipientId(recipient);
            if (senderUserId == recipientUserId) {
                throw new IllegalArgumentException("Нельзя переводить деньги самому себе");
            }

            Account senderAccount = accountRepository.findByUser_Id(senderUserId)
                    .orElseThrow(() -> new EntityNotFoundException("Счёт отправителя не найден"));
            Account recipientAccount = accountRepository.findByUser_Id(recipientUserId)
                    .orElseThrow(() -> new EntityNotFoundException("Счёт получателя не найден"));

            if (senderAccount.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Недостаточно средств");
            }

            senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
            recipientAccount.setBalance(recipientAccount.getBalance().add(amount));

            eventPublisher.publishEvent(new UsersChangedEvent(Set.of(senderUserId, recipientUserId)));
            transferAuditLogger.success(senderUserId, recipientUserId, amount);
        } catch (IllegalArgumentException | EntityNotFoundException exception) {
            transferAuditLogger.rejected(exception.getMessage(), senderUserId, recipientUserId);
            throw exception;
        } catch (RuntimeException exception) {
            transferAuditLogger.error(exception, senderUserId, recipientUserId);
            throw exception;
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0 || amount.scale() > 2) {
            throw new IllegalArgumentException("Сумма должна быть положительной и иметь не более двух знаков после запятой");
        }
    }

    private long resolveRecipientId(String recipient) {
        if (recipient == null) {
            throw new IllegalArgumentException("Получатель не указан");
        }
        if (USER_ID_PATTERN.matcher(recipient).matches()) {
            return Long.parseLong(recipient);
        }
        if (PHONE_NUMBER_PATTERN.matcher(recipient).matches()) {
            return userRepository.findByPhones_Phone(recipient)
                    .map(user -> user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Получатель не найден"));
        }
        throw new IllegalArgumentException("Укажите ID пользователя или телефон (11–13 цифр)");
    }
}
