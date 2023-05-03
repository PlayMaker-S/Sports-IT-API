package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.QCompetition;
import PlayMakers.SportsIT.domain.SportCategory;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static PlayMakers.SportsIT.domain.QCompetition.competition;

@Slf4j
@RequiredArgsConstructor
public class CompetitionCustomRepositoryImpl implements CompetitionCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Slice<Competition> findCompetitionSortedByCreatedDate( String keyword, Pageable pageable) {
        QCompetition competition = QCompetition.competition;

        List<Competition> competitions = jpaQueryFactory.selectFrom(competition)
                .where(containsKeyword(keyword))
                .offset(pageable.getOffset()*pageable.getPageNumber())
                .limit(pageable.getPageSize()+1)
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .fetch();

        boolean hasNext = removeOneIfHasNext(pageable, competitions);

        pageable = pageable.next();

        return new SliceImpl<>(competitions, pageable, hasNext);
    }

    private List<OrderSpecifier> getOrderSpecifier(Sort sort) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String prop = order.getProperty();
            PathBuilder orderByExpression = new PathBuilder(Competition.class, "competition");
            orderSpecifiers.add(new OrderSpecifier(direction, orderByExpression.get(prop)));
        });
        return orderSpecifiers;
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
        List<SportCategory> foundCategories = categoriesContainKeyword(keyword);
        log.info("sportCategories = {}", foundCategories);

        return keyword != null ? competition.name.contains(keyword)
                .or(competition.host.name.contains(keyword))
                .or(competition.category.in(foundCategories))
                : null;
    }
}
