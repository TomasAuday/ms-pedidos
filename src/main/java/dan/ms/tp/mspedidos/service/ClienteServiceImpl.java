package dan.ms.tp.mspedidos.service;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import dan.ms.tp.mspedidos.modelo.Cliente;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

@Service
public class ClienteServiceImpl implements ClienteService {

    // @Autowired
    // private DiscoveryClient discoveryClient;
    // @Autowired
    // private CircuitBreakerFactory circuitBreakerFactory;

    public Cliente getCliente(int id, String token){
        
        // CircuitBreaker circuitBreaker = circuitBreakerFactory.create("getClienteCircuitBreaker");

        String endpointPath = getApiUrl();
        
        HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization",token);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(headers);

       

        ResponseEntity<Cliente> response = restTemplate.exchange(endpointPath + "/api/cliente/" + id, HttpMethod.GET, entity, Cliente.class);
        return response.getBody();
        
        // return circuitBreaker.run(() -> restTemplate.getForEntity(endpointPath + "/api/cliente/" + id , Cliente.class).getBody(), throwable -> getClienteFallback(throwable));
    }


    private String getApiUrl(){
        
        return "http://dan-gateway:3080";
        // List<ServiceInstance> instances = discoveryClient.getInstances("MS-Usuarios");

        // if (instances.isEmpty()) {
        //     throw new RuntimeException("No instance found");
        // }

        // ServiceInstance serviceInstance = instances.get(0);

        // return serviceInstance.getUri().toString();
        
    }

//     public Cliente getClienteFallback(Throwable throwable) {
//         // can be cached? other source? we will know in the next episode
//         Cliente emptyCliente = new Cliente();
//         emptyCliente.setId(-1);
//         return emptyCliente;
//     }

// //https://github.com/eugenp/tutorials/issues/12593
// //https://www.baeldung.com/spring-cloud-circuit-breaker
//     @Bean
//     public CircuitBreakerConfig circuitBreakerConfig() {        return CircuitBreakerConfig.custom()
//                 .failureRateThreshold(50)
//                 .waitDurationInOpenState(Duration.ofMillis(3000))
//                 .permittedNumberOfCallsInHalfOpenState(2)
//                 .build();
//     }
    
}
