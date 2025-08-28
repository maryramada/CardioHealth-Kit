DROP DATABASE IF EXISTS projetows;
CREATE DATABASE projetows;

USE projetows;

-- Tabela Pessoa
CREATE TABLE Pessoa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    nascimento VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telemovel INT NOT NULL,
    contactoEmergencia INT NOT NULL
);

-- Tabela FreqCardiaca
CREATE TABLE FreqCardiaca (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pessoa_id INT,                
    num FLOAT NOT NULL,             
    instante VARCHAR(100) NOT NULL,     
    FOREIGN KEY (pessoa_id) REFERENCES Pessoa(id) ON DELETE CASCADE
);

-- Tabela SatOx
CREATE TABLE SatOx (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pessoa_id INT,                
    valor FLOAT NOT NULL,             
    instante VARCHAR(100) NOT NULL,     
    FOREIGN KEY (pessoa_id) REFERENCES Pessoa(id) ON DELETE CASCADE
);

-- Tabela Alerta
CREATE TABLE Alertas (
    id INT AUTO_INCREMENT PRIMARY KEY,     
    pessoa_id INT NOT NULL, 
    tipo VARCHAR(50) NOT NULL,               
    descricao VARCHAR(255) NOT NULL, 
    valor INT NOT NULL,                 
    instante VARCHAR(100) NOT NULL,       
    FOREIGN KEY (pessoa_id) REFERENCES Pessoa(id) ON DELETE CASCADE
);

-- TRIGGER para gerar alerta de frequência cardíaca
DELIMITER //

CREATE TRIGGER trig_alerta_freqCardiaca
AFTER INSERT ON FreqCardiaca
FOR EACH ROW
BEGIN
    IF NEW.num < 60 THEN
        INSERT INTO Alertas (pessoa_id, tipo, descricao, valor, instante)
        VALUES (NEW.pessoa_id, 'Frequência Cardíaca', 'Frequência cardíaca baixa', NEW.num, NEW.instante);
    ELSEIF NEW.num > 100 THEN
        INSERT INTO Alertas (pessoa_id, tipo, descricao, valor, instante)
        VALUES (NEW.pessoa_id, 'Frequência Cardíaca', 'Frequência cardíaca alta', NEW.num, NEW.instante);
    END IF;
END;
//

DELIMITER ;

-- TRIGGER para gerar alerta de saturação de oxigênio
DELIMITER //

CREATE TRIGGER trig_alerta_satOx
AFTER INSERT ON SatOx
FOR EACH ROW
BEGIN
    IF NEW.valor < 95 THEN
        INSERT INTO Alertas (pessoa_id, tipo, descricao, valor, instante)
        VALUES (NEW.pessoa_id, 'Saturação Oxigênio', 'Saturação de oxigênio baixa', NEW.valor, NEW.instante);
    END IF;
END;
//

DELIMITER ;
