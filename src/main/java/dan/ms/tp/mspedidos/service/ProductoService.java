package dan.ms.tp.mspedidos.service;

import dan.ms.tp.mspedidos.dto.producto.ProductoUpdateStockDto;
import dan.ms.tp.mspedidos.modelo.Producto;

public interface ProductoService {
    Producto getProducto(int id, String token);
    Producto updateStockProducto(ProductoUpdateStockDto p);
}
    