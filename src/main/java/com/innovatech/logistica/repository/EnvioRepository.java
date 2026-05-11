package com.innovatech.logistica.repository;

import com.innovatech.logistica.model.entity.Envio;
import com.innovatech.logistica.model.enums.EstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {

    Optional<Envio> findByPedidoId(Long pedidoId);

    Optional<Envio> findByCodigoSeguimiento(String codigoSeguimiento);

    List<Envio> findByUsuarioId(Long usuarioId);

    List<Envio> findByEstado(EstadoEnvio estado);

    boolean existsByPedidoId(Long pedidoId);
}
