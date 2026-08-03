DROP SCHEMA IF EXISTS taxi;
CREATE SCHEMA taxi
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE taxi;

SET GLOBAL event_scheduler = ON;

-- Tabelle

CREATE TABLE Utenti (
    username VARCHAR(50) NOT NULL,
    password CHAR(32) NOT NULL,
    ruolo ENUM('tassista', 'cliente', 'gestore') NOT NULL,
    PRIMARY KEY (username)
) ENGINE = InnoDB;

CREATE TABLE Cliente (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    nome VARCHAR(50) NOT NULL,
    cognome VARCHAR(50) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    CC VARCHAR(19) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_cliente_username (username),
    CONSTRAINT fk_cliente_utente
        FOREIGN KEY (username)
        REFERENCES Utenti(username)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE Veicolo (
    targa VARCHAR(10) NOT NULL,
    capacita TINYINT UNSIGNED NOT NULL,
    PRIMARY KEY (targa)
) ENGINE = InnoDB;

CREATE TABLE Tassista (
    patente VARCHAR(15) NOT NULL,
    username VARCHAR(50) NOT NULL,
    nome VARCHAR(50) NOT NULL,
    cognome VARCHAR(50) NOT NULL,
    CC VARCHAR(19) NOT NULL,
    targa_veicolo VARCHAR(10) NOT NULL,
    PRIMARY KEY (patente),
    UNIQUE KEY uq_tassista_username (username),
    UNIQUE KEY uq_tassista_targa_veicolo (targa_veicolo),
    CONSTRAINT fk_tassista_utente
        FOREIGN KEY (username)
        REFERENCES Utenti(username)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_tassista_veicolo
        FOREIGN KEY (targa_veicolo)
        REFERENCES Veicolo(targa)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE Richiesta (
    codice INT NOT NULL AUTO_INCREMENT,
    id_cliente INT NOT NULL,
    indPart VARCHAR(200) NOT NULL,
    indDest VARCHAR(200) NOT NULL,
    tsRichiesta DATETIME NOT NULL,
    stato ENUM('attiva', 'scaduta') NOT NULL DEFAULT 'attiva',
    PRIMARY KEY (codice),
    CONSTRAINT fk_richiesta_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES Cliente(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE Corsa (
    codice_richiesta INT NOT NULL,
    patente_tassista VARCHAR(15) NOT NULL,
    tsAccettazione DATETIME NOT NULL,
    importo DECIMAL(8,2) NULL,
    durata_secondi INT UNSIGNED NULL,
    riscossa BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (codice_richiesta),
    CONSTRAINT fk_corsa_richiesta
        FOREIGN KEY (codice_richiesta)
        REFERENCES Richiesta(codice)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_corsa_tassista
        FOREIGN KEY (patente_tassista)
        REFERENCES Tassista(patente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE = InnoDB;

-- Indici

CREATE INDEX idx_richiesta_stato_ts
ON Richiesta(stato, tsRichiesta);

-- Viste

CREATE OR REPLACE VIEW vw_richieste_attive AS
SELECT
    r.codice,
    r.indPart,
    r.indDest,
    r.tsRichiesta
FROM Richiesta r
WHERE r.stato = 'attiva'
  AND r.tsRichiesta > CURRENT_TIMESTAMP - INTERVAL 2 MINUTE
  AND NOT EXISTS (
      SELECT 1
      FROM Corsa c
      WHERE c.codice_richiesta = r.codice
  );

CREATE OR REPLACE VIEW vw_report_tassisti AS
SELECT
    t.patente,
    t.nome,
    t.cognome,
    COUNT(c.codice_richiesta) AS numero_corse,
    COALESCE(SUM(c.importo), 0) AS guadagno_totale,
    COALESCE(SUM(c.importo * 0.03), 0) AS commissione_totale
FROM Tassista t
LEFT JOIN Corsa c
    ON c.patente_tassista = t.patente
GROUP BY
    t.patente,
    t.nome,
    t.cognome;

-- Stored procedures

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_login $$
CREATE PROCEDURE sp_login(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(50),
    OUT p_ruolo INT
)
SQL SECURITY DEFINER
BEGIN
    DECLARE v_ruolo VARCHAR(20) DEFAULT NULL;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET v_ruolo = NULL;

    SELECT ruolo
    INTO v_ruolo
    FROM Utenti
    WHERE username = p_username
      AND password = MD5(p_password);

    IF v_ruolo IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Username o password non validi';
    END IF;

    SET p_ruolo = CASE v_ruolo
        WHEN 'cliente' THEN 1
        WHEN 'tassista' THEN 2
        WHEN 'gestore' THEN 3
    END;
END $$

DROP PROCEDURE IF EXISTS sp_registra_cliente $$
CREATE PROCEDURE sp_registra_cliente(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(50),
    IN p_nome VARCHAR(50),
    IN p_cognome VARCHAR(50),
    IN p_telefono VARCHAR(20),
    IN p_CC VARCHAR(19),
    OUT p_id_cliente INT
)
SQL SECURITY DEFINER
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;

    INSERT INTO Utenti(username, password, ruolo)
    VALUES (p_username, MD5(p_password), 'cliente');

    INSERT INTO Cliente(username, nome, cognome, telefono, CC)
    VALUES (p_username, p_nome, p_cognome, p_telefono, p_CC);

    SET p_id_cliente = LAST_INSERT_ID();

    COMMIT;
END $$

DROP PROCEDURE IF EXISTS sp_registra_tassista $$
CREATE PROCEDURE sp_registra_tassista(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(50),
    IN p_patente VARCHAR(15),
    IN p_nome VARCHAR(50),
    IN p_cognome VARCHAR(50),
    IN p_CC VARCHAR(19),
    IN p_targa VARCHAR(10),
    IN p_capacita INT
)
SQL SECURITY DEFINER
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;

    IF p_capacita IS NULL OR p_capacita <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La capacità del veicolo deve essere maggiore di zero';
    END IF;

    INSERT INTO Utenti(username, password, ruolo)
    VALUES (p_username, MD5(p_password), 'tassista');

    INSERT INTO Veicolo(targa, capacita)
    VALUES (p_targa, p_capacita);

    INSERT INTO Tassista(patente, username, nome, cognome, CC, targa_veicolo)
    VALUES (p_patente, p_username, p_nome, p_cognome, p_CC, p_targa);

    COMMIT;
END $$

DROP PROCEDURE IF EXISTS sp_crea_richiesta $$
CREATE PROCEDURE sp_crea_richiesta(
    IN p_username_sessione VARCHAR(50),
    IN p_indPart VARCHAR(200),
    IN p_indDest VARCHAR(200),
    OUT p_codice_richiesta INT
)
SQL SECURITY DEFINER
BEGIN
    DECLARE v_id_cliente INT;
    DECLARE v_ts_richiesta DATETIME;
    DECLARE v_ts_scadenza DATETIME;
    DECLARE v_event_name VARCHAR(64);
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    BEGIN
        DECLARE CONTINUE HANDLER FOR NOT FOUND
            SET v_not_found = TRUE;

        DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

        SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
        START TRANSACTION;

        SET v_not_found = FALSE;

        SELECT c.id
        INTO v_id_cliente
        FROM Cliente c
        JOIN Utenti u
            ON u.username = c.username
        WHERE c.username = p_username_sessione
          AND u.ruolo = 'cliente';

        IF v_not_found THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cliente non valido';
        END IF;

        SET v_ts_richiesta = CURRENT_TIMESTAMP;
        SET v_ts_scadenza = v_ts_richiesta + INTERVAL 2 MINUTE;

        INSERT INTO Richiesta(indPart, indDest, tsRichiesta, stato, id_cliente)
        VALUES (p_indPart, p_indDest, v_ts_richiesta, 'attiva', v_id_cliente);

        SET p_codice_richiesta = LAST_INSERT_ID();

        COMMIT;
    END;

    SET v_event_name = CONCAT('ev_scadenza_richiesta_', p_codice_richiesta);

    SET @sql_evento = CONCAT(
        'CREATE EVENT ', v_event_name, ' ',
        'ON SCHEDULE AT ''', DATE_FORMAT(v_ts_scadenza, '%Y-%m-%d %H:%i:%s'), ''' ',
        'ON COMPLETION NOT PRESERVE ',
        'DO UPDATE Richiesta r ',
        'SET r.stato = ''scaduta'' ',
        'WHERE r.codice = ', p_codice_richiesta, ' ',
        'AND r.stato = ''attiva'' ',
        'AND NOT EXISTS (',
            'SELECT 1 FROM Corsa c ',
            'WHERE c.codice_richiesta = r.codice',
        ')'
    );

    PREPARE stmt_evento FROM @sql_evento;
    EXECUTE stmt_evento;
    DEALLOCATE PREPARE stmt_evento;
END $$

DROP PROCEDURE IF EXISTS sp_lista_richieste_attive $$
CREATE PROCEDURE sp_lista_richieste_attive()
SQL SECURITY DEFINER
BEGIN
    SELECT
        codice,
        indPart,
        indDest,
        tsRichiesta
    FROM vw_richieste_attive
    ORDER BY tsRichiesta;
END $$

DROP PROCEDURE IF EXISTS sp_accetta_richiesta $$
CREATE PROCEDURE sp_accetta_richiesta(
    IN p_username_sessione VARCHAR(50),
    IN p_codice_richiesta INT
)
SQL SECURITY DEFINER
BEGIN
    DECLARE v_patente VARCHAR(15);
    DECLARE v_stato VARCHAR(20);
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET v_not_found = TRUE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;

    SELECT t.patente
    INTO v_patente
    FROM Tassista t
    JOIN Utenti u
        ON u.username = t.username
    WHERE t.username = p_username_sessione
      AND u.ruolo = 'tassista';

    IF v_not_found THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tassista non valido';
    END IF;

    SET v_not_found = FALSE;

    SELECT r.stato
    INTO v_stato
    FROM Richiesta r
    WHERE r.codice = p_codice_richiesta
    FOR UPDATE;

    IF v_not_found THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Richiesta non esistente';
    END IF;

    IF v_stato = 'scaduta' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La richiesta è scaduta';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM Corsa c
        WHERE c.codice_richiesta = p_codice_richiesta
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La richiesta è già stata accettata';
    END IF;

    INSERT INTO Corsa(codice_richiesta, patente_tassista, tsAccettazione, importo, durata_secondi, riscossa)
    VALUES (p_codice_richiesta, v_patente, CURRENT_TIMESTAMP, NULL, NULL, FALSE);

    COMMIT;
END $$

DROP PROCEDURE IF EXISTS sp_registra_fine_corsa $$
CREATE PROCEDURE sp_registra_fine_corsa(
    IN p_username_sessione VARCHAR(50),
    IN p_codice_richiesta INT,
    IN p_importo DECIMAL(8,2)
)
SQL SECURITY DEFINER
BEGIN
    DECLARE v_patente VARCHAR(15);
    DECLARE v_patente_corsa VARCHAR(15);
    DECLARE v_ts_accettazione DATETIME;
    DECLARE v_durata_secondi INT UNSIGNED;
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET v_not_found = TRUE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;

    IF p_importo IS NULL OR p_importo <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Importo della corsa non valido';
    END IF;

    SELECT t.patente
    INTO v_patente
    FROM Tassista t
    JOIN Utenti u
        ON u.username = t.username
    WHERE t.username = p_username_sessione
      AND u.ruolo = 'tassista';

    IF v_not_found THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tassista non valido';
    END IF;

    SET v_not_found = FALSE;

    SELECT c.patente_tassista, c.tsAccettazione, c.durata_secondi
    INTO v_patente_corsa, v_ts_accettazione, v_durata_secondi
    FROM Corsa c
    WHERE c.codice_richiesta = p_codice_richiesta
    FOR UPDATE;

    IF v_not_found THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Corsa non esistente';
    END IF;

    IF v_patente_corsa <> v_patente THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Corsa non associata al tassista autenticato';
    END IF;

    IF v_durata_secondi IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Corsa già conclusa';
    END IF;

    UPDATE Corsa
    SET importo = p_importo,
        durata_secondi = TIMESTAMPDIFF(SECOND, v_ts_accettazione, CURRENT_TIMESTAMP)
    WHERE codice_richiesta = p_codice_richiesta;

    COMMIT;
END $$

DROP PROCEDURE IF EXISTS sp_genera_report_tassisti $$
CREATE PROCEDURE sp_genera_report_tassisti()
SQL SECURITY DEFINER
BEGIN
    SELECT
        patente,
        nome,
        cognome,
        numero_corse,
        guadagno_totale,
        commissione_totale
    FROM vw_report_tassisti
    ORDER BY cognome, nome, patente;
END $$

DROP PROCEDURE IF EXISTS sp_segna_riscossione $$
CREATE PROCEDURE sp_segna_riscossione(
    IN p_codice_richiesta INT
)
SQL SECURITY DEFINER
BEGIN
    UPDATE Corsa
    SET riscossa = TRUE
    WHERE codice_richiesta = p_codice_richiesta
      AND importo IS NOT NULL
      AND durata_secondi IS NOT NULL
      AND riscossa = FALSE;

    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Corsa non riscuotibile';
    END IF;
END $$

DELIMITER ;

-- Utente applicativo gestore iniziale
-- La password iniziale è 'gestore' e viene salvata come MD5.

INSERT INTO Utenti(username, password, ruolo)
VALUES ('gestore', MD5('gestore'), 'gestore');

-- Utenti DBMS e privilegi
-- Password dimostrative: da cambiare in un ambiente reale.

DROP USER IF EXISTS 'registrazione'@'localhost';
DROP USER IF EXISTS 'login'@'localhost';
DROP USER IF EXISTS 'cliente'@'localhost';
DROP USER IF EXISTS 'tassista'@'localhost';
DROP USER IF EXISTS 'gestore'@'localhost';

CREATE USER 'registrazione'@'localhost' IDENTIFIED BY 'registrazione';
CREATE USER 'login'@'localhost' IDENTIFIED BY 'login';
CREATE USER 'cliente'@'localhost' IDENTIFIED BY 'cliente';
CREATE USER 'tassista'@'localhost' IDENTIFIED BY 'tassista';
CREATE USER 'gestore'@'localhost' IDENTIFIED BY 'gestore';

GRANT EXECUTE ON PROCEDURE taxi.sp_registra_cliente TO 'registrazione'@'localhost';
GRANT EXECUTE ON PROCEDURE taxi.sp_registra_tassista TO 'registrazione'@'localhost';

GRANT EXECUTE ON PROCEDURE taxi.sp_login TO 'login'@'localhost';

GRANT EXECUTE ON PROCEDURE taxi.sp_crea_richiesta TO 'cliente'@'localhost';

GRANT EXECUTE ON PROCEDURE taxi.sp_lista_richieste_attive TO 'tassista'@'localhost';
GRANT EXECUTE ON PROCEDURE taxi.sp_accetta_richiesta TO 'tassista'@'localhost';
GRANT EXECUTE ON PROCEDURE taxi.sp_registra_fine_corsa TO 'tassista'@'localhost';

GRANT EXECUTE ON PROCEDURE taxi.sp_genera_report_tassisti TO 'gestore'@'localhost';
GRANT EXECUTE ON PROCEDURE taxi.sp_segna_riscossione TO 'gestore'@'localhost';
