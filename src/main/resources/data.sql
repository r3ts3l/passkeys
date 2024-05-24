-- Insert sample user
INSERT INTO app_user (username, display_name) VALUES ('testuser', 'Test User');

-- Insert sample WebAuthn credential for the sample user
INSERT INTO web_authn_credential (credential_id, public_key, counter, user_id)
VALUES ('sampleCredentialId', 'samplePublicKey', 0, 1);
