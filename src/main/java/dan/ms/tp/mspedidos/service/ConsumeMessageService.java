package dan.ms.tp.mspedidos.service;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dan.ms.tp.mspedidos.dao.PedidoRepository;
import dan.ms.tp.mspedidos.dto.mensajeprocesado.MensajeProcesadoDto;
import dan.ms.tp.mspedidos.dto.pedido.PagoDtoForDecision;
import dan.ms.tp.mspedidos.modelo.EstadoPedido;
import dan.ms.tp.mspedidos.modelo.HistorialEstado;
import dan.ms.tp.mspedidos.modelo.Pedido;

@Service

public class ConsumeMessageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    PedidoRepository pedidoRepository;
    @Autowired
    ObjectMapper objectMapper;

    @RabbitListener(queues = "pedidos")
    public void consumeMessage(String pagoJson) throws JsonProcessingException {
        
        System.out.println("Hola, mundo!");
        try {
            PagoDtoForDecision pago = objectMapper.readValue(pagoJson, PagoDtoForDecision.class);
            // Procesar el pedido
            //String email = processPedido(pago);
           // Enviar un solo mensaje para el procesamiento completo
            
        } catch (Exception e) {
            // Convertir el objeto MensajeProcesadoDto a JSON
            String errorMessage = objectMapper.writeValueAsString(new MensajeProcesadoDto("ss", "a", "aa"));
            // Enviar el JSON a través de RabbitMQ
            rabbitTemplate.convertAndSend("exchange.pedidos", "respuesta.correo", errorMessage);
        }
        
        // Convertir el objeto MensajeProcesadoDto a JSON
String successMessage = objectMapper.writeValueAsString(new MensajeProcesadoDto("hola", "Hola", "hola"));
// Enviar el JSON a través de RabbitMQ
rabbitTemplate.convertAndSend("exchange.pedidos", "respuesta.correo", successMessage);

// Agregar un mensaje de registro para verificar si se envió el mensaje
System.out.println("Mensaje enviado a RabbitMQ: " + successMessage);
        System.out.println("chau, mundo!");
    }



    private String processPedido(PagoDtoForDecision pago) throws Exception {
        Pedido pedido = getPedido(pago.getNumeroPedido());
        HistorialEstado ultimoEstado = pedido.getEstados().get(pedido.getEstados().size() - 1);

        EstadoPedido estadoActual = ultimoEstado.getEstado();
        if (estadoActual == EstadoPedido.RECHAZADO || estadoActual == EstadoPedido.CANCELADO
                || estadoActual == EstadoPedido.EN_DISTRIBUCION || estadoActual == EstadoPedido.ENTREGADO) {
            throw new Exception("No se puede cancelar el pedido porque ya se encuentra en un estado final.");
        }
        HistorialEstado nuevoEstado = new HistorialEstado();
        if(pago.getDecision()=="Cancelar"){
            nuevoEstado.setEstado(EstadoPedido.CANCELADO);
            nuevoEstado.setDetalle("Pedido cancelado por el admin.");
        }else{
            nuevoEstado.setEstado(EstadoPedido.PAGO);
        }
        nuevoEstado.setFechaEstado(Instant.now());
        nuevoEstado.setDetalle("Pedido aceptado por el admin.");
        nuevoEstado.setUserEstado(String.valueOf(pago.getIdUsuario()));

        pedido.getEstados().add(nuevoEstado);

        // Guardar el pedido actualizado en la base de datos
        pedidoRepository.save(pedido);

        return pedido.getCliente().getCorreoElectronico();
    }

    public Pedido getPedido(String id) throws Exception{
        Optional<Pedido> pedido = pedidoRepository.findById(id);

        if(pedido.isPresent()){
            return pedido.get();
        }
        throw new Exception("Pedido no encontrado");
    }
}