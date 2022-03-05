package ru.itain.soup.common.ui.view.tutor;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.common.ui.component.SoupTreeGrid;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.Plan;
import ru.itain.soup.tool.umm_editor.dto.umm.Topic;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.PlanRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.TopicRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@UIScope
public class UmmTreeCreator {
    private final DisciplineRepository disciplineRepository;
    private final SpecialityRepository specialityRepository;
    private final TutorRepository tutorRepository;
    private final Map<Long, TreeItem> items = new HashMap<>();
    private final TopicRepository topicRepository;
    private final PlanRepository planRepository;
    private final LessonRepository lessonRepository;
    private final Set<Long> expandedUmm = new HashSet<>();
    private TreeData<TreeItem> ummTreeData;
    private SoupTreeGrid<TreeItem> ummTree;
    private List<TreeItem> disciplineItemList;
    private List<TreeItem> specialityItemList;
    private TreeItem lastSelected;

    public UmmTreeCreator(
            DisciplineRepository disciplineRepository,
            TutorRepository tutorRepository, TopicRepository topicRepository,
            PlanRepository planRepository,
            LessonRepository lessonRepository,
            SpecialityRepository specialityRepository
    ) {
        this.disciplineRepository = disciplineRepository;
        this.tutorRepository = tutorRepository;
        this.topicRepository = topicRepository;
        this.planRepository = planRepository;
        this.lessonRepository = lessonRepository;
        this.specialityRepository = specialityRepository;
    }

    public SoupTreeGrid<TreeItem> createSpecialityTree(SelectionListener<Grid<TreeItem>, TreeItem> listener) {
        ummTreeData = new TreeData<>();
        TreeDataProvider<TreeItem> treeDataProvider = new TreeDataProvider<>(ummTreeData);
        ummTree = new SoupTreeGrid<>(treeDataProvider);

        updateSpecialityList();

        ummTree.addHierarchyColumn(TreeItem::getName).setSortable(false);
        ummTree.addSelectionListener(e -> {
            listener.selectionChange(e);
            e.getFirstSelectedItem().ifPresent(it -> lastSelected = it);
        });

        ummTree.getDataProvider().addDataProviderListener(e -> {
            List<TreeItem> expanded = expandedUmm.stream().map(items::get).collect(Collectors.toList());
            ummTree.expand(expanded);
            ummTree.select(lastSelected);
            ummTree.expand(lastSelected);
        });
        ummTree.addExpandListener(e -> expandedUmm.addAll(e.getItems().stream().map(it -> it.getEntity().getId()).collect(Collectors.toList())));
        ummTree.addCollapseListener(e -> e.getItems().forEach(it -> expandedUmm.remove(it.getEntity().getId())));
        selectFirst();
        return ummTree;
    }

    public SoupTreeGrid<TreeItem> createTree(SelectionListener<Grid<TreeItem>, TreeItem> listener) {
        ummTreeData = new TreeData<>();
        TreeDataProvider<TreeItem> treeDataProvider = new TreeDataProvider<>(ummTreeData);
        ummTree = new SoupTreeGrid<>(treeDataProvider);

        updateDisciplines();
        updateUmmTreeData();

        ummTree.addHierarchyColumn(TreeItem::getName).setSortable(false);
        ummTree.addSelectionListener(e -> {
            listener.selectionChange(e);
            e.getFirstSelectedItem().ifPresent(it -> lastSelected = it);
        });

        ummTree.getDataProvider().addDataProviderListener(e -> {
            List<TreeItem> expanded = expandedUmm.stream().map(items::get).collect(Collectors.toList());
            ummTree.expand(expanded);
            ummTree.select(lastSelected);
            ummTree.expand(lastSelected);
        });
        ummTree.addExpandListener(e -> expandedUmm.addAll(e.getItems().stream().map(it -> it.getEntity().getId()).collect(Collectors.toList())));
        ummTree.addCollapseListener(e -> e.getItems().forEach(it -> expandedUmm.remove(it.getEntity().getId())));
        selectFirst();
        return ummTree;
    }

