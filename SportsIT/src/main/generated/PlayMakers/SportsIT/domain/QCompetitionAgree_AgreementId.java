package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCompetitionAgree_AgreementId is a Querydsl query type for AgreementId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QCompetitionAgree_AgreementId extends BeanPath<CompetitionAgree.AgreementId> {

    private static final long serialVersionUID = -48750219L;

    public static final QCompetitionAgree_AgreementId agreementId = new QCompetitionAgree_AgreementId("agreementId");

    public final NumberPath<Long> competitionId = createNumber("competitionId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QCompetitionAgree_AgreementId(String variable) {
        super(CompetitionAgree.AgreementId.class, forVariable(variable));
    }

    public QCompetitionAgree_AgreementId(Path<? extends CompetitionAgree.AgreementId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCompetitionAgree_AgreementId(PathMetadata metadata) {
        super(CompetitionAgree.AgreementId.class, metadata);
    }

}

