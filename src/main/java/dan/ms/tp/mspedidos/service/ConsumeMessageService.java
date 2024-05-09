package dan.ms.tp.mspedidos.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dan.ms.tp.mspedidos.dao.PedidoRepository;
import dan.ms.tp.mspedidos.dto.mensajeProcesado.MensajeProcesadoDto;
import dan.ms.tp.mspedidos.dto.pedido.PagoDtoForDecision;
import dan.ms.tp.mspedidos.modelo.EstadoPedido;
import dan.ms.tp.mspedidos.modelo.HistorialEstado;
import dan.ms.tp.mspedidos.modelo.Pedido;

@Service
public class ConsumeMessageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteServiceImpl clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "pedidos")
    public void consumeMessage(String pagoJson) throws JsonProcessingException {
        
        System.out.println("Mensaje recibido de la cola de pedidos: " + pagoJson);
        PagoDtoForDecision pago = null;
        
        try {
            Thread.sleep(5000);
            pago = objectMapper.readValue(pagoJson, PagoDtoForDecision.class);
            String correoElectronico = processPedido(pago);
            
            String successMessage = objectMapper.writeValueAsString(new MensajeProcesadoDto(pago.getIdPedido(), correoElectronico, pago.getDecision()));
            rabbitTemplate.convertAndSend("respuesta.pedidos", successMessage);

            System.out.println("Mensaje de confirmación enviado a RabbitMQ: " + successMessage);
        } catch (Exception e) {
            if (pago != null) {
                
                // Convertir el objeto MensajeProcesadoDto a JSON
                String errorMessage = objectMapper.writeValueAsString(new MensajeProcesadoDto(pago.getIdPedido(), 
                clienteService.getCliente(pago.getIdUsuario()).getCorreoElectronico(), "Ya se encontraba en un estado final"));
                // Enviar el JSON a través de RabbitMQ
                rabbitTemplate.convertAndSend("respuesta.pedidos", errorMessage);
    
                // Agregar un mensaje de registro para verificar si se envió el mensaje
                System.out.println("Mensaje de error enviado a RabbitMQ: " + errorMessage);
            } else {
                e.printStackTrace();
            }
        }
    }

    private String processPedido(PagoDtoForDecision pago) throws Exception {
        Pedido pedido = getPedido(pago.getIdPedido());
        HistorialEstado ultimoEstado = pedido.getEstados().get(pedido.getEstados().size() - 1);

        EstadoPedido estadoActual = ultimoEstado.getEstado();
        if (estadoActual == EstadoPedido.RECHAZADO || estadoActual == EstadoPedido.CANCELADO || estadoActual == EstadoPedido.PAGO
                || estadoActual == EstadoPedido.EN_DISTRIBUCION || estadoActual == EstadoPedido.ENTREGADO) {
            throw new Exception("No se puede cancelar el pedido porque ya se encuentra en un estado final.");
        }

        HistorialEstado nuevoEstado = new HistorialEstado();
        if ("Cancelar".equals(pago.getDecision())) {
            nuevoEstado.setEstado(EstadoPedido.CANCELADO);
            nuevoEstado.setDetalle("Pedido cancelado por el admin.");
        } else {
            nuevoEstado.setEstado(EstadoPedido.PAGO);
            nuevoEstado.setDetalle("Pedido aceptado por el admin.");
        }
        nuevoEstado.setFechaEstado(Instant.now());
        nuevoEstado.setUserEstado(String.valueOf(pago.getIdUsuario()));

        pedido.getEstados().add(nuevoEstado);
        
        // Guardar el pedido actualizado en la base de datos
        pedidoRepository.save(pedido);

        return pedido.getCliente().getCorreoElectronico();
    }

    public Pedido getPedido(String id) throws Exception {
        Optional<Pedido> pedido = pedidoRepository.findById(id);

        if (pedido.isPresent()) {
            return pedido.get();
        }
        throw new Exception("Pedido no encontrado");
    }
}
