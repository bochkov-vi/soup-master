merge into users."user" (id, username, password, authority, enabled) key(id)
values (1, 'admin', '$2a$10$x5NQY6v1OAJ8fNdKSoD5duIqoEP2xDOVivxEAoCb6HcHGOOtuCrju', 'ROLE_ADMIN', true);
