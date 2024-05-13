package dan.ms.tp.mspedidos.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dan.ms.tp.mspedidos.dto.producto.ProductoUpdateStockDto;
import dan.ms.tp.mspedidos.modelo.Cliente;
import dan.ms.tp.mspedidos.modelo.Producto;

@Service
public class ProductoServiceImpl implements ProductoService {
    // TODO : Magic path
    // TODO : Ex
    String apiBaseUrl = "http://dan-gateway:3080/api/producto/";

    public Producto getProducto(int id, String token){
       
         HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization",token);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(headers);


        ResponseEntity<Producto> response = restTemplate.exchange(apiBaseUrl + id, HttpMethod.GET, entity, Producto.class);
    

        return response.getBody();
    }

    public Producto updateStockProducto(ProductoUpdateStockDto dto){
        // TODO : MS.Prod implement something like this (route and etc can change)
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Producto> response = restTemplate.postForEntity(apiBaseUrl + "/stock" , dto, Producto.class);

        return response.getBody();
    }

}
