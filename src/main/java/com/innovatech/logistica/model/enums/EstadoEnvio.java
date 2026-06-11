package com.innovatech.logistica.model.enums;

public enum EstadoEnvio {
    PENDIENTE,          // Pedido pagado, aún no procesado
    PREPARANDO,         // En bodega, preparando el paquete
    DESPACHADO,         // Salió del almacén
    EN_TRANSITO,        // En camino al destino
    EN_REPARTO,         // Con el repartidor, última milla
    ENTREGADO,          // Confirmación de entrega exitosa
    FALLIDO,            // Intento de entrega fallido
    DEVUELTO            // Paquete devuelto al origen
}
