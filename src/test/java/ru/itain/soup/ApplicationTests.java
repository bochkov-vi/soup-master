package ru.itain.soup;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.tool.im_editor.dto.interactive_material.MaterialTopic;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Presentation;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Question;
import ru.itain.soup.tool.im_editor.dto.interactive_material.QuestionAnswer;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonTemplate;
import ru.itain.soup.tool.umm_editor.dto.umm.Mark;
import ru.itain.soup.tool.umm_editor.dto.umm.Plan;
import ru.itain.soup.tool.umm_editor.dto.umm.Topic;
import ru.itain.soup.common.dto.users.Position;
import ru.itain.soup.common.dto.users.Rank;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.tool.im_editor.repository.interactive_material.ArticleRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.MaterialTopicRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.PresentationRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionAnswerRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.TestRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.SimulatorRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonBlockRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonTemplateRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.MarkRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.PlanRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.TopicRepository;
import ru.itain.soup.common.repository.users.PositionRepository;
import ru.itain.soup.common.repository.users.RankRepository;
import ru.itain.soup.common.repository.users.StudentGroupRepository;
import ru.itain.soup.common.repository.users.StudentRepository;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.common.repository.users.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.itain.soup.common.security.Roles.ROLE_STUDENT;
import static ru.itain.soup.common.security.Roles.ROLE_TUTOR;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
	private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TutorRepository tutorRepository;
	@Autowired
	private PositionRepository positionRepository;
	@Autowired
	private RankRepository rankRepository;
	@Autowired
	private StudentGroupRepository studentGroupRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private LessonRepository lessonRepository;
	@Autowired
	private PlanRepository planRepository;
	@Autowired
	private MarkRepository markRepository;
	@Autowired
	private PresentationRepository presentationRepository;
	@Autowired
	private SimulatorRepository simulatorRepository;
	@Autowired
	private TestRepository testRepository;
	@Autowired
	private ArticleRepository articleRepository;
	@Autowired
	private LessonTemplateRepository lessonTemplateRepository;
	@Autowired
	private QuestionRepository questionRepository;
	@Autowired
	private QuestionAnswerRepository questionAnswerRepository;
	@Autowired
	private DisciplineRepository disciplineRepository;
	@Autowired
	private TopicRepository topicRepository;
	@Autowired
	private MaterialTopicRepository materialTopicRepository;
	@Autowired
	private LessonBlockRepository lessonBlockRepository;

	private static LocalDateTime getLocalDateTime(String dateTime) {
		return LocalDateTime.from(dateTimeFormatter.parse(dateTime));
	}

	private static LocalDate getLocalDate(String s) {
		return LocalDate.from(dateFormatter.parse(s));
	}

	@Test
	public void repositoriesTest() {
		Presentation presentation1 = presentationRepository.save(new Presentation("Презентация 1"));
		Presentation presentation2 = presentationRepository.save(new Presentation("Презентация 2"));
		ru.itain.soup.tool.im_editor.dto.interactive_material.Test test1 = testRepository.save(new ru.itain.soup.tool.im_editor.dto.interactive_material.Test("Тест 1"));
		ru.itain.soup.tool.im_editor.dto.interactive_material.Test test2 = testRepository.save(new ru.itain.soup.tool.im_editor.dto.interactive_material.Test("Тест 2"));
		Simulator simulator1 = simulatorRepository.save(new Simulator("Интерактивный тренажер 1"));
		Simulator simulator2 = simulatorRepository.save(new Simulator("Интерактивный тренажер 2"));

		Position position = positionRepository.save(new Position("Зав. Кафедры"));
		Rank rank = rankRepository.save(new Rank("Генерал"));
		User tutorUser = userRepository.save(new User("tutor", "123456", ROLE_TUTOR));
		Tutor tutor = tutorRepository.save(new Tutor("Препод", "Преподов", "Преподович", position, rank, tutorUser));

		StudentGroup group = studentGroupRepository.save(new StudentGroup("Группа 1"));
		LocalDate birthDate = getLocalDate("01.01.1990");
		LocalDate entryDate = getLocalDate("01.01.2010");
		User studentUser = userRepository.save(new User("student", "123456", ROLE_STUDENT));
		Student student = studentRepository.save(new Student("Курсант", "Курсантов", "Курсантович", group, rank, birthDate, entryDate, "о. Сахалин", "", studentUser));

		LocalDateTime lessonBegin = getLocalDateTime("01.01.2019 10:11:12");
		Discipline discipline = disciplineRepository.save(new Discipline("Тактика подразделений инженерных войск"));
		Topic topic = topicRepository.save(new Topic("Тема 1", discipline));
		Plan lessonPlan = planRepository.save(new Plan("Занятие 1", topic));
		Lesson lesson1 = lessonRepository.save(new Lesson(
				"Лекция 1",
				lessonPlan,
				45,
				tutor,
				Collections.singletonList(group),
				LocalDate.now(),
				null
		));
		Lesson lesson2 = lessonRepository.save(new Lesson(
				"Практическое занятие 1",
				lessonPlan,
				45 * 2,
				tutor,
				Collections.singletonList(group),
				LocalDate.now(),
				null
		));

		Mark mark = markRepository.save(new Mark(student, lesson1, Mark.Type.M4));

		Article article1_1 = articleRepository.save(new Article("Статья 1.1"));
		Article article1_2 = articleRepository.save(new Article("Статья 1.2", article1_1, "Некоторый контент"));
		Article article1_3 = articleRepository.save(new Article("Статья 1.3", article1_1, "Некоторый контент"));

		Article article2_1 = articleRepository.save(new Article("Статья 2.1"));
		Article article2_2 = articleRepository.save(new Article("Статья 2.2", article2_1, "Некоторый контент"));
		Article article2_3 = articleRepository.save(new Article("Статья 2.3", article2_1, "Некоторый контент"));

		articleRepository.delete(article2_3);
		articleRepository.delete(article1_1);
		articleRepository.delete(article2_1);

		Lesson lessonFetched = lessonRepository.findByIdFetched(lesson1.getId());
		Assert.assertNotNull(lessonFetched);

		LessonBlock lessonBlock = lessonBlockRepository.save(new LessonBlock("test", lesson1));
		lessonBlockRepository.delete(lessonBlock);
		markRepository.deleteAll();
		lessonRepository.delete(lesson1);
		lessonRepository.delete(lesson2);
		planRepository.delete(lessonPlan);
	}

	@Test
	public void lessonRepositoryTest() {
		LessonTemplate lessonTemplate1 = new LessonTemplate("Шаблон документа 1", "Некоторый контент");
		lessonTemplateRepository.save(lessonTemplate1);
		LessonTemplate lessonTemplate2 = new LessonTemplate("Шаблон документа 2", "Некоторый контент");
		lessonTemplateRepository.save(lessonTemplate2);
		LessonTemplate lessonTemplate3 = new LessonTemplate("Шаблон документа 3", null);
		lessonTemplateRepository.save(lessonTemplate3);
	}

	@Test
	@Transactional
	public void testRepositoryTest() {
		MaterialTopic topic = materialTopicRepository.save(new MaterialTopic("История"));
		ru.itain.soup.tool.im_editor.dto.interactive_material.Test test = testRepository.save(new ru.itain.soup.tool.im_editor.dto.interactive_material.Test("Тест №1", null, topic));
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

		Optional<ru.itain.soup.tool.im_editor.dto.interactive_material.Test> byId = testRepository.findById(test.getId());
		ru.itain.soup.tool.im_editor.dto.interactive_material.Test test1 = byId.get();
		List<Question> allByTest = questionRepository.findAllByTest(test1);
		Question question = allByTest.get(0);
		testRepository.delete(test);
	}
}
