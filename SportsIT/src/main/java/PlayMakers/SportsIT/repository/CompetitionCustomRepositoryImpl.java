package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.CompetitionType;
import PlayMakers.SportsIT.domain.QCompetition;
import PlayMakers.SportsIT.domain.SportCategory;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static PlayMakers.SportsIT.domain.QCompetition.competition;

@Slf4j
@RequiredArgsConstructor
public class CompetitionCustomRepositoryImpl implements CompetitionCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Slice<Competition> findCompetitionBySlice(String keyword, String filterType, Pageable pageable) {
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
    private BooleanExpression filteredBy(String filterType) {
        if(filterType == null) return null;
        else if(filterType.equals("recruitingEnd")) return competition.recruitingEnd.between(LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        else if(filterType.equals("totalPrize")) return competition.totalPrize.goe(100000);
        else if(filterType.equals("recommend")) return competition.competitionType.in(CompetitionType.PREMIUM, CompetitionType.VIP);
        else throw new IllegalArgumentException("filterType이 유효하지 않습니다.");
    }
}
