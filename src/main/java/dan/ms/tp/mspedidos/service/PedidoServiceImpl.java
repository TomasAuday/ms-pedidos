package dan.ms.tp.mspedidos.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dan.ms.tp.mspedidos.dao.PedidoRepository;
import dan.ms.tp.mspedidos.dto.auth.UserInfo;
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
    @Autowired
    AuthUserService authUserService;

    // Fix Exceptions / Exception middleware / etc
    public Pedido getPedido(String id) throws Exception{
        Optional<Pedido> pedido = pedidoRepository.findById(id);

        if(pedido.isPresent()){
            return pedido.get();
        }
        throw new Exception("Pedido no encontrado");
    }

    public Pedido getPedidoByNumeroPedido(Integer numeroPedido) throws Exception{
        Optional<Pedido> pedido = pedidoRepository.findOneByNumeroPedido(numeroPedido);

        if(pedido.isPresent()){
            return pedido.get();
        }
        throw new Exception("Pedido no encontrado");
    }


    public List<Pedido> getAllPedidos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> getPedidosByClienteOrDate(String razonSocial, Instant fromDate, Instant toDate){
        if(fromDate == null) fromDate = Instant.ofEpochMilli(Long.MIN_VALUE);
        if(toDate == null) toDate = Instant.ofEpochMilli(Long.MAX_VALUE);

        if(razonSocial == null || razonSocial.trim() == ""){
            return pedidoRepository.findByFecha(fromDate, toDate);
        }

        return pedidoRepository.findByClienteFecha(razonSocial, fromDate, toDate);
    }

    public Pedido createPedido(PedidoDtoForCreation pedidoDto, String token) throws Exception{
        System.out.println("[PEDIDOSERVICEIMPL][CreatePedido][INIT]");
        
        Pedido p = pedidoDto.toPedido();
        double totalPedido = 0;

        // gen numeropedido
        Integer nextNumeroPedido = 1;

        Optional<Pedido> lastPedido = pedidoRepository.findFirstByOrderByNumeroPedidoDesc();
        if(lastPedido.isPresent()){
            
            nextNumeroPedido = lastPedido.get().getNumeroPedido() + 1;
        }
        


        Cliente client = clienteService.getCliente(pedidoDto.getCliente(), token);
        System.out.println("[PEDIDOSERVICEIMPL][CreatePedido][CLIENTE]:");
        System.out.println(client.toString());

        if(Cliente.isEmpty(client)){
            // log
            System.out.println("[PEDIDOSERVICEIMPL][CreatePedido][NO HAY CLIENTE]");
            
            throw new Exception("Cliente Server Error");
        }
 
        if (pedidoDto.getDetallePedido() == null){
            System.out.println("[PEDIDOSERVICEIMPL][CreatePedido][NO HAY DETALLE DEL PEDIDO]");
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
                System.out.println("[PEDIDOSERVICEIMPL][CreatePedido][NO HAY DETALLE DEL PEDIDO DENTRO DEL FOR]");
                throw new Exception("Pedido sin detalle de pedido");
            }
            
            // TODO : optimize fetch
            Producto product = productoService.getProducto(detallePedidoDto.getProducto(), token);

            if(product == null){
                System.out.println("[PEDIDOSERVICEIMPL][CreatePedido][FOR - PRODUCTO NO ENCONTRADO]");
                
                throw new Exception("Producto (id: " + detallePedidoDto.getProducto() + ") no encontrado." );
            }


            if(product.getStockActual() < detallePedidoDto.getCantidad()){
                initialState.setEstado(EstadoPedido.SIN_STOCK);
                detalleEstadoInicial += product.getNombre() + " no tiene stock suficiente (" + product.getStockActual() + "en stock, " + detallePedidoDto.getCantidad() + "requeridos)";
            }

            if(detallePedidoDto.getDescuento() > 1 || detallePedidoDto.getDescuento() < 0){
                // TODO : Attribute Validation? Some Auto-Validation!
                throw new Exception("Producto (id: " + detallePedidoDto.getProducto() + ") con descuento invalido");
            }
            double totalDetalle = (1  - detallePedidoDto.getDescuento()) * (product.getPrecioVenta() * detallePedidoDto.getCantidad());

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
        
        p.setNumeroPedido(nextNumeroPedido);
        p.setFecha(Instant.now());
        p.setEstados(historialInicial);
        p.setDetallePedido(pedidoDetalles);
        p.setCliente(client);
        p.setTotal(totalPedido);

        UserInfo authUserInfo = authUserService.getCurrentUser();
        if(authUserInfo != null && !authUserInfo.getUserName().trim().isEmpty()){
            p.setUser(authUserInfo.getUserName());
        }

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
        nuevoEstado.setUserEstado(null);
        nuevoEstado.setDetalle("Pedido cancelado.");

        UserInfo authUserInfo = authUserService.getCurrentUser();
        setHistorialEstadoDetalleCanceladoBasedOnUser(nuevoEstado, authUserInfo);

        pedido.getEstados().add(nuevoEstado);

        // Guardar el pedido actualizado en la base de datos
        pedidoRepository.save(pedido);

        return pedido;
    }

    private void setHistorialEstadoDetalleCanceladoBasedOnUser(HistorialEstado estado, UserInfo authUserInfo){
        if(estado == null || authUserInfo == null || authUserInfo.getTipoUsuario().trim().isEmpty()){
            return;
        }
        try{
            if(authUserInfo.getTipoUsuario() == "ADMIN"){
                estado.setDetalle("Pedido cancelado por un administrador.");
                return;
            }
            else if(authUserInfo.getTipoUsuario() == "CLIENTE"){
                estado.setDetalle("Pedido cancelado por el cliente.");
            }
        } catch (Exception exception){
            return;
        }
        
    }
}
