package dan.ms.tp.mspedidos.modelo;

import lombok.Data;

@Data
public class Producto {
    private Integer id;
    private String nombre;
    private Double precioVenta;
    private Integer stockActual;
}
