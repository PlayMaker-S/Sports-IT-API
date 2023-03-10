package PlayMakers.SportsIT.member.repository;

import PlayMakers.SportsIT.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

        Optional<Member> findByName(String name);
        List<Member> findAll();
}