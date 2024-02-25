package dan.ms.tp.mspedidos.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dan.ms.tp.mspedidos.modelo.Cliente;

@Service
public class ClienteServiceImpl implements ClienteService {
    // TODO : Magic path
    // TODO : Ex
    String apiBaseUrl = "http://localhost:8080/api/cliente/";

    public Cliente getCliente(int id){
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Cliente> response = restTemplate.getForEntity(apiBaseUrl + id , Cliente.class);

        return response.getBody();
    }
}
