package dan.ms.tp.mspedidos.dto.detallepedido;

import lombok.Data;

@Data
public class DetallePedidoDtoForCreation {
    private Integer producto;
    private Integer cantidad;
    // CHECKME : How should descuento be applied?
    private Double descuento;
}
