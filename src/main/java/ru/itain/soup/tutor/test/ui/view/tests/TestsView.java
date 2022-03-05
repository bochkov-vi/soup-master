package ru.itain.soup.tutor.test.ui.view.tests;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.common.ui.component.SoupBaseDialog;
import ru.itain.soup.common.ui.component.SoupElementWithDepartmentEditDialog;
import ru.itain.soup.common.ui.component.SoupTreeGrid;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.common.ui.view.tutor.im.InteractiveMaterialsView;
import ru.itain.soup.syllabus.dto.repository.DepartmentRepository;
import ru.itain.soup.tool.im_editor.dto.interactive_material.MaterialTopic;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Question;
import ru.itain.soup.tool.im_editor.dto.interactive_material.QuestionAnswer;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.im_editor.repository.interactive_material.MaterialTopicRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionAnswerRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.TestRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.ui.view.tutor.CommonView.PAGE_TITLE;

@Route(value = TestsView.ROUTE, layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class TestsView extends InteractiveMaterialsView {
    public static final String ROUTE = "tutor/im/tests";
    protected final TutorRepository tutorRepository;
    protected final DepartmentRepository departmentRepository;
    protected final ComboBox<MaterialTopic> topics = new ComboBox<>();
    protected final TestRepository testRepository;
    protected final MaterialTopicRepository materialTopicRepository;
    protected final QuestionRepository questionRepository;
    protected final QuestionAnswerRepository questionAnswerRepository;
    protected final HorizontalLayout buttons = new HorizontalLayout();
    protected final HorizontalLayout editButtons = new HorizontalLayout();
    private final Map<Long, TestItem> allItems = new HashMap<>();
    private final Set<Long> expandedTests = new HashSet<>();
    private final VerticalLayout content;
    protected TestContentLayout testsInfoLayout;
    protected EditTestLayout editTestLayout;
    protected List<MaterialTopic> allTopics;
    protected Label testName = new Label();
    private SoupTreeGrid<TestItem> tree;
    private TreeData<TestItem> testTreeData;

    public TestsView(
            TestRepository testRepository,
            MaterialTopicRepository materialTopicRepository,
            QuestionRepository questionRepository,
            QuestionAnswerRepository questionAnswerRepository,
            TutorRepository tutorRepository,
            DepartmentRepository departmentRepository
    ) {
        this.departmentRepository = departmentRepository;
        this.tutorRepository = tutorRepository;
        this.testRepository = testRepository;
        this.materialTopicRepository = materialTopicRepository;
        this.questionRepository = questionRepository;
        this.questionAnswerRepository = questionAnswerRepository;
        content = new VerticalLayout();
        content.setPadding(false);
        content.setSizeFull();
        content.getStyle().set("overflow", "auto");
        testName.setWidthFull();
        infoPanel.add(testName);
        tabs.setSelectedTab(testsTab);
        Button delete = new Button("Удалить");
        Button copy = new Button("Копировать");
        Button edit = new Button("Редактировать");
        allTopics = StreamSupport
                .stream(materialTopicRepository.findAll(tutorRepository.getCurrentDepartment()).spliterator(), false)
                .collect(Collectors.toList());
        initTestTree();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        Button add = new Button("+Тестовое задание");
        add.addClickListener(e -> {
            openEditTest(null);
        });
        Button addTopic = new Button("+Тема");
        addTopic.addClickListener(e -> openAddTopicDialog());
        layout.add(addTopic);
        layout.add(add);

        center.add(content);

        buttons.add(delete);
        buttons.add(copy);
        buttons.add(edit);
        infoPanel.add(buttons);
        infoPanel.add(editButtons);
        left.add(layout);
        delete.addClickListener(e -> {
            VisualEntity selectedItem = getSelectedItem();
            if (selectedItem instanceof Test) {
                Test test = (Test) selectedItem;
                SoupBaseDialog dialog = new SoupBaseDialog(
                        click -> {
                            testRepository.delete(test);
                            updateTree();
                            buttons.setVisible(false);
                            testsInfoLayout.setVisible(false);
                        },
                        SoupBaseDialog.CONFIRM,
                        "Удалить тест \"" + test.getName() + "\" из темы \"" + test.getTopic().asString() + "\"?"
                );
                dialog.open();
            }
        });
        edit.addClickListener(e -> {
            VisualEntity selectedItem = getSelectedItem();
            if (selectedItem instanceof Test) {
                Test test = (Test) selectedItem;
                openEditTest(test);
            }
        });

        copy.addClickListener(e -> {
            VisualEntity selectedItem = getSelectedItem();
            if (selectedItem instanceof Test) {
                Test test = (Test) selectedItem;
                Test copyTest = new Test();
                copyTest.setTopic(test.getTopic());
                copyTest.setDurationMinutes(test.getDurationMinutes());
                copyTest.setName(test.getName() + "_копия");
                testRepository.save(copyTest);
                List<Question> sourceQuestions = questionRepository.findAllByTest(test);
                List<Question> copiedQuestions = sourceQuestions.stream().map(sourceQuestion -> {
                            Question question = new Question();
                            question.setTest(copyTest);
                            question.setImage(sourceQuestion.getImage());
                            question.setText(sourceQuestion.getText());
                            question.setName(sourceQuestion.getName());
                            questionRepository.save(question);
                            List<QuestionAnswer> sourceAnswers = questionAnswerRepository
                                    .findAllByQuestion(sourceQuestion)
                                    .stream()
                                    .sorted(Comparator.comparingLong(QuestionAnswer::getId))
                                    .collect(Collectors.toList());
                            sourceAnswers.forEach(sourceAnswer -> {
                                QuestionAnswer answer = new QuestionAnswer();
                                answer.setQuestion(question);
                                answer.setName(sourceAnswer.getName());
                                answer.setCorrect(sourceAnswer.isCorrect());
                                questionAnswerRepository.save(answer);
                            });
                            return question;
                        })
                        .collect(Collectors.toList());
                openEditTest(copyTest, copiedQuestions, true);
            }
        });
    }

    private VisualEntity getSelectedItem() {
        Iterator<TestItem> it = tree.getSelectedItems().iterator();
        if (it.hasNext()) {
            return it.next().getEntity();
        }
        return null;
    }

    private void openEditTest(Test test) {
        openEditTest(test, null, false);
    }

    private void openEditTest(Test test, List<Question> questions, boolean isCopy) {
        content.removeAll();
        editTestLayout = new EditTestLayout(testRepository, materialTopicRepository, questionRepository, questionAnswerRepository, editButtons);
        content.add(editTestLayout);
        buttons.setVisible(false);
        testName.setText("РЕДАКТОР ТЕСТОВЫХ ЗАДАНИЙ");
        editTestLayout.show(test, questions, topics, click -> updateTree(), click -> {
            if (isCopy) {
                questions.forEach(it -> {
                    List<QuestionAnswer> allByQuestion = questionAnswerRepository.findAllByQuestion(it);
                    questionAnswerRepository.deleteAll(allByQuestion);
                });
                questionRepository.deleteAll(questions);
                testRepository.delete(test);
            }
            editTestLayout.close();
            updateTree();
        });
    }

    private void openAddTopicDialog() {
        new SoupElementWithDepartmentEditDialog<MaterialTopic>(allTopics, departmentRepository.findAll(), tutorRepository.getCurrentDepartment(), "Редактировать тему") {
            @Override
            protected void updateElementList() {
                updateTree();
            }

            @Override
            protected void delete(MaterialTopic document) {
                materialTopicRepository.delete(document);
            }

            @Override
            protected void save(MaterialTopic document) {
                materialTopicRepository.save(document);
            }

            @Override
            protected void rename(MaterialTopic document, String rename) {
                document.setName(rename);
            }

            @Override
            protected MaterialTopic getNewElement() {
                return new MaterialTopic("Новая тема");
            }
        };
    }

    private void updateTree() {
        allTopics = StreamSupport
                .stream(materialTopicRepository.findAll(tutorRepository.getCurrentDepartment()).spliterator(), false).collect(Collectors.toList());
        testTreeData.clear();
        updateArticleTreeData();
        tree.getDataProvider().refreshAll();
        topics.setItems(allTopics);
    }

    private void initTestTree() {
        testTreeData = new TreeData<>();
        updateArticleTreeData();

        TreeDataProvider<TestItem> treeDataProvider = new TreeDataProvider<>(testTreeData);

        tree = new SoupTreeGrid<>(treeDataProvider);
        tree.addHierarchyColumn(TestItem::getName).setSortable(false).setHeader("Имя");
        tree.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(it -> showTest(it.getEntity())));

        Div articleTreeDiv = new Div(tree);
        articleTreeDiv.setClassName("soup-left-panel-inner-div");
        left.add(articleTreeDiv);

        List<TestItem> topics = getTestTopics();
        if (!topics.isEmpty()) {
            TestItem item = topics.get(0);
            long id = item.getEntity().getId();
            TestItem testItem = allItems.get(id);
            tree.expand(testItem);
            if (testItem.getEntity() instanceof MaterialTopic) {
                List<Test> testList = testRepository.findAllByTopic((MaterialTopic) testItem.getEntity());
                if (!testList.isEmpty()) {
                    testItem = allItems.get(testList.get(0).getId());
                }
            }
            tree.select(testItem);
        }

        tree.getDataProvider().addDataProviderListener(e -> {
            List<TestItem> expanded = expandedTests.stream().map(it -> allItems.get(it)).collect(Collectors.toList());
            tree.expand(expanded);
            TestItem selected = tree.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (selected != null) {
                TestItem lastSelected = allItems.get(selected.entity.getId());
                tree.select(lastSelected);
            }
        });
        tree.addExpandListener(e -> expandedTests.addAll(e.getItems().stream().map(it -> it.getEntity().getId()).collect(Collectors.toList())));
        tree.addCollapseListener(e -> {
            e.getItems().forEach(it -> expandedTests.remove(it.getEntity().getId()));
        });
    }

    private void showTest(VisualEntity entity) {
        Test test;
        content.removeAll();
        testsInfoLayout = new TestContentLayout(questionRepository, questionAnswerRepository, testRepository);
        content.add(testsInfoLayout);
        if (entity instanceof MaterialTopic) {
            List<Test> testList = testRepository.findAllByTopic((MaterialTopic) entity);
            if (testList.isEmpty()) {
                testsInfoLayout.setVisible(false);
                buttons.setVisible(false);
                testName.setText("");
                return;
            }
            test = testList.get(0);
            tree.expand(allItems.get(entity.getId()));
            tree.select(allItems.get(test.getId()));
        } else {
            test = (Test) entity;
        }
        String name = test.getName();

        editButtons.removeAll();
        buttons.setVisible(true);
        testName.setText(name);
        testsInfoLayout.show(test);
    }

    private void updateArticleTreeData() {
        List<TestItem> testTopics = getTestTopics();
        allItems.clear();
        testTopics.forEach(it -> allItems.put(it.getEntity().getId(), it));
        testTreeData.addRootItems(testTopics);
        for (TestItem topic : testTopics) {
            List<Test> tests = testRepository.findAllByTopic((MaterialTopic) topic.getEntity());
            if (!tests.isEmpty()) {
                List<TestItem> testItems = tests.stream().map(TestItem::new).collect(Collectors.toList());
                testItems.forEach(it -> allItems.put(it.getEntity().getId(), it));
                testTreeData.addItems(topic, testItems);
            }
        }
    }

    private List<TestItem> getTestTopics() {
        return StreamSupport.stream(materialTopicRepository.findAll(tutorRepository.getCurrentDepartment()).spliterator(), false)
                .map(TestItem::new)
                .collect(Collectors.toList());
    }

    private static class TestItem {
        private final VisualEntity entity;

        public TestItem(VisualEntity entity) {
            this.entity = entity;
        }

        public VisualEntity getEntity() {
            return entity;
        }

        public String getName() {
            return entity.asString();
        }
    }
}
