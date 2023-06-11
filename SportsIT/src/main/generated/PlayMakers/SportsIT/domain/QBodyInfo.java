package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBodyInfo is a Querydsl query type for BodyInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBodyInfo extends EntityPathBase<BodyInfo> {

    private static final long serialVersionUID = 17846529L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBodyInfo bodyInfo = new QBodyInfo("bodyInfo");

    public final NumberPath<Float> fatMass = createNumber("fatMass", Float.class);

    public final NumberPath<Float> height = createNumber("height", Float.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final NumberPath<Float> smMass = createNumber("smMass", Float.class);

    public final NumberPath<Float> weight = createNumber("weight", Float.class);

    public QBodyInfo(String variable) {
        this(BodyInfo.class, forVariable(variable), INITS);
    }

    public QBodyInfo(Path<? extends BodyInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBodyInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBodyInfo(PathMetadata metadata, PathInits inits) {
        this(BodyInfo.class, metadata, inits);
    }

    public QBodyInfo(Class<? extends BodyInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