    public SoupTreeGrid<TreeItem> createTree(Tutor byTutor, @NonNull Discipline byDiscipline, SelectionListener<Grid<TreeItem>, TreeItem> listener) {
        ummTreeData = new TreeData<>();
        TreeDataProvider<TreeItem> treeDataProvider = new TreeDataProvider<>(ummTreeData);
        ummTree = new SoupTreeGrid<>(treeDataProvider);

        updateDisciplines();
        updateUmmTreeData(byTutor, byDiscipline);

        ummTree.addHierarchyColumn(TreeItem::getName).setSortable(false);
        ummTree.addSelectionListener(e -> {
            listener.selectionChange(e);
            e.getFirstSelectedItem().ifPresent(it -> lastSelected = it);
        });

        ummTree.getDataProvider().addDataProviderListener(e -> {
            List<TreeItem> expanded = expandedUmm.stream().map(items::get).collect(Collectors.toList());
            ummTree.expand(expanded);
            ummTree.select(lastSelected);
            ummTree.expand(lastSelected);
        });
        ummTree.addExpandListener(e -> expandedUmm.addAll(e.getItems().stream().map(it -> it.getEntity().getId()).collect(Collectors.toList())));
        ummTree.addCollapseListener(e -> e.getItems().forEach(it -> expandedUmm.remove(it.getEntity().getId())));
        selectFirst();
        return ummTree;
    }

    public SoupTreeGrid<TreeItem> createTree(Tutor byTutor, SelectionListener<Grid<TreeItem>, TreeItem> listener) {
        ummTreeData = new TreeData<>();
        TreeDataProvider<TreeItem> treeDataProvider = new TreeDataProvider<>(ummTreeData);
        ummTree = new SoupTreeGrid<>(treeDataProvider);

        updateDisciplines();
        updateUmmTreeData(byTutor);

        ummTree.addHierarchyColumn(TreeItem::getName).setSortable(false);
        ummTree.addSelectionListener(e -> {
            listener.selectionChange(e);
            e.getFirstSelectedItem().ifPresent(it -> lastSelected = it);
        });

        ummTree.getDataProvider().addDataProviderListener(e -> {
            List<TreeItem> expanded = expandedUmm.stream().map(items::get).collect(Collectors.toList());
            ummTree.expand(expanded);
            ummTree.select(lastSelected);
            ummTree.expand(lastSelected);
        });
        ummTree.addExpandListener(e -> expandedUmm.addAll(e.getItems().stream().map(it -> it.getEntity().getId()).collect(Collectors.toList())));
        ummTree.addCollapseListener(e -> e.getItems().forEach(it -> expandedUmm.remove(it.getEntity().getId())));
        selectFirst();
        return ummTree;
    }

    public void selectFirst() {
        if (!disciplineItemList.isEmpty()) {
            ummTree.select(disciplineItemList.get(0));
        }
    }

    public VisualEntity getSelectedItem() {
        Iterator<TreeItem> it = ummTree.getSelectedItems().iterator();
        if (it.hasNext()) {
            return it.next().getEntity();
        }
        return null;
    }

    public void refreshAll() {
        updateDisciplines();
        updateUmmTreeData();
    }

    private void updateDisciplines() {
        disciplineItemList = disciplineRepository.findAll(tutorRepository.getCurrentDepartment()).stream()
                .map(TreeItem::new)
                .collect(Collectors.toList());
    }

    private void updateSpecialityList() {
        specialityItemList = specialityRepository.findAll().stream()
                .map(TreeItem::new)
                .collect(Collectors.toList());
    }

    public void updateUmmTreeData(Tutor byTutor, @NonNull Discipline byDiscipline) {
        ummTreeData.clear();
        Set<Topic> topics = new HashSet<>();

        boolean tutorFilter = byTutor != null;
        if (tutorFilter) {
            topics.addAll(topicRepository.findAllByDisciplineAndTutor(byDiscipline, byTutor));
        } else {
            topics.addAll(getTopics(byDiscipline));
        }
        List<TreeItem> topicsItemList = topics.stream()
                .map(UmmTreeCreator.TreeItem::new)
                .sorted(Comparator.comparingLong(TreeItem::getId))
                .collect(Collectors.toList());
        ummTreeData.addRootItems(topicsItemList);
        items.clear();
        topicsItemList.forEach(topic -> {
            VisualEntity entity = topic.getEntity();
            items.put(entity.getId(), topic);
            Set<Plan> plans;
            plans = tutorFilter ?
                    new HashSet<>(planRepository.findAllByTutorAndTopic(byTutor, (Topic) entity)) :
                    new HashSet<>(getPlans((Topic) topic.getEntity()));

            List<UmmTreeCreator.TreeItem> planItems = plans.stream()
                    .map(UmmTreeCreator.TreeItem::new)
                    .sorted(Comparator.comparingLong(TreeItem::getId))
                    .collect(Collectors.toList());
            ummTreeData.addItems(topic, planItems);
            planItems.forEach(plan -> {
                items.put(plan.getEntity().getId(), plan);
                List<Lesson> lessonList = new ArrayList<>(lessonRepository.findAllByLessonPlanFetched((Plan) plan.getEntity()));
                lessonList.sort((o1, o2) -> {
                    if (o1.getDefault()) {
                        return -1;
                    }
                    if (o2.getDefault()) {
                        return 1;
                    }
                    return 0;
                });
                if (tutorFilter) {
                    lessonList = lessonList.stream()
                            .filter(lesson -> byTutor.equals(lesson.getTutor()) || lesson.getDefault())
                            .collect(Collectors.toList());
                }
                List<UmmTreeCreator.TreeItem> lessonItems = lessonList.stream()
                        .map(UmmTreeCreator.TreeItem::new)
                        .sorted(Comparator.comparingLong(TreeItem::getId))
                        .collect(Collectors.toList());
                lessonItems.forEach(it -> items.put(it.getEntity().getId(), it));
                ummTreeData.addItems(plan, lessonItems);
            });
        });
        ummTree.getDataProvider().refreshAll();
    }

