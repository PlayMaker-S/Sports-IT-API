package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

        List<Member> findByName(String name);
        Member findByEmail(String email);;
        List<Member> findAll();

        @EntityGraph(attributePaths = "memberType") // Eager 조회로 가져옴
        Optional<Member> findOneWithMemberTypeByEmail(String email);
}