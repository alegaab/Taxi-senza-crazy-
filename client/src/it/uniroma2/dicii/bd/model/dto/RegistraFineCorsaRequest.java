package it.uniroma2.dicii.bd.model.dto;

import java.math.BigDecimal;

public record RegistraFineCorsaRequest(
        String usernameSessione,
        int codiceRichiesta,
        BigDecimal importo
) {
}
