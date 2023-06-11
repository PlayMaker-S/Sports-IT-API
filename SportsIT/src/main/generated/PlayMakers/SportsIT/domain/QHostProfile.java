package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHostProfile is a Querydsl query type for HostProfile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHostProfile extends EntityPathBase<HostProfile> {

    private static final long serialVersionUID = 1767287216L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHostProfile hostProfile = new QHostProfile("hostProfile");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath facebookUrl = createString("facebookUrl");

    public final StringPath homepageUrl = createString("homepageUrl");

    public final NumberPath<Long> hostProfileId = createNumber("hostProfileId", Long.class);

    public final StringPath instagramUrl = createString("instagramUrl");

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final StringPath location = createString("location");

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final QMember member;

    public final StringPath naverUrl = createString("naverUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath youtubeUrl = createString("youtubeUrl");

    public QHostProfile(String variable) {
        this(HostProfile.class, forVariable(variable), INITS);
    }

    public QHostProfile(Path<? extends HostProfile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHostProfile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHostProfile(PathMetadata metadata, PathInits inits) {
        this(HostProfile.class, metadata, inits);
    }

    public QHostProfile(Class<? extends HostProfile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

