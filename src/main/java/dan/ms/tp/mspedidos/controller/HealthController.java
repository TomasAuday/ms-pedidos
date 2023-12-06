package dan.ms.tp.mspedidos.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/health")
public class HealthController {
    
    @GetMapping
    public ResponseEntity<Map<String,String>> health(){
        Map<String,String> res = new LinkedHashMap<>();
        res.put("app","ms-pedidos");
        res.put("status","OK");
        res.put("timestamp",Instant.now().toString());
        return ResponseEntity.ok().body(res);
    }
}