    public void updateUmmTreeData() {
        ummTreeData.clear();
        ummTreeData.addRootItems(disciplineItemList);
        items.clear();
        disciplineItemList.forEach(discipline -> {
            items.put(discipline.getEntity().getId(), discipline);
            List<Topic> topics = getTopics((Discipline) discipline.getEntity());
            List<TreeItem> topicItems = topics.stream()
                    .map(TreeItem::new)
                    .sorted(Comparator.comparingLong(TreeItem::getId))
                    .collect(Collectors.toList());
            ummTreeData.addItems(discipline, topicItems);
            topicItems.forEach(topic -> {
                items.put(topic.getEntity().getId(), topic);
                List<Plan> plans = getPlans((Topic) topic.getEntity());
                List<TreeItem> lessonItems = plans.stream()
                        .map(TreeItem::new)
                        .sorted(Comparator.comparingLong(TreeItem::getId))
                        .collect(Collectors.toList());
                ummTreeData.addItems(topic, lessonItems);
                lessonItems.forEach(it -> items.put(it.getEntity().getId(), it));
            });
        });
        ummTree.getDataProvider().refreshAll();
    }

    public void updateUmmTreeData(Tutor tutor) {
        ummTreeData.clear();
        ummTreeData.addRootItems(disciplineItemList);
        items.clear();
        disciplineItemList.forEach(discipline -> {
            items.put(discipline.getEntity().getId(), discipline);
            List<Topic> topics = getTopics((Discipline) discipline.getEntity());
            List<TreeItem> topicItems = topics.stream()
                    .map(TreeItem::new)
                    .sorted(Comparator.comparingLong(TreeItem::getId))
                    .collect(Collectors.toList());
            ummTreeData.addItems(discipline, topicItems);
            topicItems.forEach(topic -> {
                items.put(topic.getEntity().getId(), topic);
                Set<Plan> plans;
                plans = tutor != null ?
                        new HashSet<>(planRepository.findAllByTutorAndTopic(tutor, (Topic) topic.getEntity())) :
                        new HashSet<>(getPlans((Topic) topic.getEntity()));

                List<UmmTreeCreator.TreeItem> planItems = plans.stream()
                        .map(UmmTreeCreator.TreeItem::new)
                        .sorted(Comparator.comparingLong(TreeItem::getId))
                        .collect(Collectors.toList());
                planItems.forEach(plan -> {
                    List<Lesson> lessonList = new ArrayList<>(lessonRepository.findAllByLessonPlanFetched((Plan) plan.getEntity()));
                    lessonList.sort((o1, o2) -> {
                        if (o1.getDefault()) {
                            return -1;
                        }
                        if (o2.getDefault()) {
                            return 1;
                        }
                        return 0;
                    });
                    if (tutor != null) {
                        List<Lesson> collect = lessonList.stream()
                                .filter(lesson -> tutor.equals(lesson.getTutor()))
                                .collect(Collectors.toList());
                        lessonList.clear();
                        lessonList.addAll(collect);
                    }
                    List<UmmTreeCreator.TreeItem> lessonItems = lessonList.stream()
                            .map(UmmTreeCreator.TreeItem::new)
                            .sorted(Comparator.comparingLong(TreeItem::getId))
                            .collect(Collectors.toList());
                    lessonItems.forEach(it -> items.put(it.getEntity().getId(), it));
                    ummTreeData.addItems(topic, lessonItems);
                });
            });
        });
        ummTree.getDataProvider().refreshAll();
    }

    private List<Topic> getTopics(Discipline discipline) {
        return topicRepository.findAllByDiscipline(discipline);
    }

