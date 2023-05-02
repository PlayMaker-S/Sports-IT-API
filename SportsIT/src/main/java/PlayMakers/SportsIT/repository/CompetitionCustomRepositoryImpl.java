package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.SportCategory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

//import static PlayMakers.SportsIT.domain.QCompetition.competition;

@RequiredArgsConstructor
public class CompetitionCustomRepositoryImpl implements CompetitionCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Slice<Competition> findCompetitionSortedByCreatedDate(String keyword) {

//        JPAQuery<Competition> jpaQuery = jpaQueryFactory.selectFrom(competition)
//                .where(containsKeyword(keyword))
//                .orderBy(competition.createdDate.desc());
        return null;
    }

    @Override
    public Slice<Competition> findCompetitionSortedByViewCount(String keyword) {
        return null;
    }

    @Override
    public Slice<Competition> findCompetitionSortedByScrapCount(String keyword) {
        return null;
    }

    @Override
    public Slice<Competition> findCompetitionWithConditionsSortedByCreatedDate(String keyword, String condition) {
        return null;
    }

    @Override
    public Slice<Competition> findCompetitionWithConditionsSortedByViewCount(String keyword, String condition) {
        return null;
    }

    @Override
    public Slice<Competition> findCompetitionWithConditionsSortedByScrapCount(String keyword, String condition) {
        return null;
    }

    private BooleanExpression containsKeyword(String keyword) {
//        return keyword != null ? competition.name.contains(keyword)
//                .or(competition.host.name.contains(keyword))
//                .or(competition.category.eq(SportCategory.valueOf(keyword)))
//                : null;
        return null;
    }
}
