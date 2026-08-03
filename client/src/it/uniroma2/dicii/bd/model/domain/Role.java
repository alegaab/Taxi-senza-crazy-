package it.uniroma2.dicii.bd.model.domain;

public enum Role {
    CLIENTE(1),
    TASSISTA(2),
    GESTORE(3),
    REGISTRAZIONE(4),
    LOGIN(5);

    private final int id;

    Role(int id) {
        this.id = id;
    }

    public static Role fromInt(int id) {
        for (Role role : values()) {
            if (role.id == id) {
                return role;
            }
        }

        return null;
    }
}
