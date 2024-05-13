package dan.ms.tp.mspedidos.service;

import dan.ms.tp.mspedidos.modelo.Cliente;

public interface ClienteService {
    Cliente getCliente(int id, String token);
}
    