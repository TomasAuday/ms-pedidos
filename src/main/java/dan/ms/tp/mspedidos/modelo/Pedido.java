package dan.ms.tp.mspedidos.modelo;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "dan_pedidos")
public class Pedido {

    @Id
    private String id;
    private Instant fecha;
    private Integer numeroPedido;
    private String user;
    private String observaciones;
    private Cliente cliente;
    private List<PedidoDetalle> detallePedido;
    private List<HistorialEstado> estados;
    private Double total;
}
