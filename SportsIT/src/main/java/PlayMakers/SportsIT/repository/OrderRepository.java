package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.Order;
import PlayMakers.SportsIT.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>{
    Optional<Order> findById(String merchantUid);
    boolean existsById(String merchantUid);
    List<Order> findByBuyer(Member buyer);
    List<Order> findByBuyerAndStatus(Member buyer, OrderStatus status);

}
