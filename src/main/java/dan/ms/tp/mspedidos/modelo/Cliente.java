package dan.ms.tp.mspedidos.modelo;

import lombok.Data;

@Data
public class Cliente {
    private Integer id;
    private String razonSocial;
    private String cuit;
    private Integer deuda;
    private String correoElectronico;
    private Double maximoCuentaCorriente;


    public static boolean isEmpty(Cliente c){
        return c.id <= -1;
    }

}
