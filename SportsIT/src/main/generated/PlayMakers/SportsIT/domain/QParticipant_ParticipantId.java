package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QParticipant_ParticipantId is a Querydsl query type for ParticipantId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QParticipant_ParticipantId extends BeanPath<Participant.ParticipantId> {

    private static final long serialVersionUID = -1956622174L;

    public static final QParticipant_ParticipantId participantId = new QParticipant_ParticipantId("participantId");

    public final NumberPath<Long> competitionId = createNumber("competitionId", Long.class);

    public final StringPath sectorTitle = createString("sectorTitle");

    public final StringPath subSectorName = createString("subSectorName");

    public final NumberPath<Long> uid = createNumber("uid", Long.class);

    public QParticipant_ParticipantId(String variable) {
        super(Participant.ParticipantId.class, forVariable(variable));
    }

    public QParticipant_ParticipantId(Path<? extends Participant.ParticipantId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QParticipant_ParticipantId(PathMetadata metadata) {
        super(Participant.ParticipantId.class, metadata);
    }

}

