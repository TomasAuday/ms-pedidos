package dan.ms.tp.mspedidos.dto.pedido;

import java.util.List;

import lombok.Data;

import dan.ms.tp.mspedidos.dto.detallepedido.DetallePedidoDtoForCreation;
import dan.ms.tp.mspedidos.modelo.Pedido;

@Data
public class PedidoDtoForCreation {
    private Integer numeroPedido;
    private String usuario;
    private String observaciones;
    private Integer cliente;
    private List<DetallePedidoDtoForCreation> detallePedido;

    public Pedido toPedido(){
        Pedido p = new Pedido();
        
        p.setObservaciones(observaciones);

        return p;
    }

}
