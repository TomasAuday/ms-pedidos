package dan.ms.tp.mspedidos.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dan.ms.tp.mspedidos.dto.producto.ProductoUpdateStockDto;
import dan.ms.tp.mspedidos.modelo.Producto;

@Service
public class ProductoServiceImpl implements ProductoService {
    // TODO : Magic path
    // TODO : Ex
    String apiBaseUrl = "http://localhost:3000/producto/";

    public Producto getProducto(int id){
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Producto> response = restTemplate.getForEntity(apiBaseUrl + id , Producto.class);

        return response.getBody();
    }

    public Producto updateStockProducto(ProductoUpdateStockDto dto){
        // TODO : MS.Prod implement something like this (route and etc can change)
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Producto> response = restTemplate.postForEntity(apiBaseUrl + "/stock" , dto, Producto.class);

        return response.getBody();
    }

}
