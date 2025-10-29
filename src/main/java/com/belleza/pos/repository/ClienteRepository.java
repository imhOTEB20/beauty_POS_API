package com.belleza.pos.repository;

import com.belleza.pos.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Cliente
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByNroCliente(String nroCliente);

    Optional<Cliente> findByNroDocumento(String nroDocumento);

    Optional<Cliente> findByEmail(String email);

    List<Cliente> findByActivo(Boolean activo);

    Page<Cliente> findByActivo(Boolean activo, Pageable pageable);

    Boolean existsByNroCliente(String nroCliente);

    Boolean existsByNroDocumento(String nroDocumento);

    Boolean existsByEmail(String email);

    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE %:searchTerm% OR c.apellido LIKE %:searchTerm% OR c.nroDocumento LIKE %:searchTerm%")
    Page<Cliente> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.cuentaCorrienteHabilitada = true AND c.activo = true")
    List<Cliente> findConCuentaCorriente();

    @Query("SELECT c FROM Cliente c WHERE c.cuentaCorrienteHabilitada = true AND c.saldoActual > c.limiteCredito AND c.activo = true")
    List<Cliente> findConLimiteCreditoExcedido();

    @Query("SELECT c FROM Cliente c WHERE c.cuentaCorrienteHabilitada = true AND c.saldoActual > 0 AND c.activo = true")
    List<Cliente> findConSaldoPendiente();
}
