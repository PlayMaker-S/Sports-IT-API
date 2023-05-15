package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPoster_PosterId is a Querydsl query type for PosterId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPoster_PosterId extends BeanPath<Poster.PosterId> {

    private static final long serialVersionUID = -490171848L;

    public static final QPoster_PosterId posterId = new QPoster_PosterId("posterId");

    public final NumberPath<Long> competitionId = createNumber("competitionId", Long.class);

    public final StringPath posterUrl = createString("posterUrl");

    public QPoster_PosterId(String variable) {
        super(Poster.PosterId.class, forVariable(variable));
    }

    public QPoster_PosterId(Path<? extends Poster.PosterId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPoster_PosterId(PathMetadata metadata) {
        super(Poster.PosterId.class, metadata);
    }

}

