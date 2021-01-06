CREATE TABLE external_risk_score (
	id UUID NOT NULL,
	created_date TIMESTAMP NOT NULL,
	external_risk_score_json JSONB NULL,
	party_key VARCHAR(255) NULL,
	provider VARCHAR(255) NULL,
	risk_score VARCHAR(255) NULL,
	CONSTRAINT "external_risk_score_pk" PRIMARY KEY (id)
);

CREATE INDEX idx_external_risk_score_party_key
ON external_risk_score(party_key);