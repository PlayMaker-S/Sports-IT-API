package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

        List<Member> findByName(String name);
        List<Member> findByNameContaining(String name);
        Member findByEmail(String email);
        List<Member> findAll();
        Boolean existsByEmail(String email);
        Boolean existsByPhone(String phoneNumber);

        @EntityGraph(attributePaths = "memberType") // Eager 조회로 가져옴
        Optional<Member> findOneWithMemberTypeByEmail(String email);

        Optional<Member> findByPhone(String phoneNumber);
}