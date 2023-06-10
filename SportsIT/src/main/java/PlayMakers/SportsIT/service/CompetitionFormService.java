package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.CompetitionForm;
import PlayMakers.SportsIT.repository.CompetitionFormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitionFormService {
    private final CompetitionFormRepository competitionFormRepository;
    public String createForm(CompetitionForm form) throws Exception{
        return competitionFormRepository.saveForm(form);
    }
    public CompetitionForm getForm(String formId) throws Exception{
        return competitionFormRepository.findForm(formId);
    }
    public void deleteForm(String formId) throws Exception{
        competitionFormRepository.deleteForm(formId);
    }

}
