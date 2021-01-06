package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)

@Data
@Table(name = "payments_case")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true)
public class PaymentCaseEntityV2 {

    @Id
    String transactionId;

    @Column(nullable = false)
    String paymentType;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false,
            updatable = false)
    Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    Date updatedDate;

    @Type(type = "jsonb")
    @Column(nullable = false,
            columnDefinition = "jsonb")
    CaseV2 paymentCase;
}
