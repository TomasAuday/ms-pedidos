package dan.ms.tp.mspedidos.dto.detallepedido;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetallePedidoDtoForCreation {
    @NotNull
    private Integer producto;
    @NotNull
    @Range(min = 1, max  = 1000)
    private Integer cantidad;
    // CHECKME : How should descuento be applied?
    @Range(min = 0, max  = 1)
    private Double descuento;
}
