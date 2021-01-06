CREATE TABLE payments_case (
	transaction_id VARCHAR(255) NOT NULL,
	created_date TIMESTAMP NOT NULL,
	payment_case JSONB NOT NULL,
	payment_type VARCHAR(255) NOT NULL,
	updated_date TIMESTAMP NOT NULL,
	CONSTRAINT "payments_case_pk" PRIMARY KEY (transaction_id ASC)
)