package it.uniroma2.dicii.bd.model.dto;

public record RegistraClienteRequest(
        String username,
        String password,
        String nome,
        String cognome,
        String telefono,
        String cc
) {
}
