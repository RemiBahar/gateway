/* Add roles */
INSERT INTO roles (title) VALUES ('ROLE_ADMIN');
INSERT INTO roles (title) VALUES ('ROLE_USER');
INSERT INTO roles (title) VALUES ('ROLE_ASSISTANCE');

/* Add users  -> only for testing remove on production */
INSERT INTO users (patient_id, active, password, user_name)
VALUES (NULL, true, '$2a$12$AnJJhf5PICQ25vSzOo4AKe0mQcKbI3z8hDg24jSrvRTgfiiORfqRK', 'admin');

INSERT INTO users (patient_id, active, password, user_name)
VALUES (1, true, '$2a$12$AnJJhf5PICQ25vSzOo4AKe0mQcKbI3z8hDg24jSrvRTgfiiORfqRK', 'user');

INSERT INTO users (patient_id, active, password, user_name)
VALUES (1, true, '$2a$12$AnJJhf5PICQ25vSzOo4AKe0mQcKbI3z8hDg24jSrvRTgfiiORfqRK', 'assistance');

/* Assign users to roles */
INSERT INTO user_role (role_id, user_id)
VALUES (1, 1);

INSERT INTO user_role (role_id, user_id)
VALUES (2, 2);

INSERT INTO user_role (role_id, user_id)
VALUES (3, 3);