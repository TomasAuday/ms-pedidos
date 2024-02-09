package dan.ms.tp.mspedidos.dto.producto;


import lombok.Data;


@Data
public class ProductoUpdateStockDto {
    private int id;
    private int newStock;
}
