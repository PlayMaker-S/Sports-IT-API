package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.competitions.domain.QCompetition;
import PlayMakers.SportsIT.competitions.enums.CompetitionState;
import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.enums.CompetitionType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static PlayMakers.SportsIT.competitions.domain.QCompetition.competition;

@Slf4j
@RequiredArgsConstructor
public class CompetitionCustomRepositoryImpl implements CompetitionCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Slice<Competition> findCompetitionBySlice(String keyword, List<String> filterType, Pageable pageable) {
        QCompetition competition = QCompetition.competition;

        OrderSpecifier orderSpecifier = getOrderSpecifier(pageable, competition);

        List<Competition> competitions = jpaQueryFactory.selectFrom(competition)
                .where(
                        containsKeyword(keyword),
                        filteredBy(filterType)
                )
                .offset(pageable.getOffset()*pageable.getPageNumber())
                .limit(pageable.getPageSize()+1)
                .orderBy(
                        orderSpecifier,
                        competition.createdDate.desc()
                )
                .fetch();

        boolean hasNext = removeOneIfHasNext(pageable, competitions);

        return new SliceImpl<>(competitions, pageable, hasNext);
    }

    /**
     * 유저가 참가한 대회 목록 조회
     * @param competitionIds 대회 아이디 리스트
     * @param pageable 페이지 정보
     * @return 유저가 참가한 대회 목록
     */
    @Override
    public Slice<Competition> findCompetitionsBySliceWithIdsAndUid(List<Long> competitionIds, Long uid, Pageable pageable) {
        QCompetition competition = QCompetition.competition;
        QJoinCompetition joinCompetition = QJoinCompetition.joinCompetition;

        OrderSpecifier<LocalDateTime> orderByCreatedAt = joinCompetition.createdDate.desc();

        List<Competition> competitions = jpaQueryFactory
                .selectFrom(competition)
                .join(joinCompetition).on(competition.competitionId.eq(joinCompetition.competition.competitionId))
                .where(joinCompetition.member.uid.eq(uid)
                        .and(joinCompetition.competition.competitionId.in(competitionIds)))
                .orderBy(
                        orderByCreatedAt,
                        competition.startDate.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        boolean hasNext = removeOneIfHasNext(pageable, competitions);

        return new SliceImpl<>(competitions, pageable, hasNext);
    }

    /**
     * 주최자 기준 대회 목록 조회
     * @param hostUid 주최자 uid
     * @param pageable 페이지 정보
     * @return 주최자가 주최한 대회 목록
     */

    public Slice<Competition> findCompetitionsBySliceWithHostUid(Long hostUid, Pageable pageable) {
        QCompetition competition = QCompetition.competition;

        OrderSpecifier<LocalDateTime> orderByCreatedAt = competition.createdDate.desc();

        List<Competition> competitions = jpaQueryFactory
                .selectFrom(competition)
                .where(competition.host.uid.eq(hostUid))
                .orderBy(
                        orderByCreatedAt,
                        competition.startDate.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        boolean hasNext = removeOneIfHasNext(pageable, competitions);

        return new SliceImpl<>(competitions, pageable, hasNext);
    }


    @NotNull
    private static OrderSpecifier getOrderSpecifier(Pageable pageable, QCompetition competition) {
        OrderSpecifier orderSpecifier = new OrderSpecifier<>(Order.DESC, competition.createdDate);
        for(Sort.Order order : pageable.getSort()){
            log.info("order: {}", order);
            NumberPath<Integer> orderPath;
            if(order.getProperty().equals("viewCount")) orderPath = competition.viewCount;
            else if(order.getProperty().equals("scrapCount")) orderPath = competition.scrapCount;
            else continue;
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            orderSpecifier = new OrderSpecifier<>(direction, orderPath);
        }
        return orderSpecifier;
    }

    private static List<SportCategory> categoriesContainKeyword(String keyword) {
        List<SportCategory> sportCategoriesContainsKeyword = new ArrayList<>();
        if(keyword != null) {
            for (SportCategory sportCategory : SportCategory.values()) {
                if (sportCategory.getCategoryName().contains(keyword)) {
                    sportCategoriesContainsKeyword.add(sportCategory);
                }
            }
        }
        else{
            sportCategoriesContainsKeyword = List.of(SportCategory.values());
        }
        return sportCategoriesContainsKeyword;
    }

    private static boolean removeOneIfHasNext(Pageable pageable, List<Competition> competitions) {
        boolean hasNext = competitions.size() == pageable.getPageSize() + 1;
        if(hasNext) {
            competitions.remove(competitions.size()-1);
        }
        return hasNext;
    }

    private BooleanExpression containsKeyword(String keyword) {
        List<SportCategory> foundCategories = categoriesContainKeyword(keyword);
        log.info("sportCategories = {}", foundCategories);

        return keyword != null ? competition.name.contains(keyword)
                .or(competition.host.name.contains(keyword))
                .or(competition.category.in(foundCategories))
                : null;
    }
    private BooleanBuilder filteredBy(List<String> filterType) {
        // CompetitionState : PLANNING, RECRUITING, RECRUITING_END, IN_PROGRESS, END
        // recruitingEnd : 7일 이내
        // totalPrize : 100,000원 이상
        // recommend : FREE, PREMIUM, VIP

        if(filterType == null) return null;
        // 대회 상태 옵션이 있을 경우
        BooleanExpression expression = null;
        BooleanBuilder builder = new BooleanBuilder();
        if(filterType.contains("PLANNING")) builder.or(competition.state.eq(CompetitionState.PLANNING));
        if(filterType.contains("RECRUITING")) builder.or(competition.state.eq(CompetitionState.RECRUITING));
        if(filterType.contains("RECRUITING_END")) builder.or(competition.state.eq(CompetitionState.RECRUITING_END));
        if(filterType.contains("IN_PROGRESS")) builder.or(competition.state.eq(CompetitionState.IN_PROGRESS));
        if(filterType.contains("END")) builder.or(competition.state.eq(CompetitionState.END));

        if(filterType.contains("recruitingEnd")) builder.and(competition.recruitingEnd.between(LocalDateTime.now(), LocalDateTime.now().plusDays(7)));
        if(filterType.contains("totalPrize")) builder.and(competition.totalPrize.goe(100000));
        if(filterType.contains("recommend")) builder.and(competition.competitionType.in(CompetitionType.PREMIUM, CompetitionType.VIP));
        return builder;
    }
}
