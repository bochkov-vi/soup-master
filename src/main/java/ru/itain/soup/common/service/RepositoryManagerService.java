package ru.itain.soup.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import ru.itain.soup.common.dto.system.System;
import ru.itain.soup.common.dto.users.*;
import ru.itain.soup.common.repository.system.SystemRepository;
import ru.itain.soup.common.repository.users.*;
import ru.itain.soup.tool.im_editor.dto.interactive_material.*;
import ru.itain.soup.tool.im_editor.repository.interactive_material.*;
import ru.itain.soup.tool.simulator_editor.dto.simulator.*;
import ru.itain.soup.tool.simulator_editor.repository.simulator.*;
import ru.itain.soup.tool.umm_editor.dto.umm.*;
import ru.itain.soup.tool.umm_editor.repository.umm.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.security.Roles.*;

@Service
public class RepositoryManagerService {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final Logger log = LoggerFactory.getLogger(RepositoryManagerService.class);

    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final PositionRepository positionRepository;
    private final RankRepository rankRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;
    private final LessonTemplateRepository lessonTemplateRepository;
    private final PlanRepository planRepository;
    private final MarkRepository markRepository;
    private final PresenceRepository presenceRepository;
    private final PresentationRepository presentationRepository;
    private final SimulatorRepository simulatorRepository;
    private final ModeRepository modeRepository;
    private final ScenarioRepository scenarioRepository;
    private final ArticleRepository articleRepository;
    private final TestRepository testRepository;
    private final SystemRepository systemRepository;
    private final MaterialTopicRepository materialTopicRepository;
    private final QuestionRepository questionRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final DisciplineRepository disciplineRepository;
    private final TopicRepository topicRepository;
    private final LessonBlockRepository lessonBlockRepository;
    private final RoleRepository roleRepository;
    private final FilterKeyRepository filterKeyRepository;
    private final FilterRepository filterRepository;
    private final StudentQuestionAnswerRepository studentQuestionAnswerRepository;
    private final ExecutionRepository executionRepository;
    private final MonitoringRepository monitoringRepository;
    private final SignalRepository signalRepository;
    private final PdfService pdfService;
    private final VideoService videoService;
    private final ArchiveService archiveService;

    public RepositoryManagerService(
            UserRepository userRepository,
            TutorRepository tutorRepository,
            PositionRepository positionRepository,
            RankRepository rankRepository,
            StudentGroupRepository studentGroupRepository,
            StudentRepository studentRepository,
            LessonRepository lessonRepository,
            LessonTemplateRepository lessonTemplateRepository,
            PlanRepository planRepository,
            MarkRepository markRepository,
            PresenceRepository presenceRepository, PresentationRepository presentationRepository,
            SimulatorRepository simulatorRepository,
            ModeRepository modeRepository, ScenarioRepository scenarioRepository, ArticleRepository articleRepository,
            TestRepository testRepository,
            SystemRepository systemRepository,
            MaterialTopicRepository materialTopicRepository,
            QuestionRepository questionRepository,
            QuestionAnswerRepository questionAnswerRepository,
            DisciplineRepository disciplineRepository,
            TopicRepository topicRepository,
            LessonBlockRepository lessonBlockRepository,
            RoleRepository roleRepository,
            FilterKeyRepository filterKeyRepository,
            FilterRepository filterRepository,
            StudentQuestionAnswerRepository studentQuestionAnswerRepository,
            ExecutionRepository executionRepository,
            MonitoringRepository monitoringRepository,
            SignalRepository signalRepository,
            PdfService pdfService,
            VideoService videoService,
            ArchiveService archiveService
    ) {
        this.userRepository = userRepository;
        this.tutorRepository = tutorRepository;
        this.positionRepository = positionRepository;
        this.rankRepository = rankRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.studentRepository = studentRepository;
        this.lessonRepository = lessonRepository;
        this.lessonTemplateRepository = lessonTemplateRepository;
        this.planRepository = planRepository;
        this.markRepository = markRepository;
        this.presenceRepository = presenceRepository;
        this.presentationRepository = presentationRepository;
        this.simulatorRepository = simulatorRepository;
        this.modeRepository = modeRepository;
        this.scenarioRepository = scenarioRepository;
        this.articleRepository = articleRepository;
        this.testRepository = testRepository;
        this.systemRepository = systemRepository;
        this.materialTopicRepository = materialTopicRepository;
        this.questionRepository = questionRepository;
        this.questionAnswerRepository = questionAnswerRepository;
        this.disciplineRepository = disciplineRepository;
        this.topicRepository = topicRepository;
        this.lessonBlockRepository = lessonBlockRepository;
        this.roleRepository = roleRepository;
        this.filterKeyRepository = filterKeyRepository;
        this.filterRepository = filterRepository;
        this.studentQuestionAnswerRepository = studentQuestionAnswerRepository;
        this.executionRepository = executionRepository;
        this.monitoringRepository = monitoringRepository;
        this.signalRepository = signalRepository;
        this.pdfService = pdfService;
        this.videoService = videoService;
        this.archiveService = archiveService;
    }

