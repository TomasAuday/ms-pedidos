package dan.ms.tp.mspedidos.dto.mensajeprocesado;

public class MensajeProcesadoDto {

    private String idPedido;
    private String emailCliente ;
    private String decision;

    public MensajeProcesadoDto() {
        // Constructor vacío necesario para deserialización por Spring
    }

    public MensajeProcesadoDto(String idPedido, String emailCliente, String decision) {
        this.idPedido = idPedido;
        this.emailCliente = emailCliente;
        this.decision = decision;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getIdUsuario() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
}

