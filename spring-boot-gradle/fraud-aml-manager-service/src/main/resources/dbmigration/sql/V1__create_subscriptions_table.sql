CREATE TABLE subscription_entity (
	subscription_key VARCHAR(255) NOT NULL,
	account_number VARCHAR(255) NULL,
	created_date TIMESTAMP NOT NULL,
	party_key VARCHAR(255) NULL,
	sort_code VARCHAR(255) NULL,
	subscription_json JSONB NULL,
	updated_date TIMESTAMP NOT NULL,
	CONSTRAINT "subscription_entity_pk" PRIMARY KEY (subscription_key)
);

CREATE INDEX idx_subscription_entity_party_key
ON subscription_entity(party_key);

