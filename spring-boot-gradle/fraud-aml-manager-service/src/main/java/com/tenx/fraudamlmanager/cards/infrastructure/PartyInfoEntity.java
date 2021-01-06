package com.tenx.fraudamlmanager.cards.infrastructure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "party_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartyInfoEntity {

  @Id
  @Column(nullable = false)
  private String partyKey;

  @Column(nullable = false)
  private String postCode;

}
