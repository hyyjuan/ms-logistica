package com.innovatech.logistica.model.entity;

import com.innovatech.logistica.model.enums.EstadoEnvio;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "envios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_seguimiento", unique = true, nullable = false, length = 20)
    private String codigoSeguimiento;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoEnvio estado;

    // Datos del destinatario
    @Column(name = "nombre_destinatario", nullable = false)
    private String nombreDestinatario;

    @Column(name = "direccion_destino", nullable = false)
    private String direccionDestino;

    @Column(name = "ciudad_destino", nullable = false)
    private String ciudadDestino;

    @Column(name = "region_destino")
    private String regionDestino;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    // Observaciones del repartidor o sistema
    @Column(name = "observaciones", length = 500)
    private String observaciones;

    // Fechas clave del ciclo de vida
    @Column(name = "fecha_estimada_entrega")
    private LocalDateTime fechaEstimadaEntrega;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
