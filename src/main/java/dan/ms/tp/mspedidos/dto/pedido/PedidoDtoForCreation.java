package dan.ms.tp.mspedidos.dto.pedido;

import java.util.List;

import lombok.Data;

import dan.ms.tp.mspedidos.dto.detallepedido.DetallePedidoDtoForCreation;
import dan.ms.tp.mspedidos.modelo.Pedido;

@Data
public class PedidoDtoForCreation {
    private String usuario;
    private String observaciones;
    private int cliente;
    private List<DetallePedidoDtoForCreation> detallePedido;

    public Pedido map(){
        Pedido p = new Pedido();
        
        p.setObservaciones(observaciones);

        return p;
    }

}
