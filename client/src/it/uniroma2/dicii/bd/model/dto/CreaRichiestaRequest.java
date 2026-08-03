package it.uniroma2.dicii.bd.model.dto;

public record CreaRichiestaRequest(
        String usernameSessione,
        String indirizzoPartenza,
        String indirizzoDestinazione
) {
}
