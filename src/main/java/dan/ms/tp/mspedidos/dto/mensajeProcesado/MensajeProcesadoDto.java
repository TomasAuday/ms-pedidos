package dan.ms.tp.mspedidos.dto.mensajeprocesado;

public class MensajeProcesadoDto {

    private String numeroPedido;
    private String emailCliente ;
    private String decision;

    public MensajeProcesadoDto() {
        // Constructor vacío necesario para deserialización por Spring
    }

    public MensajeProcesadoDto(String numeroPedido, String emailCliente, String decision) {
        this.numeroPedido = numeroPedido;
        this.emailCliente = emailCliente;
        this.decision = decision;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
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