    private List<Plan> getPlans(Topic topic) {
        return planRepository.findAllByTopic(topic);
    }

    public void filter(Tutor tutor, LocalDate fromValue, LocalDate toValue) {
        ummTreeData.clear();
        ummTreeData.addRootItems(disciplineItemList);
        items.clear();
        disciplineItemList.forEach(discipline -> {
            items.put(discipline.getEntity().getId(), discipline);
            List<Topic> topics = getTopics((Discipline) discipline.getEntity());
            List<TreeItem> topicItems = topics.stream()
                    .map(TreeItem::new)
                    .sorted(Comparator.comparingLong(TreeItem::getId))
                    .collect(Collectors.toList());
            ummTreeData.addItems(discipline, topicItems);
            topicItems.forEach(topic -> {
                items.put(topic.getEntity().getId(), topic);
                Set<Plan> plans;
                plans = tutor != null ?
                        new HashSet<>(planRepository.findAllByTutorAndTopic(tutor, (Topic) topic.getEntity())) :
                        new HashSet<>(getPlans((Topic) topic.getEntity()));

                List<UmmTreeCreator.TreeItem> planItems = plans.stream()
                        .map(UmmTreeCreator.TreeItem::new)
                        .sorted(Comparator.comparingLong(TreeItem::getId))
                        .collect(Collectors.toList());
                planItems.forEach(plan -> {
                    List<Lesson> lessonList = new ArrayList<>(lessonRepository.findAllByLessonPlanFetched((Plan) plan.getEntity()));
                    lessonList.sort((o1, o2) -> {
                        if (o1.getDefault()) {
                            return -1;
                        }
                        if (o2.getDefault()) {
                            return 1;
                        }
                        return 0;
                    });
                    if (tutor != null) {
                        List<Lesson> collect = lessonList.stream()
                                .filter(lesson -> tutor.equals(lesson.getTutor()))
                                .collect(Collectors.toList());
                        lessonList.clear();
                        lessonList.addAll(collect);
                    }
                    if (fromValue != null) {
                        lessonList = lessonList.stream().filter(it -> {
                                    LocalDate lessonDate = it.getLessonDate();
                                    if (lessonDate == null) {
                                        return false;
                                    }
                                    return lessonDate.isAfter(fromValue) || Objects.equals(lessonDate, fromValue);
                                })
                                .collect(Collectors.toList());
                    }
                    if (toValue != null) {
                        lessonList = lessonList.stream().filter(it -> {
                                    LocalDate lessonDate = it.getLessonDate();
                                    if (lessonDate == null) {
                                        return false;
                                    }
                                    return lessonDate.isBefore(toValue) || Objects.equals(lessonDate, toValue);
                                })
                                .collect(Collectors.toList());
                    }
                    List<UmmTreeCreator.TreeItem> lessonItems = lessonList.stream()
                            .map(UmmTreeCreator.TreeItem::new)
                            .sorted(Comparator.comparingLong(TreeItem::getId))
                            .collect(Collectors.toList());
                    lessonItems.forEach(it -> items.put(it.getEntity().getId(), it));
                    ummTreeData.addItems(topic, lessonItems);
                });
            });
        });
        ummTree.getDataProvider().refreshAll();
    }

    public void select(Lesson lesson) {
        TreeItem treeItem = items.get(lesson.getId());
        ummTree.select(treeItem);
        ummTree.getDataProvider().refreshAll();
    }

    public void select(Plan plan) {
        TreeItem treeItem = items.get(plan.getId());
        ummTree.select(treeItem);
        ummTree.getDataProvider().refreshAll();
    }

    private Tutor getTutor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return tutorRepository.findByUserUsername(authentication.getName());
    }

    public class TreeItem {
        private final Long id;
        private VisualEntity entity;

        public TreeItem(VisualEntity entity) {
            this.entity = entity;
            this.id = entity.getId();
        }

        public Long getId() {
            return id;
        }

        public VisualEntity getEntity() {
            return entity;
        }

        public void setEntity(VisualEntity entity) {
            this.entity = entity;
        }

        public String getName() {
            boolean isLesson = entity instanceof Lesson;
            if (isLesson) {
                Lesson lesson = (Lesson) entity;
                if (Boolean.TRUE.equals(((Lesson) entity).getDefault())) {
                    return "\uD83D\uDCC3" + " " + entity.asString();
                }
                boolean myLesson = Objects.equals(lesson.getTutor(), getTutor());
                if (!myLesson) {
                    return "\uD83D\uDD12" + " " + entity.asString();
                }

            }
            return entity.asString();
        }
    }
}
