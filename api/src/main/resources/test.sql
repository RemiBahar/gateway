/* Testing for fields */
INSERT INTO roles (title) VALUES ('ROLE_USER'); 

INSERT INTO users (patient_id, active, password, user_name)
VALUES (1, true, '$2a$12$AnJJhf5PICQ25vSzOo4AKe0mQcKbI3z8hDg24jSrvRTgfiiORfqRK', 'field');

INSERT INTO user_role (role_id, user_id)
VALUES (3, 3);

INSERT INTO field (class_id, name)
VALUES (1, 'FirstName'), (1, 'FamilyName');

INSERT INTO privilege (access_level, all_fields, class_id, condition, field_id, role_id) 
VALUES (2, FALSE, 1, '(PatientId eq %PatientId)', 1, 3), (2, FALSE, 1, '(PatientId eq %PatientId)', 2, 3);