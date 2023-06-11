package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAgreement is a Querydsl query type for Agreement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAgreement extends EntityPathBase<Agreement> {

    private static final long serialVersionUID = 296510617L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAgreement agreement = new QAgreement("agreement");

    public final StringPath agreementUrl = createString("agreementUrl");

    public final QCompetition competition;

    public final StringPath name = createString("name");

    public QAgreement(String variable) {
        this(Agreement.class, forVariable(variable), INITS);
    }

    public QAgreement(Path<? extends Agreement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAgreement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAgreement(PathMetadata metadata, PathInits inits) {
        this(Agreement.class, metadata, inits);
    }

    public QAgreement(Class<? extends Agreement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.competition = inits.isInitialized("competition") ? new QCompetition(forProperty("competition"), inits.get("competition")) : null;
    }

}

