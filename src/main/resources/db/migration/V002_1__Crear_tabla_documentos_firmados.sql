CREATE TABLE documentos_firmados(
    id SERIAL PRIMARY KEY,
    firmaDigital TEXT NOT NULL,
    configuracion_firma_id INT NOT NULL REFERENCES configuraciones_firmas(id) ON DELETE CASCADE
);