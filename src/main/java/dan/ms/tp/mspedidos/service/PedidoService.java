package dan.ms.tp.mspedidos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dan.ms.tp.mspedidos.dto.pedido.PedidoDtoForCreation;
import dan.ms.tp.mspedidos.modelo.Pedido;

@Service
public interface PedidoService {

    Pedido getPedido(String id) throws Exception;

    List<Pedido> getPedidosByCliente(String razonSocial);
           
    Pedido createPedido(PedidoDtoForCreation p) throws Exception;

    Pedido cancelPedido(Pedido p);

}