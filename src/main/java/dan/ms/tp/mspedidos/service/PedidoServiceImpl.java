package dan.ms.tp.mspedidos.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dan.ms.tp.mspedidos.dao.PedidoRepository;
import dan.ms.tp.mspedidos.dto.detallepedido.DetallePedidoDtoForCreation;
import dan.ms.tp.mspedidos.dto.pedido.PedidoDtoForCreation;
import dan.ms.tp.mspedidos.modelo.Cliente;
import dan.ms.tp.mspedidos.modelo.EstadoPedido;
import dan.ms.tp.mspedidos.modelo.HistorialEstado;
import dan.ms.tp.mspedidos.modelo.Pedido;
import dan.ms.tp.mspedidos.modelo.PedidoDetalle;
import dan.ms.tp.mspedidos.modelo.Producto;

@Service
public class PedidoServiceImpl implements PedidoService {
    @Autowired
    PedidoRepository pedidoRepository;
    @Autowired
    ProductoService productoService;
    @Autowired
    ClienteService clienteService;

    // Fix Exceptions / Exception middleware / etc
    public Pedido getPedido(String id) throws Exception{
        Optional<Pedido> pedido = pedidoRepository.findById(id);

        if(pedido.isPresent()){
            return pedido.get();
        }
        throw new Exception("Pedido no encontrado");
    }

    public List<Pedido> getPedidosByCliente(String razonSocial){
        // TODO : 
        return new ArrayList<Pedido>();
    }

    public Pedido createPedido(PedidoDtoForCreation pedidoDto) throws Exception{
        Pedido p = pedidoDto.map();

        Cliente client = clienteService.getCliente(pedidoDto.getCliente());
 
        if (pedidoDto.getDetallePedido() == null){
            throw new Exception("Pedido sin detalle de pedido");
        }

        List<PedidoDetalle> pedidoDetalles = new ArrayList<>();

        // obtener y validar productos
        for(DetallePedidoDtoForCreation detallePedidoDto : pedidoDto.getDetallePedido()){
            if(detallePedidoDto.getProducto() == null){
                throw new Exception("Pedido sin detalle de pedido");
            }
            
            // TODO : optimize fetch
            Producto product = productoService.getProducto(detallePedidoDto.getProducto());

            if(product == null){
                throw new Exception("Producto (id: " + detallePedidoDto.getProducto() + ") no encontrado." );
            }

            if(product.getStockActual() < detallePedidoDto.getCantidad()){
                // TODO : Should be EstadoPedido.SIN_STOCK
                throw new Exception("Producto (id: " + detallePedidoDto.getProducto() + ") sin stock" );
            }

            if(detallePedidoDto.getDescuento() > 1 || detallePedidoDto.getDescuento() < 0){
                // TODO : Attribute Validation? Some Auto-Validation!
                throw new Exception("Producto (id: " + detallePedidoDto.getProducto() + ") con descuento invalido");
            }
            double total = (1  - detallePedidoDto.getDescuento()) * (product.getPrecio() * detallePedidoDto.getCantidad());

            PedidoDetalle pedidoDetalle = new PedidoDetalle();
            pedidoDetalle.setProducto(product);
            pedidoDetalle.setTotal(total);
            pedidoDetalle.setCantidad(detallePedidoDto.getCantidad());
            pedidoDetalle.setDescuento(detallePedidoDto.getDescuento());

            pedidoDetalles.add(pedidoDetalle);
        }
        // TODO : Update Stocks | MS.Prod implementation missing ( or use put maybe ? [inconsistent])
        // productoService.updateStockProducto(productUpdateDto)
        
        //Producto producto = productoService.getProducto();
        // initial state
        HistorialEstado estadoInicial = new HistorialEstado();
        estadoInicial.setEstado(EstadoPedido.EN_PROCESO);
        estadoInicial.setFechaEstado(Instant.now());
        
        List<HistorialEstado> historialInicial = new ArrayList<>();
        historialInicial.add(estadoInicial);
        
        p.setFecha(Instant.now());
        p.setEstados(historialInicial);
        p.setDetallePedido(pedidoDetalles);
        p.setCliente(client);
        pedidoRepository.save(p);
        return p;
    }

    public Pedido cancelPedido(Pedido p){
        // TODO 
        return p;
    }
}
