package dan.ms.tp.mspedidos.modelo;

import lombok.Data;

@Data
public class PedidoDetalle {
    private Producto producto;
    private Integer cantidad;
    private Double descuento;
    private Double total;
}
