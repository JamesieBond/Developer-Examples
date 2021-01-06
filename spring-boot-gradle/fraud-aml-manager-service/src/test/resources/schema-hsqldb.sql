DROP TABLE payments_case IF EXISTS;
DROP TABLE transaction_case IF EXISTS;
DROP TABLE party_info IF EXISTS;
DROP TABLE subscription_info IF EXISTS;
DROP TYPE JSONB IF EXISTS;

CREATE TYPE JSONB as OTHER;

CREATE TABLE payments_case (
	transaction_id VARCHAR(255) NOT NULL,
	created_date TIMESTAMP NOT NULL,
	payment_case JSONB NOT NULL,
	payment_type VARCHAR(255) NOT NULL,
	updated_date TIMESTAMP NOT NULL,
	CONSTRAINT "payments_case_pk" PRIMARY KEY (transaction_id)
)


CREATE TABLE transaction_case (
	transaction_id VARCHAR(255) NOT NULL,
	case_id VARCHAR(255) NOT NULL,
	CONSTRAINT "transaction_case_pk" PRIMARY KEY (transaction_id)
);

CREATE TABLE party_info (
	party_key VARCHAR(255) NOT NULL,
	post_code VARCHAR(255) NOT NULL,
	CONSTRAINT "party_info_pk" PRIMARY KEY (party_key)
);

CREATE TABLE subscription_info (
	subscription_key VARCHAR(255) NOT NULL,
	active_date TIMESTAMP NOT NULL,
	CONSTRAINT "subscription_key_pk" PRIMARY KEY (subscription_key)
);