package PlayMakers.SportsIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPayment is a Querydsl query type for Payment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayment extends EntityPathBase<Payment> {

    private static final long serialVersionUID = -1810850731L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPayment payment = new QPayment("payment");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final QMember buyer;

    public final QCompetition competition;

    public final StringPath content = createString("content");

    public final StringPath impUid = createString("impUid");

    public final StringPath merchantUid = createString("merchantUid");

    public final StringPath refundReason = createString("refundReason");

    public final EnumPath<PlayMakers.SportsIT.enums.PaymentStatus> status = createEnum("status", PlayMakers.SportsIT.enums.PaymentStatus.class);

    public final EnumPath<PlayMakers.SportsIT.enums.PaymentType> type = createEnum("type", PlayMakers.SportsIT.enums.PaymentType.class);

    public QPayment(String variable) {
        this(Payment.class, forVariable(variable), INITS);
    }

    public QPayment(Path<? extends Payment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPayment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPayment(PathMetadata metadata, PathInits inits) {
        this(Payment.class, metadata, inits);
    }

    public QPayment(Class<? extends Payment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.buyer = inits.isInitialized("buyer") ? new QMember(forProperty("buyer"), inits.get("buyer")) : null;
        this.competition = inits.isInitialized("competition") ? new QCompetition(forProperty("competition"), inits.get("competition")) : null;
    }

}

