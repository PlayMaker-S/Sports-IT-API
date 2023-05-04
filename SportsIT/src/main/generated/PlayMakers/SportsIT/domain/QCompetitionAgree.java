package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCompetitionAgree is a Querydsl query type for CompetitionAgree
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCompetitionAgree extends EntityPathBase<CompetitionAgree> {

    private static final long serialVersionUID = -35918146L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCompetitionAgree competitionAgree = new QCompetitionAgree("competitionAgree");

    public final StringPath agreementUrl = createString("agreementUrl");

    public final QCompetition competition;

    public final QCompetitionAgree_AgreementId id;

    public QCompetitionAgree(String variable) {
        this(CompetitionAgree.class, forVariable(variable), INITS);
    }

    public QCompetitionAgree(Path<? extends CompetitionAgree> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCompetitionAgree(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCompetitionAgree(PathMetadata metadata, PathInits inits) {
        this(CompetitionAgree.class, metadata, inits);
    }

    public QCompetitionAgree(Class<? extends CompetitionAgree> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.competition = inits.isInitialized("competition") ? new QCompetition(forProperty("competition"), inits.get("competition")) : null;
        this.id = inits.isInitialized("id") ? new QCompetitionAgree_AgreementId(forProperty("id")) : null;
    }

}

