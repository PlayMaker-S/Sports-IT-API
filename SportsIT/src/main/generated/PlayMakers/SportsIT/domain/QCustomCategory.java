package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCustomCategory is a Querydsl query type for CustomCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCustomCategory extends EntityPathBase<CustomCategory> {

    private static final long serialVersionUID = 1753930336L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCustomCategory customCategory = new QCustomCategory("customCategory");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> customCategoryId = createNumber("customCategoryId", Long.class);

    public final QMember member;

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QCustomCategory(String variable) {
        this(CustomCategory.class, forVariable(variable), INITS);
    }

    public QCustomCategory(Path<? extends CustomCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCustomCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCustomCategory(PathMetadata metadata, PathInits inits) {
        this(CustomCategory.class, metadata, inits);
    }

    public QCustomCategory(Class<? extends CustomCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

