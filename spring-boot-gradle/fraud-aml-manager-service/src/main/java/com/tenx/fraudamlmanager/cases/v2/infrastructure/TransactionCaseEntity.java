package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction_case")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCaseEntity {
    @Id
    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String caseId;
}
