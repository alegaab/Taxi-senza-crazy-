package it.uniroma2.dicii.bd.model.dto;

public record RegistraTassistaRequest(
        String username,
        String password,
        String patente,
        String nome,
        String cognome,
        String cc,
        String targa,
        int capacita
) {
}
