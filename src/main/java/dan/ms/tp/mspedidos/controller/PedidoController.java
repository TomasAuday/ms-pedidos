package dan.ms.tp.mspedidos.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dan.ms.tp.mspedidos.dto.pedido.PedidoDtoForCreation;
import dan.ms.tp.mspedidos.modelo.Pedido;
import dan.ms.tp.mspedidos.service.PedidoService;
import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:3001")
@RequestMapping("api/pedido")
public class PedidoController {
    @Autowired PedidoService pedidoService;
    
    @PostMapping
    public ResponseEntity<Pedido> guardar(@Valid @RequestBody PedidoDtoForCreation pedido){
        try{    
            System.out.println(pedido);
            Pedido createdPedido = pedidoService.createPedido(pedido);
            return ResponseEntity.ok().body(createdPedido);
        } catch (Exception e) {
            // TODO Ex
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscar(@PathVariable String id){
        try{
            Pedido pedido = pedidoService.getPedido(id);
            return ResponseEntity.ok().body(pedido);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
        
    }

    @GetMapping("/numeroPedido/{numeroPedido}")
    public ResponseEntity<Pedido> buscar(@PathVariable Integer numeroPedido){
        try{
            Pedido pedido = pedidoService.getPedidoByNumeroPedido(numeroPedido);
            return ResponseEntity.ok().body(pedido);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> buscar(@RequestParam(required = false) String razonSocial, @RequestParam(required = false) Instant desde,@RequestParam(required = false) Instant hasta){
        return ResponseEntity.ok().body(pedidoService.getPedidosByClienteOrDate(razonSocial, desde, hasta));
    }
    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable String id) {
        try {
            Pedido pedidoCancelado = pedidoService.cancelPedido(id);
            return ResponseEntity.ok().body(pedidoCancelado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
