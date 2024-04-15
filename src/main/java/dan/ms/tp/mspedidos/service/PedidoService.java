package dan.ms.tp.mspedidos.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import dan.ms.tp.mspedidos.dto.pedido.PedidoDtoForCreation;
import dan.ms.tp.mspedidos.modelo.Pedido;

@Service
public interface PedidoService {

    Pedido getPedido(String id) throws Exception;

    List<Pedido> getPedidosByClienteOrDate(String razonSocial, Instant fromDate, Instant toDate);
           
    Pedido createPedido(PedidoDtoForCreation p) throws Exception;

    Pedido cancelPedido(String id) throws Exception;

}
