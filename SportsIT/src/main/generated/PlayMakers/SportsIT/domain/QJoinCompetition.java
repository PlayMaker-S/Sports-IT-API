package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJoinCompetition is a Querydsl query type for JoinCompetition
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJoinCompetition extends EntityPathBase<JoinCompetition> {

    private static final long serialVersionUID = 239012228L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QJoinCompetition joinCompetition = new QJoinCompetition("joinCompetition");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QCompetition competition;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath formId = createString("formId");

    public final QJoinCompetition_JoinCompetitionId id;

    public final EnumPath<JoinCompetition.joinType> joinType = createEnum("joinType", JoinCompetition.joinType.class);

    public final QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QJoinCompetition(String variable) {
        this(JoinCompetition.class, forVariable(variable), INITS);
    }

    public QJoinCompetition(Path<? extends JoinCompetition> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QJoinCompetition(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QJoinCompetition(PathMetadata metadata, PathInits inits) {
        this(JoinCompetition.class, metadata, inits);
    }

    public QJoinCompetition(Class<? extends JoinCompetition> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.competition = inits.isInitialized("competition") ? new QCompetition(forProperty("competition"), inits.get("competition")) : null;
        this.id = inits.isInitialized("id") ? new QJoinCompetition_JoinCompetitionId(forProperty("id")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

