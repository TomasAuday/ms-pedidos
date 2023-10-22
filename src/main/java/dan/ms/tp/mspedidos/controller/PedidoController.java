package dan.ms.tp.mspedidos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dan.ms.tp.mspedidos.dao.PedidoRepository;
import dan.ms.tp.mspedidos.modelo.Pedido;

@RestController
@RequestMapping("api/pedido")
public class PedidoController {
    
    @Autowired PedidoRepository repo;

    @PostMapping
    public ResponseEntity<Pedido> guardar(@RequestBody Pedido pedido){
        return ResponseEntity.ok().body(repo.save(pedido));
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> buscar(){
        return ResponseEntity.ok().body(repo.findAll());
    }
}
