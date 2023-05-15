package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.BodyInfo;
import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.BodyInfoDto;
import PlayMakers.SportsIT.repository.BodyInfoRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BodyInfoService {
    private final BodyInfoRepository bodyInfoRepository;
    private final MemberRepository memberRepository;

    public BodyInfo create(BodyInfoDto dto) {
        log.info("신체정보 생성 요청: {}", dto);
        Member member = memberRepository.findById(dto.getMember().getUid()).orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
        BodyInfo newBodyInfo = bodyInfoRepository.save(dto.toEntity());
        return newBodyInfo;
    }
    public Optional<BodyInfo> getBodyInfo(Member member){
        Optional<BodyInfo> findBodyInfo = bodyInfoRepository.findByMemberUid(member.getUid());
        return findBodyInfo;
    }

    public BodyInfo update(Long bodyInfoId, BodyInfoDto dto) {
        log.info("신체정보 수정 : {}", bodyInfoId);

        BodyInfo updated = dto.toEntity();

        // ID를 기준으로 신체정보 조회
        BodyInfo bodyInfo = bodyInfoRepository.findById(bodyInfoId)
                .orElseThrow(()-> new EntityNotFoundException("해당 신체정보가 존재하지 않습니다."));

        // 수정 사항 적용
        updateBodyInfo(bodyInfo, dto);

//        BodyInfo modifiedInfo = bodyInfoRepository.save(bodyInfo);
        return bodyInfo;
    }

    private void updateBodyInfo(BodyInfo bodyInfo, BodyInfoDto dto) {
        bodyInfo.setWeight(dto.getWeight());
        bodyInfo.setHeight(dto.getHeight());
        bodyInfo.setSmMass(dto.getSmMass());
        bodyInfo.setFatMass(dto.getFatMass());
    }
}