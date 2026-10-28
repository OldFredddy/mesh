package com.chsbk.mesh.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class TransferAuditLogger {

    public void start(Long from, Long to, BigDecimal amount) {
        log.info("Transfer: REQUEST  | from={} to={} amount={}", from, to, amount);
    }

    public void success(Long from, Long to, BigDecimal amount) {
        log.info("Transfer: SUCCESS  | from={} to={} amount={}", from, to, amount);
    }

    public void rejected(String reason, Long from, Long to) {
        log.warn("Transfer: REJECTED | reason={} from={} to={}", reason, from, to);
    }

    public void error(Exception ex, Long from, Long to) {
        log.error("Transfer: ERROR    | from={} to={} - {}", from, to, ex.toString());
    }
}
