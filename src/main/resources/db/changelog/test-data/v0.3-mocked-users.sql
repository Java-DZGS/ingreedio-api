--liquibase formatted sql

--changeset migoox:add-mocked-moderator
INSERT INTO users (email, display_name) VALUES ('moderator@mod.com', 'Moderator');

INSERT INTO auth_info
    (user_id, username, password)
VALUES ((SELECT id FROM users WHERE email = 'moderator@mod.com'), 'mod', '$2a$10$Z4psUYfZRjI.SCrOWmS7m.M1KiBODdWJ89c7z3OiHd39RUY3/0VOW');

INSERT INTO auth_infos_roles
    (auth_info_id, role_id)
VALUES (
    (SELECT id FROM auth_info WHERE username = 'mod'),
    (SELECT id FROM roles WHERE name = 'MODERATOR')
);

--changeset migoox:add-mocked-user
INSERT INTO users (email, display_name) VALUES ('user@us.com', 'User');

INSERT INTO auth_info
    (user_id, username, password)
VALUES ((SELECT id FROM users WHERE email = 'user@us.com'), 'user', '$2a$10$GqVelfiBpvWFPizure6aJebeCw8MCGh2Ss51jgJOyEiarcFmqEWJC');

INSERT INTO auth_infos_roles
    (auth_info_id, role_id)
VALUES (
    (SELECT id FROM auth_info WHERE username = 'user'),
    (SELECT id FROM roles WHERE name = 'USER')
);