package PlayMakers.SportsIT.member.repository;

import PlayMakers.SportsIT.member.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MemoryMemberRepositoryTest {
    MemberRepository memberRepository = new MemoryMemberRepository();

    @AfterEach
    void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void save() {
        //given
        Member member = new Member("test_id", "test", "홍길동");
        //when
        Member savedMember = memberRepository.save(member);

        //then
        Member findMember = memberRepository.findById(savedMember.getUid()).get();

        //System.out.println("savedMember = " + savedMember);
        //System.out.println("findMember = " + findMember);
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    void updateMember() {
        //given
        Member member = new Member("Test_ID", "test", "홍길동");

        Member savedMember = memberRepository.save(member);
        Long savedId = savedMember.getUid();
        System.out.println("savedMember = " + savedMember + ", password = " + savedMember.getPw() + ", name = " + savedMember.getName());
        //when
        Member updateParam = new Member("Test_ID", "test2", "홍길동");
        memberRepository.update(savedId, updateParam);

        Member findMember = memberRepository.findById(savedId).get();
        System.out.println("findMember = " + findMember + ", password = " + findMember.getPw() + ", name = " + findMember.getName());
        //then
        assertThat(findMember.getPw()).isEqualTo(updateParam.getPw());
        assertThat(findMember.getName()).isEqualTo(updateParam.getName());

    }
}