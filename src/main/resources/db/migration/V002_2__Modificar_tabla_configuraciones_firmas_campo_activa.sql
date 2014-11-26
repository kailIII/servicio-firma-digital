ALTER TABLE configuraciones_firmas
    DROP COLUMN activa,
    ADD COLUMN activa BOOLEAN NOT NULL DEFAULT TRUE;