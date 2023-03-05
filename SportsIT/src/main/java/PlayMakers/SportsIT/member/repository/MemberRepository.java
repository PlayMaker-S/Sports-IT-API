package PlayMakers.SportsIT.member.repository;

import PlayMakers.SportsIT.member.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);  // Null을 감싸서 반환하기 위한 자료형
    Optional<Member> findByName(String name);
    List<Member> findAll();
}