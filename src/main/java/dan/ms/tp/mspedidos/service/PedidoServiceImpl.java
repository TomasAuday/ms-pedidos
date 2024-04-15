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

    public List<Pedido> getPedidosByClienteOrDate(String razonSocial, Instant fromDate, Instant toDate){

        if(fromDate == null) fromDate = Instant.ofEpochMilli(Long.MIN_VALUE);
        if(toDate == null) toDate = Instant.ofEpochMilli(Long.MAX_VALUE);

        if(razonSocial == null || razonSocial.trim() == ""){
            return pedidoRepository.findByFecha(fromDate, toDate);
        }

        return pedidoRepository.findByClienteFecha(razonSocial, fromDate, toDate);
    }

    public Pedido createPedido(PedidoDtoForCreation pedidoDto) throws Exception{
        Pedido p = pedidoDto.toPedido();
        double totalPedido = 0;

        Cliente client = clienteService.getCliente(pedidoDto.getCliente());
 
        if (pedidoDto.getDetallePedido() == null){
            throw new Exception("Pedido sin detalle de pedido");
        }

        List<PedidoDetalle> pedidoDetalles = new ArrayList<>();

        // initial default state 
        HistorialEstado initialState = new HistorialEstado();
        initialState.setEstado(EstadoPedido.RECIBIDO);
        initialState.setFechaEstado(Instant.now());
        String detalleEstadoInicial =  "";

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

            // TODO : IMPORTANT! price.get
            product.setPrecio(100.0);
            //

            if(product.getStockActual() < detallePedidoDto.getCantidad()){
                initialState.setEstado(EstadoPedido.SIN_STOCK);
                detalleEstadoInicial += product.getNombre() + " no tiene stock suficiente (" + product.getStockActual() + "en stock, " + detallePedidoDto.getCantidad() + "requeridos)";
            }

            if(detallePedidoDto.getDescuento() > 1 || detallePedidoDto.getDescuento() < 0){
                // TODO : Attribute Validation? Some Auto-Validation!
                throw new Exception("Producto (id: " + detallePedidoDto.getProducto() + ") con descuento invalido");
            }
            double totalDetalle = (1  - detallePedidoDto.getDescuento()) * (product.getPrecio() * detallePedidoDto.getCantidad());

            totalPedido += totalDetalle;

            PedidoDetalle pedidoDetalle = new PedidoDetalle();
            pedidoDetalle.setProducto(product);
            pedidoDetalle.setTotal(totalDetalle);
            pedidoDetalle.setCantidad(detallePedidoDto.getCantidad());
            pedidoDetalle.setDescuento(detallePedidoDto.getDescuento());

            pedidoDetalles.add(pedidoDetalle);
        }
        
        if(totalPedido > client.getMaximoCuentaCorriente()){
            initialState.setEstado(EstadoPedido.RECHAZADO);
            detalleEstadoInicial += "El maximo de cuenta de " + client.getRazonSocial() + "es menor al total de pedido $"+ Double.toString(totalPedido) +" .\n";
        }

        if(initialState.getEstado() == EstadoPedido.RECIBIDO){
            detalleEstadoInicial = "Pedido recibido!";
        }

        initialState.setDetalle(detalleEstadoInicial);

        // TODO : UserEstado
        initialState.setUserEstado(null);

        
        List<HistorialEstado> historialInicial = new ArrayList<>();
        historialInicial.add(initialState);
        
        p.setFecha(Instant.now());
        p.setEstados(historialInicial);
        p.setDetallePedido(pedidoDetalles);
        p.setCliente(client);
        p.setTotal(totalPedido);

        pedidoRepository.save(p);
        return p;
    }

    public Pedido cancelPedido(String id) throws Exception {
        Pedido pedido = getPedido(id);
        HistorialEstado ultimoEstado = pedido.getEstados().get(pedido.getEstados().size() - 1);

        EstadoPedido estadoActual = ultimoEstado.getEstado();
        if (estadoActual == EstadoPedido.RECHAZADO || estadoActual == EstadoPedido.CANCELADO
                || estadoActual == EstadoPedido.EN_DISTRIBUCION || estadoActual == EstadoPedido.ENTREGADO) {
            throw new Exception("No se puede cancelar el pedido porque ya se encuentra en un estado final.");
        }
        HistorialEstado nuevoEstado = new HistorialEstado();
        nuevoEstado.setEstado(EstadoPedido.CANCELADO);
        nuevoEstado.setFechaEstado(Instant.now());
        nuevoEstado.setDetalle("Pedido cancelado por el cliente.");
        nuevoEstado.setUserEstado(null);

        pedido.getEstados().add(nuevoEstado);

        // Guardar el pedido actualizado en la base de datos
        pedidoRepository.save(pedido);

        return pedido;
    }
}
