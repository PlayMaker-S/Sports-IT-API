package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMemberType is a Querydsl query type for MemberType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberType extends EntityPathBase<MemberType> {

    private static final long serialVersionUID = -923865019L;

    public static final QMemberType memberType = new QMemberType("memberType");

    public final StringPath roleName = createString("roleName");

    public QMemberType(String variable) {
        super(MemberType.class, forVariable(variable));
    }

    public QMemberType(Path<? extends MemberType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemberType(PathMetadata metadata) {
        super(MemberType.class, metadata);
    }

}

