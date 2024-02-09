package dan.ms.tp.mspedidos.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dan.ms.tp.mspedidos.dto.pedido.PedidoDtoForCreation;
import dan.ms.tp.mspedidos.modelo.Pedido;
import dan.ms.tp.mspedidos.service.PedidoService;

@RestController
@RequestMapping("api/pedido")
public class PedidoController {
    
    // @Autowired PedidoRepository repo;
    @Autowired PedidoService pedidoService;
    
    @PostMapping
    public ResponseEntity<Pedido> guardar(@RequestBody PedidoDtoForCreation pedido){
        try{
            Pedido createdPedido = pedidoService.createPedido(pedido);
            return ResponseEntity.ok().body(createdPedido);
        } catch (Exception e) {
            // TODO Ex
            return ResponseEntity.internalServerError().build();
        }
        //return ResponseEntity.ok().body(repo.save(pedido));
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> buscar(){
        // TODO : Busquedas
        return ResponseEntity.ok().body(new ArrayList<Pedido>());
        //return ResponseEntity.ok().body(repo.findAll());
    }
}
