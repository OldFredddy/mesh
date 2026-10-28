package com.chsbk.mesh.service;

import java.math.BigDecimal;

public interface TransferService {

    void transfer(long senderUserId, String recipient, BigDecimal amount);
}
