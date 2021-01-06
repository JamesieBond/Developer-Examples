drop table if exists external_risk_score cascade;
drop table if exists subscription_entity cascade;
drop table if exists subscription cascade;

CREATE TABLE subscription (
subscription_key VARCHAR(255) NOT NULL,
account_number VARCHAR(255) NULL,
created_date TIMESTAMP NOT NULL,
party_key VARCHAR(255) NOT NULL,
sort_code VARCHAR(255) NULL,
subscription_json JSONB NOT NULL,
updated_date TIMESTAMP NOT NULL,
CONSTRAINT "subscription_entity_pk" PRIMARY KEY (subscription_key)
);

CREATE INDEX idx_subscription_party_key
ON subscription(party_key);

CREATE TABLE external_risk_score (
id UUID NOT NULL,
created_date TIMESTAMP NOT NULL,
external_risk_score_json JSONB NOT NULL,
party_key VARCHAR(255) NOT NULL,
provider VARCHAR(255) NOT NULL,
risk_score VARCHAR(255) NOT NULL,
CONSTRAINT "external_risk_score_pk" PRIMARY KEY (id)
);

CREATE INDEX idx_external_risk_score_party_key
ON external_risk_score(party_key);