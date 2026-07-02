package com.innovatech.logistica.exception;

public class EnvioNotFoundException extends RuntimeException {
    public EnvioNotFoundException(Long id) {
        super("Envío no encontrado con ID: " + id);
    }

    public EnvioNotFoundException(String codigoSeguimiento) {
        super("Envío no encontrado con código: " + codigoSeguimiento);
    }
}
