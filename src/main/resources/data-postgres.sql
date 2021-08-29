-- Админ
INSERT INTO
	users."user" (id, username, password, authority, enabled)
VALUES
	(1, 'admin', '$2a$10$x5NQY6v1OAJ8fNdKSoD5duIqoEP2xDOVivxEAoCb6HcHGOOtuCrju', 'ROLE_ADMIN', TRUE)
ON CONFLICT DO NOTHING;

--Специальности
INSERT INTO
	users."speciality" (id, name)
VALUES
(1, 'Применение инженерных подразделений и эксплуатация средств инженерного вооружения'),
(2, 'Применение инженерных подразделений РВСН и эксплуатация средств инженерного вооружения'),
(3, 'Применение понтонно-мостовых, переправочно-десантных подразделений и эксплуатация средств инженерного вооружения')
ON CONFLICT DO NOTHING;

--Типы занятий
INSERT INTO
	umm."lesson_type" (id, name, code)
VALUES
(1, 'Групповое', 'GROUP'),
(2, 'Практическое','PRACTICE'),
(3, 'Лекционное','LECTURE'),
(4, 'Зачетное','TEST')
ON CONFLICT DO NOTHING;

INSERT INTO syllabus.cycle (id, name)
VALUES (1, '1 семестр') ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (2, '2 семестр')ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (3, '3 семестр')ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (4, '4 семестр')ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (5, '5 семестр')ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (6, '6 семестр')ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (7, '7 семестр')ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (8, '8 семестр')ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (9, '9 семестр')ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (10, '10 семестр')ON CONFLICT DO NOTHING;
