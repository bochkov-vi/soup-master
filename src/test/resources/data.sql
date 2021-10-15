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


MERGE INTO users.speciality(id, name) key (id)
VALUES (101, 'Спутниковая связь');

MERGE INTO syllabus.syllabus_category (id, name) key (id)
VALUES (101, 'ГУМАНИТАРНЫЙ, СОЦИАЛЬНО-ЭКОНОМИЧЕСКИЙ ЦИКЛ');

MERGE INTO umm.discipline (id, name) key (id)
VALUES (1001, 'Иностранный язык'),
       (1002, 'История'),
       (1003, 'Философия'),
       (1004, 'Правоведение'),
       (1005, 'Военная история'),
       (1006, 'Психология и педагогика'),
       (1007, 'Основы информационного обеспечения военной деятельности и связей с общественностью'),
       (1008, 'Организация работы с личным составом ВС РФ и МПО деятельности войск (сил)'),
       (1009, 'Политология'),
       (1010, 'Социология');

delete from syllabus.syllabus where id between 10001 and 10010;

merge INTO syllabus.syllabus (id, syllabus_category_id, speciality_id, index, discipline_id, base, fertile_units,
                               undefining_parameter,
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
    key(id)
VALUES (10001, 101, 101, 'Ц.1.Б.1', 1001, true, 10, 1, 0, 0, 0, 0, 0, 194, 0, 0, 0, 0, 10, 12, 1.5, 36, 18, 2, 48, 24,
        1.5,
        36, 18, 2, 48, 24, 3, 54, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 2.4, 1.3, 0),
       (10002, 101, 101, 'Ц.1.Б.2', 1002, true, 3, 0, 36, 30, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 1.5, 36, 18, 1.5, 36, 18, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0),
       (10003, 101, 101, 'Ц.1.Б.3', 1003, true, 3, 0, 44, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1.5, 36, 18, 1.5, 36, 18,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0),
       (10004, 101, 101, 'Ц.1.Б.4', 1004, true, 5, 1, 44, 30, 0, 0, 0, 18, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 48, 24, 3, 54, 54, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0),
       (10005, 101, 101, 'Ц.1.В.1', 1005, false, 5, 1, 50, 44, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 2,48, 24, 3, 54, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0),
       (10006, 101, 101, 'Ц.1.В.2', 1006, false, 6, 1, 30, 42, 0, 0, 0, 36, 0, 0, 6, 0, 6, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 2, 48, 24, 4,78, 66, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0),
       (10007, 101, 101, 'Ц.1.В.3', 1007, false, 2, 0, 12, 14, 0, 0, 0, 18, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0,0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1.25, 30, 15, 0.75, 18, 9, 0, 10, 0, 0)
;
