/* Add roles */
INSERT INTO roles (title) VALUES ('ROLE_ADMIN');
INSERT INTO roles (title) VALUES ('ROLE_USER');

/* Add classes */
INSERT INTO class (name) VALUES ('Patient');

/* Add privileges */ 
INSERT INTO privilege (access_level, all_fields, class_id, condition, field_id, role_id) 
VALUES (1, NULL, 1, '(PatientId eq %PatientId) and (PatientStatusId eq 1 or PatientStatusId eq 2)', NULL, 2);

/* Add users  -> only for testing remove on production */
INSERT INTO users (patient_id, active, password, user_name)
VALUES (NULL, true, '$2a$12$AnJJhf5PICQ25vSzOo4AKe0mQcKbI3z8hDg24jSrvRTgfiiORfqRK', 'admin');

/* Assign users to roles */
INSERT INTO user_role (role_id, user_id)
VALUES (1, 1);