package dan.ms.tp.mspedidos.dto.pedido;



public class PagoDtoForDecision {
    private String numeroPedido;
    private Integer idUsuario;
    private String decision;

    public PagoDtoForDecision() {
        // Constructor vacío necesario para deserialización por Spring
    }

    public PagoDtoForDecision(String numeroPedido, Integer idUsuario, String decision) {
        this.numeroPedido = numeroPedido;
        this.idUsuario = idUsuario;
        this.decision = decision;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
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