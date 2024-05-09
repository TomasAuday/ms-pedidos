package dan.ms.tp.mspedidos.dto.auth;

import lombok.Data;

@Data
public class UserInfo {
    private String userName;
    private Integer idTipoUsuario;
    private String TipoUsuario;

    public UserInfo() {
        super();
    }
}
