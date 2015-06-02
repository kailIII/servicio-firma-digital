package ec.gob.senescyt;

import ec.gob.senescyt.sniese.commons.configurations.ConfiguracionSnieseBase;

import javax.validation.constraints.NotNull;

public class ServicioConfiguration extends ConfiguracionSnieseBase {

    @NotNull
    private boolean activarProduccion = false;

    public boolean isActivarProduccion() {
        return activarProduccion;
    }
}
