-- Админ
INSERT INTO users."user" (id, username, password, authority, enabled)
VALUES (1, 'admin', '$2a$10$x5NQY6v1OAJ8fNdKSoD5duIqoEP2xDOVivxEAoCb6HcHGOOtuCrju', 'ROLE_ADMIN', TRUE)
ON CONFLICT DO NOTHING;

--Специальности
INSERT INTO users."speciality" (id, name)
VALUES (1, 'Применение инженерных подразделений и эксплуатация средств инженерного вооружения'),
       (2, 'Применение инженерных подразделений РВСН и эксплуатация средств инженерного вооружения'),
       (3,
        'Применение понтонно-мостовых, переправочно-десантных подразделений и эксплуатация средств инженерного вооружения')
ON CONFLICT DO NOTHING;

--Типы занятий
INSERT INTO umm."lesson_type" (id, name, code)
VALUES (1, 'Групповое', 'GROUP'),
       (2, 'Практическое', 'PRACTICE'),
       (3, 'Лекционное', 'LECTURE'),
       (4, 'Зачетное', 'TEST')
ON CONFLICT DO NOTHING;

INSERT INTO syllabus.cycle (id, name)
VALUES (1, '1 семестр')
ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (2, '2 семестр')
ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (3, '3 семестр')
ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (4, '4 семестр')
ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (5, '5 семестр')
ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (6, '6 семестр')
ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (7, '7 семестр')
ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (8, '8 семестр')
ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (9, '9 семестр')
ON CONFLICT DO NOTHING;
INSERT INTO syllabus.cycle (id, name)
VALUES (10, '10 семестр')
ON CONFLICT(id) DO NOTHING;

INSERT INTO users.speciality(id, name)
VALUES (101, 'Спутниковая связь')
on conflict(id) do update set id=excluded.id,
                              name=excluded.name;
INSERT INTO umm.discipline (id, name)
VALUES (1001, 'Иностранный язык'),
       (1002, 'История')
on conflict (id) do update set id=excluded.id,
                               name=excluded.name;


INSERT INTO syllabus.syllabus (id, speciality_id, index, discipline_id, base, fertile_units, undefining_parameter,
                               lectures,
                               seminars, group_exercises, group_lessons, laboratory_works, practical_lessons,
                               special_lessons, course_works, conferences, practices, tests, credit,
                               year1_cycle1_intensity, year1_cycle1_training_hours, year1_cycle1_self_study_hours,
                               year1_cycle2_intensity, year1_cycle2_training_hours, year1_cycle2_self_study_hours,
                               year2_cycle1_intensity, year2_cycle1_training_hours, year2_cycle1_self_study_hours,
                               year2_cycle2_intensity, year2_cycle2_training_hours, year2_cycle2_self_study_hours,
                               year3_cycle1_intensity, year3_cycle1_training_hours, year3_cycle1_self_study_hours,
                               year3_cycle2_intensity, year3_cycle2_training_hours, year3_cycle2_self_study_hours,
                               year4_cycle1_intensity, year4_cycle1_training_hours, year4_cycle1_self_study_hours,
                               year4_cycle2_intensity, year4_cycle2_training_hours, year4_cycle2_self_study_hours,
                               year5_cycle1_intensity, year5_cycle1_training_hours, year5_cycle1_self_study_hours,
                               year5_cycle2_intensity, year5_cycle2_training_hours, year5_cycle2_self_study_hours,
                               exam_control,
                               graded_credit_control, pass_without_assessment_control, course_work_control)
VALUES (10001, 101, 'Ц.1.Б.1', 1001, true, 10, 1, 0, 0, 0, 0, 0, 194, 0, 0, 0, 0, 10, 12, 1.5, 36, 18, 2, 48, 24, 1.5,
        36, 18, 2, 48, 24, 3, 54, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 2.4, 1.3, 0),
       (10002, 101, 'Ц.1.Б.2', 1002, true, 3, 0, 36,30, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 1.5, 36, 18, 1.5, 36, 18, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0)
on conflict do nothing;
