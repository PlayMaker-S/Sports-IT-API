package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -1803543957L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final BooleanPath activated = createBoolean("activated");

    public final StringPath birth = createString("birth");

    public final ListPath<CompetitionResult, QCompetitionResult> competitionResults = this.<CompetitionResult, QCompetitionResult>createList("competitionResults", CompetitionResult.class, QCompetitionResult.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final StringPath email = createString("email");

    public final ListPath<Feed, QFeed> feeds = this.<Feed, QFeed>createList("feeds", Feed.class, QFeed.class, PathInits.DIRECT2);

    public final SetPath<Follow, QFollow> followers = this.<Follow, QFollow>createSet("followers", Follow.class, QFollow.class, PathInits.DIRECT2);

    public final SetPath<Follow, QFollow> following = this.<Follow, QFollow>createSet("following", Follow.class, QFollow.class, PathInits.DIRECT2);

    public final QHostProfile hostProfile;

    public final SetPath<MemberType, QMemberType> memberType = this.<MemberType, QMemberType>createSet("memberType", MemberType.class, QMemberType.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath phone = createString("phone");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final StringPath pw = createString("pw");

    public final EnumPath<PlayMakers.SportsIT.enums.Subscribe> subscription = createEnum("subscription", PlayMakers.SportsIT.enums.Subscribe.class);

    public final NumberPath<Long> uid = createNumber("uid", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hostProfile = inits.isInitialized("hostProfile") ? new QHostProfile(forProperty("hostProfile"), inits.get("hostProfile")) : null;
    }

}

