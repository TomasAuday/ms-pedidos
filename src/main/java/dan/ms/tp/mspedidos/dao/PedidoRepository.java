package dan.ms.tp.mspedidos.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import dan.ms.tp.mspedidos.modelo.Pedido;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends MongoRepository<Pedido,String> {
    Optional<Pedido> findById(String id);

    Optional<Pedido> findOneByNumeroPedido(Integer numeroPedido);

    Optional<Pedido> findFirstByOrderByNumeroPedidoDesc();

    List<Pedido> findByClienteRazonSocial(String razonSocial);

    @Query("{'fecha': {$gte: ?0, $lte: ?1} }")
    List<Pedido> findByFecha(Instant fromDate, Instant toDate);

    @Query("{'cliente.razonSocial': ?0, 'fecha': {$gte: ?1, $lte: ?2}}")
    List<Pedido> findByClienteFecha(String razonSocial, Instant startDate, Instant endDate);
}
