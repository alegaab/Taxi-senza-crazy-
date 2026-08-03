package it.uniroma2.dicii.bd.model.domain;

import java.math.BigDecimal;

public class ReportTassista {
    private final String patente;
    private final String nome;
    private final String cognome;
    private final int numeroCorse;
    private final BigDecimal guadagnoTotale;
    private final BigDecimal commissioneTotale;

    public ReportTassista(String patente, String nome, String cognome, int numeroCorse,
                          BigDecimal guadagnoTotale, BigDecimal commissioneTotale) {
        this.patente = patente;
        this.nome = nome;
        this.cognome = cognome;
        this.numeroCorse = numeroCorse;
        this.guadagnoTotale = guadagnoTotale;
        this.commissioneTotale = commissioneTotale;
    }

    public String getPatente() {
        return patente;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public int getNumeroCorse() {
        return numeroCorse;
    }

    public BigDecimal getGuadagnoTotale() {
        return guadagnoTotale;
    }

    public BigDecimal getCommissioneTotale() {
        return commissioneTotale;
    }

    @Override
    public String toString() {
        return String.format(
                "%s - %s %s | corse: %d | guadagno: %s | commissione: %s",
                patente,
                nome,
                cognome,
                numeroCorse,
                guadagnoTotale,
                commissioneTotale
        );
    }
}
