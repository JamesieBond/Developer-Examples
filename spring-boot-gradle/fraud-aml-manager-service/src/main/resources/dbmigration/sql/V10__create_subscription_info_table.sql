CREATE TABLE subscription_info (
	subscription_key VARCHAR(255) NOT NULL,
	active_date TIMESTAMP NOT NULL,
	CONSTRAINT "subscription_key_pk" PRIMARY KEY (subscription_key ASC)
);