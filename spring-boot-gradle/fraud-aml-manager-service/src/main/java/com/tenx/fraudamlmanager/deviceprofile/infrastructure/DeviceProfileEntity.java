package com.tenx.fraudamlmanager.deviceprofile.infrastructure;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.Date;
import java.util.HashMap;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(name = "device_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DeviceProfileEntityId.class)
public class DeviceProfileEntity {
  @Id
  @Column(nullable = false)
  private String partyKey;

  @Id
  @Column(nullable = false)
  private String deviceKeyId;

  @Column(nullable = false)
  private String sessionId;

  @Column
  private String eventType;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb", nullable = false)
  private HashMap<String, Object> deviceProfile;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date updatedDate;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date createdDate;
}
