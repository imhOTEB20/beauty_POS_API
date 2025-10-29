package com.belleza.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad Lista de Precios
 */
@Entity
@Table(name = "listas_precios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ListaPrecio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lista")
    private Integer idLista;

    @Column(name = "nombre", unique = true, nullable = false, length = 50)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "es_predeterminada", nullable = false)
    private Boolean esPredeterminada = false;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}
