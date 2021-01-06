package com.tenx.fraudamlmanager.subscriptions.infrastructure;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionInfo {

    @Id
    @Column(name = "subscription_key", nullable = false)
    private String subscriptionKey;

    @Column(name = "active_date", nullable = false)
    private Date activeDate;

}