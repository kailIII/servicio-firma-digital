CREATE TABLE configuraciones_firmas(
    id SERIAL PRIMARY KEY,
    caminoArchivo varchar(300) NOT NULL,
    nombreUsuario VARCHAR(50) NOT NULL,
    activa BIT NOT NULL DEFAULT 1::BIT
);