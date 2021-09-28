merge into users."user" (id, username, password, authority, enabled) key (id)
    values (1, 'admin', '$2a$10$x5NQY6v1OAJ8fNdKSoD5duIqoEP2xDOVivxEAoCb6HcHGOOtuCrju', 'ROLE_ADMIN', true);
merge INTO syllabus.cycle (id, name) key (id)
    VALUES (1, '1 семестр');
merge INTO syllabus.cycle (id, name) key (id)
    VALUES (2, '2 семестр');
merge INTO syllabus.cycle (id, name) key (id)
    VALUES (3, '3 семестр');
merge INTO syllabus.cycle (id, name) key (id)
    VALUES (4, '4 семестр');
merge INTO syllabus.cycle (id, name) key (id)
    VALUES (5, '5 семестр');
merge INTO syllabus.cycle (id, name) key (id)
    VALUES (6, '6 семестр');
merge INTO syllabus.cycle (id, name) key (id)
    VALUES (7, '7 семестр');
merge INTO syllabus.cycle (id, name) key (id)
    VALUES (8, '8 семестр');
merge INTO syllabus.cycle (id, name) key (id)
    VALUES (9, '9 семестр');
merge INTO syllabus.cycle (id, name) key (id)
    VALUES (10, '10 семестр');
