package com.bank.core.dto;

import java.io.Serializable;
import java.util.UUID;

public record TransferMessage(
        UUID transferId,
        Long fromAccountId,
        Long toAccountId,
        double amount
) implements Serializable {
}
