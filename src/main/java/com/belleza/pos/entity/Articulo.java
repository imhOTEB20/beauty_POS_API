package com.belleza.pos.entity;

import com.belleza.pos.entity.enums.UnidadVenta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Artículo/Producto
 */
@Entity
@Table(name = "articulos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_articulo")
    private Integer idArticulo;

    @Column(name = "codigo_barras", unique = true, nullable = false, length = 50)
    private String codigoBarras;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rubro")
    private Rubro rubro;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_venta", nullable = false)
    private UnidadVenta unidadVenta = UnidadVenta.UNIDAD;

    @Column(name = "usa_control_stock", nullable = false)
    private Boolean usaControlStock = false;

    @Column(name = "stock_actual", precision = 10, scale = 3)
    private BigDecimal stockActual = BigDecimal.ZERO;

    @Column(name = "stock_minimo", precision = 10, scale = 3)
    private BigDecimal stockMinimo = BigDecimal.ZERO;

    @Column(name = "stock_maximo", precision = 10, scale = 3)
    private BigDecimal stockMaximo = BigDecimal.ZERO;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "publicar_en_web", nullable = false)
    private Boolean publicarEnWeb = false;

    @Column(name = "en_oferta", nullable = false)
    private Boolean enOferta = false;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArticuloPrecio> precios = new HashSet<>();

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArticuloProveedor> proveedores = new HashSet<>();

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}
