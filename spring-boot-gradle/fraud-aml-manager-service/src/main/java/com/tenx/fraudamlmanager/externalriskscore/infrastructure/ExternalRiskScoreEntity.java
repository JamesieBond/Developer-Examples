package com.tenx.fraudamlmanager.externalriskscore.infrastructure;

import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@TypeDef(
    name = "jsonb",
    typeClass = JsonBinaryType.class
)
@Table(name = "external_risk_score")
@Data
@NoArgsConstructor(force = true)
public class ExternalRiskScoreEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String riskScore;

    @Column(nullable = false)
    private String partyKey;

    @Column(nullable = false)
    private String provider;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdDate;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", nullable = false)
    private ExternalRiskScore externalRiskScoreJson;

}
