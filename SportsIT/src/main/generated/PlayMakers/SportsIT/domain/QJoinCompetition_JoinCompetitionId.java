package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QJoinCompetition_JoinCompetitionId is a Querydsl query type for JoinCompetitionId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QJoinCompetition_JoinCompetitionId extends BeanPath<JoinCompetition.JoinCompetitionId> {

    private static final long serialVersionUID = 383500326L;

    public static final QJoinCompetition_JoinCompetitionId joinCompetitionId = new QJoinCompetition_JoinCompetitionId("joinCompetitionId");

    public final NumberPath<Long> competitionId = createNumber("competitionId", Long.class);

    public final NumberPath<Long> uid = createNumber("uid", Long.class);

    public QJoinCompetition_JoinCompetitionId(String variable) {
        super(JoinCompetition.JoinCompetitionId.class, forVariable(variable));
    }

    public QJoinCompetition_JoinCompetitionId(Path<? extends JoinCompetition.JoinCompetitionId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJoinCompetition_JoinCompetitionId(PathMetadata metadata) {
        super(JoinCompetition.JoinCompetitionId.class, metadata);
    }

}

