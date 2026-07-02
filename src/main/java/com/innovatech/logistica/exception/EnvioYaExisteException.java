package com.innovatech.logistica.exception;

public class EnvioYaExisteException extends RuntimeException {
    public EnvioYaExisteException(Long pedidoId) {
        super("Ya existe un envío registrado para el pedido ID: " + pedidoId);
    }
}
