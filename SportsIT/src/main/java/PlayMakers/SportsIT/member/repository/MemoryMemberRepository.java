package PlayMakers.SportsIT.member.repository;

import PlayMakers.SportsIT.member.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class MemoryMemberRepository implements MemberRepository {

    private static final Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Member save(Member member) {
        member.setUid(++sequence);
        store.put(member.getUid(), member);

        log.info("MemberRepository: save: member: {}", member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = store.get(id);
        return Optional.ofNullable(member); // Null도 반환
    }

    @Override
    public Optional<Member> findByName(String name) {
        Optional<Member> result = store.values().stream()
                .filter(member -> member.getName().equals(name))
                .findAny();

        return result;
    }

    @Override
    public List<Member> findAll() {
        return (List<Member>) store.values();
    }

    @Override
    public void update(Long id, Member updateParam) {
        Member member = store.get(id);
        member.setName(updateParam.getName());
        member.setPw(updateParam.getPw());
    }

    @Override
    public void clearStore() {
        store.clear();
    }
}
