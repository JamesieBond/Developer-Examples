drop table if exists device_profile cascade;

CREATE TABLE device_profile (
    session_id VARCHAR(255) NOT NULL,
	party_key VARCHAR(255) NOT NULL,
	device_key_id VARCHAR(255) NOT NULL,
	event_type VARCHAR(255) NULL,
	created_date TIMESTAMP NOT NULL,
	updated_date TIMESTAMP NOT NULL,
	device_profile JSONB NULL,
	CONSTRAINT "device_profile_pk" PRIMARY KEY (party_key, device_key_id)
);

CREATE INDEX idx_primary_key
ON device_profile(party_key, device_key_id);