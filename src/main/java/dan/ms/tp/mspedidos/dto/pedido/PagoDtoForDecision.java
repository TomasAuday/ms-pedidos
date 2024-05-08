package dan.ms.tp.mspedidos.dto.pedido;



public class PagoDtoForDecision {
    private String idPedido;
    private Integer idUsuario;
    private String decision;

    public PagoDtoForDecision() {
        // Constructor vacío necesario para deserialización por Spring
    }

    public PagoDtoForDecision(String idPedido, Integer idUsuario, String decision) {
        this.idPedido = idPedido;
        this.idUsuario = idUsuario;
        this.decision = decision;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
}