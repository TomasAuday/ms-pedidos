package dan.ms.tp.mspedidos.dto.pedido;

import java.util.List;

import lombok.Data;

import dan.ms.tp.mspedidos.dto.detallepedido.DetallePedidoDtoForCreation;
import dan.ms.tp.mspedidos.modelo.Pedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
public class PedidoDtoForCreation {
    private Integer numeroPedido;

    @NotEmpty
    private String usuario;
    private String observaciones;
    @NotNull
    private Integer cliente;
    @NotEmpty(message = "El pedido debe contener productos")
    @Valid
    private List<DetallePedidoDtoForCreation> detallePedido;

    public Pedido toPedido(){
        Pedido p = new Pedido();
        
        p.setObservaciones(observaciones);

        return p;
    }

}