    private static LocalDateTime getLocalDateTime(String dateTime) {
        return LocalDateTime.from(dateTimeFormatter.parse(dateTime));
    }

    private static LocalDate getLocalDate(String s) {
        return LocalDate.from(dateFormatter.parse(s));
    }

    @Transactional
    public void init() {
        if (isInit()) {
            log.info("Already init. skip...");
            return;
        }
        Presentation presentation1 = presentationRepository.save(new Presentation("Презентация 1"));
        Presentation presentation2 = presentationRepository.save(new Presentation("Презентация 2"));
        Test test1 = createFirstTest();
        Test test2 = createSecondTest();

        createSimulatorTemplates();
        createSimulators();

        Position position = positionRepository.save(new Position("Зав. Кафедры"));
        Rank rank = rankRepository.save(new Rank("Генерал"));
        User tutorUser = userRepository.save(new User("tutor", "$2a$10$MQTvNQEPUoRweno6KohtmuNh9RQKpNCAo90IOSCYfwAdSBOLXi0/u", ROLE_TUTOR));
        User seniorTutorUser = userRepository.save(new User("secretary", "$2a$10$9EMzKt0Dj4XU7Z5CsdOQgOK2udQXweC3o31LeA9Zha3MHctyMSOGK", ROLE_SECRETARY));
        Tutor tutor = tutorRepository.save(new Tutor("Препод", "Преподов", "Преподович", position, rank, tutorUser));
        Tutor seniorTutor = tutorRepository.save(new Tutor("Секретарь", "Секретарев", "Секретаревич", position, rank, seniorTutorUser));

        StudentGroup group = studentGroupRepository.save(new StudentGroup("Группа 1"));
        LocalDate birthDate = getLocalDate("01.01.1990");
        LocalDate entryDate = getLocalDate("01.01.2010");
        User studentUser = userRepository.save(new User("student", "$2a$10$GPprKDinKjPih4Przf3e0.723LACYbSCdK5zHZo.MCBf4jzPNslGK", ROLE_STUDENT));
        Student student = studentRepository.save(new Student("Курсант", "Курсантов", "Курсантович", group, rank, birthDate, entryDate, "о. Сахалин", "", studentUser));

        LocalDateTime lessonBegin = getLocalDateTime("01.01.2019 10:11:12");
        Discipline discipline = disciplineRepository.save(new Discipline("Тактика подразделений инженерных войск"));
        Topic topic = topicRepository.save(new Topic("Последовательность и содержание работы командира", discipline));
        Plan plan = planRepository.save(new Plan("Занятие 1. Обязанности командира", topic));
        Lesson lesson1 = lessonRepository.save(new Lesson(
                "Обязанности командира. Типовое",
                plan,
                45,
                seniorTutor,
                Collections.singletonList(group),
                LocalDate.now(),
                null
        ));
        lesson1.setDefault(true);
        Lesson lesson2 = lessonRepository.save(new Lesson(
                "Обязанности командира. Преподов П.П.",
                plan,
                45 * 2,
                tutor,
                Collections.singletonList(group),
                LocalDate.now(),
                null
        ));

        Discipline discipline2 = disciplineRepository.save(new Discipline("Обучение начальным навыкам вождения"));
        Topic topicLes = topicRepository.save(new Topic("Техническое обслуживание", discipline2));
        Plan plan2 = planRepository.save(new Plan("Занятие 1. Устройство и работа двигателя", topicLes));
        Lesson lesson3 = lessonRepository.save(new Lesson(
                "Устройство и работа двигателя. Типовое",
                plan2,
                45,
                seniorTutor,
                Collections.singletonList(group),
                LocalDate.now(),
                null
        ));
        Lesson lesson4 = lessonRepository.save(new Lesson(
                "Устройство и работа двигателя. Преподов П.П.",
                plan2,
                45,
                tutor,
                Collections.singletonList(group),
                LocalDate.now(),
                null
        ));
        lesson3.setDefault(true);

//		ScheduledLesson scheduledLesson1 = scheduledLessonRepository.save(new ScheduledLesson(tutor, group, lessonBegin, lesson1));
//		ScheduledLesson scheduledLesson2 = scheduledLessonRepository.save(new ScheduledLesson(tutor, group, lessonBegin, lesson2));

//		Mark mark = markRepository.save(new Mark(student, scheduledLesson2, Mark.Type.M4));
//

        Article topic1 = articleRepository.save(new Article("Минирование"));
        Article topic1section1 = articleRepository.save(new Article(
                "Минное заграждение",
                topic1,
                new String(getResourceBytes("demo/article1.html"), StandardCharsets.UTF_8)
        ));
        pdfService.createPdf(topic1section1, getResourceInputStream("demo/article1.pdf"));

        Article article2 = new Article(
                "Морские минные заграждения",
                topic1section1,
                new String(getResourceBytes("demo/article2.html"), StandardCharsets.UTF_8));
        articleRepository.save(article2);
        pdfService.createPdf(article2, getResourceInputStream("demo/article2.pdf"));

        Article topic2 = articleRepository.save(new Article("Тема 2", null, null));
        Article topic2section1 = articleRepository.save(new Article("Раздел 1", topic2, null));
        Article topic2section2 = articleRepository.save(new Article("Раздел 2", topic2, null));
        Article topic2section3 = articleRepository.save(new Article("Раздел 3", topic2, null));

        Article topic3 = articleRepository.save(new Article("Тема 3", null, null));
        Article topic3section1 = articleRepository.save(new Article("Раздел 1", topic3, null));
        Article topic3section2 = articleRepository.save(new Article("Раздел 2", topic3, null));
        Article topic3section3 = articleRepository.save(new Article("Раздел 3", topic3, null));

        //-------------------------------------------------------------- Xorandif 30.03.2021
        lessonTemplateRepository.save(new LessonTemplate(
                "1) Тушение пожара в жилой квартире",
                new String(getResourceBytes("demo/shablon_1.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "2) Тушение пожара на объекте торговли в ночное время",
                new String(getResourceBytes("demo/shablon_2.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "3) Тушение пожара в помещении с электроустановками - "
                        + "электрическое оборудование обесточено",
                new String(getResourceBytes("demo/shablon_3.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "4) Тушение пожара в подвальном помещении общественного здания",
                new String(getResourceBytes("demo/shablon_4.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "5) Тушение пожара на складе товарно-материальных"
                        + "ценностей с наличием газовых баллонов",
                new String(getResourceBytes("demo/shablon_5.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "6) Тушение разлившегося бензина на площади 15 м2 в резервуарном парке "
                        + "при сливе топлива в подземный резервуар",
                new String(getResourceBytes("demo/shablon_6.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "7) Тушение разлившегося бензина на площади 10 м2 и автомобиля в результате аварии (ДТП)",
                new String(getResourceBytes("demo/shablon_7.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "8) Тушение пожара в жилой квартире на верхнем этаже здания",
                new String(getResourceBytes("demo/shablon_8.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "9) Тушение пажара холодильной установки в складском помещении объекта торговли",
                new String(getResourceBytes("demo/shablon_9.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "10) Тушение загорания на подвижном составе железнодорожного транспорта",
                new String(getResourceBytes("demo/shablon_10.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "11) Тушение загорания в номере гостиницы",
                new String(getResourceBytes("demo/shablon_11.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "12) Тушение загорания в здании с наличием пустотных деревянных конструкций",
                new String(getResourceBytes("demo/shablon_12.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "13) Тушение загорания в частном жилом доме в сельской местности",
                new String(getResourceBytes("demo/shablon_13.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "14) Тушение пожара в сауне",
                new String(getResourceBytes("demo/shablon_14.htm"), StandardCharsets.UTF_8))
        );
        //---------------------------
        lessonTemplateRepository.save(new LessonTemplate(
                "15) Тушение пожара в гаражах",
                new String(getResourceBytes("demo/shablon_15.htm"), StandardCharsets.UTF_8))
        );

        setInit(true);
    }

    private void createSimulatorTemplates() {
        List<Simulator> simulators = Arrays.asList(

                new Simulator("1", "Колесница. Учебно-боевая задача", "Колесница. Учебно-боевая задача", true),
                new Simulator("1", "МВТК-МЧС. Решение пожарно-тактической задачи \"Пожаротушение\"", "МВТК-МЧС. Решение пожарно-тактической задачи \"Пожаротушение\"", true),
        new Simulator("1", "1) Тушение пожара в зданиях", "(1) Тушение пожара в зданиях", true),
        new Simulator("2", "Процедурный тренажёр Р-168-100", "Процедурный тренажёр Р-168-100", false),
                new Simulator("3", "Процедурный тренажёр Р-168-25", "Процедурный тренажёр Р-168-25", false),
                new Simulator("4", "Процедурный тренажёр Азимут", "Процедурный тренажёр Азимут", false)
		);
        byte[] photo = getResourceBytes("demo/simulator1.jpg");
        simulators.get(1).setPhoto(photo);
        simulators.get(2).setPhoto(photo);
        simulators.get(3).setPhoto(photo);
        simulatorRepository.saveAll(simulators);

        List<Role> roles = Arrays.asList(
                new Role("1", "Командир оператор", "Командир оператор", simulators.get(0)),
                new Role("2", "Водитель", "Водитель", simulators.get(0)),
                new Role("3", "Командир", "Командир", simulators.get(0)),
                new Role("4", "Водитель-оператор", "Водитель-оператор", simulators.get(0))
        );
        roleRepository.saveAll(roles);

        List<FilterKey> filterKeys = Arrays.asList(
                new FilterKey("Время суток"),
                new FilterKey("Сезон"),
                new FilterKey("Тип подготовки")
        );
        filterKeyRepository.saveAll(filterKeys);

        List<Filter> filters = Arrays.asList(
                new Filter(filterKeys.get(0), "День", simulators.get(0)),
                new Filter(filterKeys.get(0), "Ночь", simulators.get(0)),
                new Filter(filterKeys.get(1), "Зима", simulators.get(0)),
                new Filter(filterKeys.get(1), "Лето", simulators.get(0)),
                new Filter(filterKeys.get(2), "Тактико-специальная подготовка", simulators.get(0)),
                new Filter(filterKeys.get(2), "Специальная подготовка", simulators.get(0))
        );
        filterRepository.saveAll(filters);

        List<Mode> modes = Arrays.asList(
                new Mode("1", "Редактирование", "Редактирование", simulators.get(0)),
                new Mode("2", "Выполнение", "Выполнение", simulators.get(0)),
                new Mode("3", "Пошаговое объяснение", "Пошаговое объяснение", simulators.get(1)),
                new Mode("3", "Пошаговое объяснение", "Пошаговое объяснение", simulators.get(2)),
                new Mode("3", "Пошаговое объяснение", "Пошаговое объяснение", simulators.get(3)),
                new Mode("4", "Тренировка", "Тренировка", simulators.get(1)),
                new Mode("4", "Тренировка", "Тренировка", simulators.get(2)),
                new Mode("4", "Тренировка", "Тренировка", simulators.get(3)),
                new Mode("5", "Зачёт", "Зачёт", simulators.get(1)),
                new Mode("5", "Зачёт", "Зачёт", simulators.get(2)),
                new Mode("5", "Зачёт", "Зачёт", simulators.get(3))
        );
        modeRepository.saveAll(modes);

        List<Scenario> scenarios = Arrays.asList(
                new Scenario("1", "Тушение пожара в квартире", "Тушение пожара в квартире", simulators.get(0)),
                new Scenario("2", "Тушение пожара в кафе", "Тушение пожара в кафе", simulators.get(0)),
                new Scenario("3", "Тушение пожара в сауне", "Тушение пожара в сауне", simulators.get(0)),
                new Scenario("4", "Тушение пожара в частном доме", "Тушение пожара в частном доме", simulators.get(0)),
                new Scenario("5", "Тушение пожара в гараже", "Тушение пожара в гараже", simulators.get(0)),
                new Scenario("6", "Тушение пожара на лестнице", "Тушение пожара на лестнице", simulators.get(0)),
                new Scenario("7", "Тушение пожара в поле", "Тушение пожара в поле", simulators.get(0)),

                new Scenario("8", "Тушение пожара на складе", "Тушение пожара на складе", simulators.get(0)),
                new Scenario("9", "Тушение пожара в офисе", "Тушение пожара в офисе", simulators.get(0)),
                new Scenario("10", "Тушение пожара в подвале", "Тушение пожара в подвале", simulators.get(0)),
                new Scenario("11", "Тушение пожара в электрощитовой", "Тушение пожара в электрощитовой", simulators.get(0)),
                new Scenario("12", "Тушение пожара при аварии на дороге", "Тушение пожара при аварии на дороге", simulators.get(0)),
                new Scenario("13", "Тушение пожара при разливе ГСМ", "Тушение пожара при разливе ГСМ", simulators.get(0)),
                new Scenario("14", "Тушение пожара на лесопилке", "Тушение пожара на лесопилке", simulators.get(0)),
                new Scenario("15", "Тушение пожара в контейнере", "Тушение пожара в контейнере", simulators.get(0))


                //new Scenario("1", "Настройка частоты радиостанции", "Настройка частоты радиостанции", simulators.get(1)),
                //new Scenario("2", "Включение радиостанции", "Включение радиостанции", simulators.get(2)),
                //new Scenario("3", "Получение навигационных данных", "Получение навигационных данных", simulators.get(3))
        );
        scenarioRepository.saveAll(scenarios);
    }

    private void createSimulators() {
        List<Simulator> simulatorTemplates = simulatorRepository.findAllByTemplateNullAndIsDeletedIsFalse();
        simulatorTemplates.forEach(simulatorTemplate -> {
            Simulator simulator = new Simulator(simulatorTemplate.getName(), simulatorTemplate);
            simulatorRepository.save(simulator);
            roleRepository.saveAll(roleRepository.findAllBySimulator(simulatorTemplate).stream()
                    .map(role -> new Role(role.getCode(), role.getName(), role.getDescription(), simulator))
                    .collect(Collectors.toList()));
            scenarioRepository.saveAll(scenarioRepository.findAllBySimulatorAndIsDeletedIsFalse(simulatorTemplate).stream()
                    .map(scenario -> new Scenario(scenario.getCode(), scenario.getName(), scenario.getDescription(), simulator))
                    .collect(Collectors.toList()));
            modeRepository.saveAll(modeRepository.findAllBySimulator(simulatorTemplate).stream()
                    .map(mode -> new Mode(mode.getCode(), mode.getName(), mode.getDescription(), simulator))
                    .collect(Collectors.toList()));
        });
    }

    private byte[] getResourceBytes(String resourceLocation) {
        try {
            InputStream inputStream = getResourceInputStream(resourceLocation);
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            log.error("Can't load resource", e);
        }
        return null;
    }

    private InputStream getResourceInputStream(String resourceLocation) {
        try {
            Resource resource = new ClassPathResource(resourceLocation);
            return resource.getInputStream();
        } catch (IOException e) {
            log.error("Can't load resource", e);
        }
        return null;
    }

    //===================================== createFirstTest
    private Test createFirstTest() {
        MaterialTopic topic = materialTopicRepository.save(new MaterialTopic("История"));
        Test test = testRepository.save(new Test("Тест №1", null, topic));
        Question question1 = questionRepository.save(new Question("Вопрос №1",
                "Что было основным в политике «военного коммунизма»?",
                test));
        QuestionAnswer answer1_1 = new QuestionAnswer("передача всех помещичьих земель крестьянам", false, question1);
        QuestionAnswer answer1_2 = new QuestionAnswer("запрет всех форм частной собственности", false, question1);
        QuestionAnswer answer1_3 = new QuestionAnswer("национализация всех средств производства, внедрение централизованного управления и политической диктатуры большевиков", true, question1);
        List<QuestionAnswer> answers = Arrays.asList(answer1_1, answer1_2, answer1_3);
        questionAnswerRepository.saveAll(answers);

        Question question2 = questionRepository.save(new Question(
                "Вопрос №2",
                "В каком году состоялось подписание договора о запрещении испытаний ядерного оружия в атмосфере, космическом пространстве и под водой?",
                test));
        QuestionAnswer answer2_1 = new QuestionAnswer("в 1963 г.", true, question2);
        QuestionAnswer answer2_2 = new QuestionAnswer("в 1958 г.", false, question2);
        QuestionAnswer answer2_3 = new QuestionAnswer("в 1972 г.", false, question2);
        QuestionAnswer answer2_4 = new QuestionAnswer("в 1969 г.", false, question2);
        answers = Arrays.asList(answer2_1, answer2_2, answer2_3, answer2_4);
        questionAnswerRepository.saveAll(answers);

        Question question3 = questionRepository.save(new Question("Вопрос №3",
                "После смерти Андропова пост Генерального секретаря ЦК КПСС занял",
                test));
        QuestionAnswer answer3_1 = new QuestionAnswer("Е.К.Лигачев", false, question3);
        QuestionAnswer answer3_2 = new QuestionAnswer("К.У.Черненко", true, question3);
        QuestionAnswer answer3_3 = new QuestionAnswer("М.С.Горбачев", false, question3);
        QuestionAnswer answer3_4 = new QuestionAnswer("Д.Ф.Устинов", false, question3);
        answers = Arrays.asList(answer3_1, answer3_2, answer3_3, answer3_4);
        questionAnswerRepository.saveAll(answers);

        Question question4 = questionRepository.save(new Question("Вопрос №4",
                "В каком году Россия вошла в Совет Европы?",
                test));
        QuestionAnswer answer4_1 = new QuestionAnswer("в 1991 г", false, question4);
        QuestionAnswer answer4_2 = new QuestionAnswer("в 1993 г.", false, question4);
        QuestionAnswer answer4_3 = new QuestionAnswer("в 1996 г.", true, question4);
        QuestionAnswer answer4_4 = new QuestionAnswer("в 2001 г.", false, question4);
        answers = Arrays.asList(answer4_1, answer4_2, answer4_3, answer4_4);
        questionAnswerRepository.saveAll(answers);

        Question question5 = questionRepository.save(new Question("Вопрос №5",
                "В 1918-1921 годах советское правительство проводило в жизнь…",
                test));
        QuestionAnswer answer5_1 = new QuestionAnswer("НЭП", false, question5);
        QuestionAnswer answer5_2 = new QuestionAnswer("политику военного коммунизма", true, question5);
        QuestionAnswer answer5_3 = new QuestionAnswer("план индустриализации", false, question5);
        QuestionAnswer answer5_4 = new QuestionAnswer("массовую коллективизацию", false, question5);
        answers = Arrays.asList(answer5_1, answer5_2, answer5_3, answer5_4);
        questionAnswerRepository.saveAll(answers);
        return test;
    }

    private Test createSecondTest() {
        MaterialTopic topic = materialTopicRepository.save(new MaterialTopic("Астрономия"));
        Test test = testRepository.save(new Test("Тест №1", null, topic));

        Question question1 = questionRepository.save(new Question("Вопрос №1", "Шестая планета от Солнца – это…", test));
        QuestionAnswer answer1_1 = new QuestionAnswer("Сатурн.", true, question1);
        QuestionAnswer answer1_2 = new QuestionAnswer("Юпитер.", false, question1);
        QuestionAnswer answer1_3 = new QuestionAnswer("Уран", false, question1);
        QuestionAnswer answer1_4 = new QuestionAnswer("Марс", false, question1);
        List<QuestionAnswer> answers = Arrays.asList(answer1_1, answer1_2, answer1_3, answer1_4);
        questionAnswerRepository.saveAll(answers);

        Question question2 = questionRepository.save(new Question("Вопрос №2", "Какие звёзды имеют наибольшую температуру поверхности?", test));
        QuestionAnswer answer2_1 = new QuestionAnswer("Голубые карлики", true, question2);
        QuestionAnswer answer2_2 = new QuestionAnswer("Жёлтые звёзды", false, question2);
        QuestionAnswer answer2_3 = new QuestionAnswer("Красные гиганты", false, question2);
        answers = Arrays.asList(answer2_1, answer2_2, answer2_3);
        questionAnswerRepository.saveAll(answers);

        Question question3 = questionRepository.save(new Question("Вопрос №3", "Ближайшая к Земле звезда (после Солнца) это –", test));
        QuestionAnswer answer3_1 = new QuestionAnswer("Проксима Центавра", true, question3);
        QuestionAnswer answer3_2 = new QuestionAnswer("Звезда Барнарда", false, question3);
        QuestionAnswer answer3_3 = new QuestionAnswer("Сириус", false, question3);
        QuestionAnswer answer3_4 = new QuestionAnswer("Альтаир", false, question3);
        answers = Arrays.asList(answer3_1, answer3_2, answer3_3, answer3_4);
        questionAnswerRepository.saveAll(answers);

        Question question4 = questionRepository.save(new Question("Вопрос №4", "Сколько времени свет от Солнца идет до Земли?", test));
        QuestionAnswer answer4_1 = new QuestionAnswer("Примерно 8 мин", true, question4);
        QuestionAnswer answer4_2 = new QuestionAnswer("Приходит мгновенно", false, question4);
        QuestionAnswer answer4_3 = new QuestionAnswer("1световой год", false, question4);
        QuestionAnswer answer4_4 = new QuestionAnswer("Около суток", false, question4);
        answers = Arrays.asList(answer4_1, answer4_2, answer4_3, answer4_4);
        questionAnswerRepository.saveAll(answers);

        Question question5 = questionRepository.save(new Question("Вопрос №5", "К какому классу звезд относится Бетельгейзе?", test));
        QuestionAnswer answer5_1 = new QuestionAnswer("Сверхгигант", true, question5);
        QuestionAnswer answer5_2 = new QuestionAnswer("Желтый карлик", false, question5);
        QuestionAnswer answer5_3 = new QuestionAnswer("Белый карлик", false, question5);
        QuestionAnswer answer5_4 = new QuestionAnswer("Оранжевый гигант", false, question5);
        answers = Arrays.asList(answer5_1, answer5_2, answer5_3, answer5_4);
        questionAnswerRepository.saveAll(answers);

        return test;
    }

    public boolean isInit() {
        System isInit = systemRepository.findByKey("IsInit");
        return isInit != null && Boolean.TRUE.equals(Boolean.valueOf(isInit.getValue()));
    }

    public void setInit(boolean init) {
        System isInit = systemRepository.findByKey("IsInit");
        if (isInit == null) {
            isInit = new System("IsInit", Boolean.toString(init));
        } else {
            isInit.setValue(Boolean.toString(init));
        }
        systemRepository.save(isInit);
    }

    @Transactional
    public void cleanup() {
        if (!isInit()) {
            log.info("Already clean. skip...");
            return;
        }
        monitoringRepository.deleteAll();
        signalRepository.deleteAll();
        executionRepository.deleteAll();
        studentQuestionAnswerRepository.deleteAll();
        presenceRepository.deleteAll();
        markRepository.deleteAll();
        planRepository.deleteAll();
        lessonBlockRepository.deleteAll();
        lessonRepository.deleteAll();
        lessonTemplateRepository.deleteAll();
        studentRepository.deleteAll();
        filterRepository.deleteAll();
        filterKeyRepository.deleteAll();
        roleRepository.deleteAll();
        modeRepository.deleteAll();
        scenarioRepository.deleteAll();
        simulatorRepository.deleteAll();
        studentGroupRepository.deleteAll();
        tutorRepository.deleteAll();
        positionRepository.deleteAll();
        rankRepository.deleteAll();
        presentationRepository.deleteAll();
        articleRepository.deleteAll();
        questionRepository.deleteAll();
        questionAnswerRepository.deleteAll();
        testRepository.deleteAll();
        materialTopicRepository.deleteAll();
        topicRepository.deleteAll();
        disciplineRepository.deleteAll();

        final Iterable<User> users = userRepository.findAll();
        userRepository.deleteAll(
                StreamSupport.stream(users.spliterator(), false)
                        .filter(it -> !"admin".equals(it.getUsername()))
                        .collect(Collectors.toList())
        );

        systemRepository.deleteAll();

        pdfService.cleanup();
        videoService.cleanup();
        archiveService.cleanup();

        setInit(false);
    }
}
