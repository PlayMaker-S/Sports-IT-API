package PlayMakers.SportsIT.member.repository;

import PlayMakers.SportsIT.member.domain.Member;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class JpaMemberRepository implements MemberRepository{
    private final EntityManager em;

    // JPA는 EntityManager를 통해 DB에 접근한다.
    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member); // Client에서 Null 처리를 할 수 있게
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();

        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        // 객체 자체를 조회하는 것이 아니라, JPQL을 사용하여 조회한다.
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    @Override
    public void update(Long id, Member updateParam) {
        Member member = em.find(Member.class, id);
        member.setName(updateParam.getName());
    }

    @Override
    public void clearStore() {
        em.createQuery("delete from Member").executeUpdate();
    }
}
