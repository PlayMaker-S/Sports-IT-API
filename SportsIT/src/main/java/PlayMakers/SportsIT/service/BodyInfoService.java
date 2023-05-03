package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.BodyInfo;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.BodyInfoDto;
import PlayMakers.SportsIT.repository.BodyInfoRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BodyInfoService {
    private final BodyInfoRepository bodyInfoRepository;
    private final MemberRepository memberRepository;

    public BodyInfo create(BodyInfoDto dto) {
        log.info("신체정보 생성 요청: {}", dto);
        Member host = memberRepository.findById(dto.getMember().getUid()).orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
        BodyInfo newBodyInfo = dto.toEntity();

        return null;
    }
}
