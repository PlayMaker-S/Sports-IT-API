package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.Payment;
import PlayMakers.SportsIT.enums.PaymentStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String>{
    @NotNull Optional<Payment> findById(String merchantUid);
    boolean existsById(String merchantUid);
    List<Payment> findByBuyer(Member buyer);
    List<Payment> findByBuyerAndStatus(Member buyer, PaymentStatus status);

}
