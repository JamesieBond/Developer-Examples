CREATE TABLE transaction_case (
	transaction_id VARCHAR(255) NOT NULL,
	case_id VARCHAR(255) NOT NULL,
	CONSTRAINT "transaction_case_pk" PRIMARY KEY (transaction_id ASC)
);

CREATE INDEX transaction_case_case_id_idx
ON transaction_case(case_id);