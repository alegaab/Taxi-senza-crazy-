package it.uniroma2.dicii.bd.model.domain;

import java.time.LocalDateTime;

public class RichiestaAttiva {
    private final int codice;
    private final String indirizzoPartenza;
    private final String indirizzoDestinazione;
    private final LocalDateTime timestampRichiesta;

    public RichiestaAttiva(int codice, String indirizzoPartenza, String indirizzoDestinazione,
                           LocalDateTime timestampRichiesta) {
        this.codice = codice;
        this.indirizzoPartenza = indirizzoPartenza;
        this.indirizzoDestinazione = indirizzoDestinazione;
        this.timestampRichiesta = timestampRichiesta;
    }

    public int getCodice() {
        return codice;
    }

    public String getIndirizzoPartenza() {
        return indirizzoPartenza;
    }

    public String getIndirizzoDestinazione() {
        return indirizzoDestinazione;
    }

    public LocalDateTime getTimestampRichiesta() {
        return timestampRichiesta;
    }

    @Override
    public String toString() {
        return String.format("%d) %s -> %s (%s)", codice, indirizzoPartenza, indirizzoDestinazione,
                timestampRichiesta);
    }
}
