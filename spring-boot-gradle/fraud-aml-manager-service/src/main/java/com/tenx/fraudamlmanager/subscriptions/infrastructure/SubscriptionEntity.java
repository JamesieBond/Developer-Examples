package com.tenx.fraudamlmanager.subscriptions.infrastructure;

import com.tenx.fraudamlmanager.subscriptions.api.Subscription;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@TypeDef(
  name = "jsonb",
  typeClass = JsonBinaryType.class
)

@Table(name = "subscription")
@Data
@NoArgsConstructor(force = true)
public class SubscriptionEntity {

  @Id
  @Column(nullable = false)
  private String subscriptionKey;

  private String accountNumber;

  private String sortCode;

  @Column(nullable = false)
  private String partyKey;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date createdDate;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date updatedDate;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb", nullable = false)
  private Subscription subscriptionJson;

}
