package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCompetitionResult is a Querydsl query type for CompetitionResult
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCompetitionResult extends EntityPathBase<CompetitionResult> {

    private static final long serialVersionUID = -628568501L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCompetitionResult competitionResult = new QCompetitionResult("competitionResult");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QCompetition competition;

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> historyDate = createDateTime("historyDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QCompetitionResult(String variable) {
        this(CompetitionResult.class, forVariable(variable), INITS);
    }

    public QCompetitionResult(Path<? extends CompetitionResult> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCompetitionResult(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCompetitionResult(PathMetadata metadata, PathInits inits) {
        this(CompetitionResult.class, metadata, inits);
    }

    public QCompetitionResult(Class<? extends CompetitionResult> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.competition = inits.isInitialized("competition") ? new QCompetition(forProperty("competition"), inits.get("competition")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

