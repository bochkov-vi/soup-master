package ru.itain.soup.common.ui.view.tutor.article;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.common.service.PdfService;
import ru.itain.soup.common.ui.component.*;
import ru.itain.soup.common.ui.component.tooltip.Tooltips;
import ru.itain.soup.common.ui.view.tutor.CommonView;
import ru.itain.soup.common.ui.view.tutor.ExternalLinkCreator;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.common.ui.view.tutor.service.ArticleBlockService;
import ru.itain.soup.common.util.StreamUtils;
import ru.itain.soup.syllabus.dto.repository.DepartmentRepository;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.tool.im_editor.repository.interactive_material.ArticleRepository;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.security.Roles.ROLE_SECRETARY;
import static ru.itain.soup.common.security.Roles.ROLE_TUTOR;

@Secured({ROLE_TUTOR, ROLE_SECRETARY})
@PageTitle("СОУП - Преподаватель")
@Route(value = "tutor/article", layout = MainLayout.class)
public class ArticleMainView extends CommonView {
    private final ArticleRepository articleRepository;
    private final DepartmentRepository departmentRepository;
    private final TutorRepository tutorRepository;
    private final ArticleBlockService articleBlockService;
    private final PdfService pdfService;
    private final Button addElement = new Button(new Icon(VaadinIcon.FILE_ADD));
    private final Button editElement = new Button(new Icon(VaadinIcon.EDIT));
    private final Button deleteElement = new Button(new Icon(VaadinIcon.FILE_REMOVE));
    private final Span articleName = new Span();
    private final HorizontalLayout buttons = new HorizontalLayout();
    private final Map<Long, Article> articles = new HashMap<>();
    private final Set<Long> expandedArticles = new HashSet<>();
    private final ExternalLinkCreator linkCreator = new ExternalLinkCreator("Article");
    private Button uploadPdf;
    private Button link;
    /**
     * Локальное хранилище статей. Обновляется из БД.
     */
    private Map<Article, Article> articlesMap;
    /**
     * Верхнеуровневые статьи (без родителя).
     */
    private List<Article> articleRoots;
    private SoupTreeGrid<Article> articleTree;
    private TreeData<Article> articleTreeData;
    private Button addThemeButton;
    private Button addSectionButton;
    private Button addSubsectionButton;
    private PdfViewer pdfViewer;
    private Button copy;
    private Button openNewWindow;
    private Button moveArticle;

    public ArticleMainView(
            ArticleRepository articleRepository,
            ArticleBlockService articleBlockService,
            PdfService pdfService,
            DepartmentRepository departmentRepository,
            TutorRepository tutorRepository
    ) {
        this.articleRepository = articleRepository;
        this.articleBlockService = articleBlockService;
        this.pdfService = pdfService;
        this.tutorRepository = tutorRepository;
        this.departmentRepository = departmentRepository;
        initPage();
    }

    private void initPage() {
        center.setMaxWidth("75vw");
        Icon search = new Icon(VaadinIcon.SEARCH);
        Tooltips.addTooltip(search, "Поиск");
        Icon close = new Icon(VaadinIcon.CLOSE);
        Tooltips.addTooltip(close, "Закрыть");
        search.getStyle().set("cursor", "pointer");
        search.setSize("20px");
        close.getStyle().set("cursor", "pointer");
        close.setSize("20px");
        close.setVisible(false);
        Span label = new Span("СПРАВОЧНИКИ");
        label.setWidthFull();
        TextField searchField = new TextField();
        searchField.setWidthFull();
        searchField.setVisible(false);
        HorizontalLayout dicLabel = new HorizontalLayout(label, searchField, search, close);
        search.addClickListener(e -> {
            if (searchField.isVisible()) {
                search(searchField);
            } else {
                searchField.setVisible(true);
                close.setVisible(true);
                label.setVisible(false);
            }
        });
        close.addClickListener(e -> {
            searchField.setVisible(false);
            close.setVisible(false);
            label.setVisible(true);
            searchField.setValue("");
            articleTreeData.clear();
            updateArticlesMap();
            updateArticleTreeData();
            articleTree.getDataProvider().refreshAll();
        });
        searchField.addKeyPressListener(Key.ENTER, (ComponentEventListener<KeyPressEvent>) event -> {
            search(searchField);
        });
        dicLabel.setWidthFull();
        dicLabel.setAlignItems(Alignment.CENTER);
        dicLabel.setMinHeight("44px");
        label.getStyle().set("margin-left", "40px");
        searchField.getStyle().set("margin-left", "40px");
        search.getStyle().set("margin-right", "10px");
        close.getStyle().set("margin-right", "20px");
        left.add(dicLabel);
        initContentDiv();
        infoPanel.add(articleName);
        articleName.setClassName("soup-element-name");
        updateArticlesMap();
        initArticleContentButtons();
        initArticleTree();
    }

    private void search(TextField searchField) {
        List<Article> articleByNameLike = getArticlesByName(searchField);
        articleTreeData.clear();
        articleTreeData.addRootItems(articleByNameLike);
        articleTree.getDataProvider().refreshAll();
    }

    public List<Article> getArticlesByName(TextField searchField) {
        String value = searchField.getValue();
        value = value.toLowerCase();
        return articleRepository.findArticlesByNameLike(value);
    }

    private void initContentDiv() {
        pdfViewer = new PdfViewer();
        pdfViewer.setClassName("soup-article-content-div");
        center.add(pdfViewer);
    }

    private void initArticleContentButtons() {
        addElement.addClickListener(e -> {
            Iterator<Article> it = articleTree.getSelectedItems().iterator();
            if (it.hasNext()) {
                openContentEditor(it.next());
            }
        });
        editElement.addClickListener(e -> {
            Iterator<Article> it = articleTree.getSelectedItems().iterator();
            if (it.hasNext()) {
                Article article = it.next();
                if (article.getContent() == null && !pdfService.isPdfNull(article)) {
                    SoupBaseDialog dialog = new SoupBaseDialog(SoupBaseDialog.CAUTION, "Редактирование недоступно", "В разделе прикреплен сторонний PDF файл");
                    dialog.open();
                } else {
                    openContentEditor(article);
                }
            }
        });
        deleteElement.addClickListener(e -> {
            Iterator<Article> it = articleTree.getSelectedItems().iterator();
            if (it.hasNext()) {
                deleteContent(it.next());
            }
        });
        Tooltips.addTooltip(addElement, "Добавить");
        Tooltips.addTooltip(editElement, "Редактировать");
        Tooltips.addTooltip(deleteElement, "Удалить");
        uploadPdf = new Button(new Icon(VaadinIcon.UPLOAD));
        uploadPdf.addClickListener(e -> uploadPdf());
        link = new Button(new Icon(VaadinIcon.LINK));
        copy = new Button(new Icon(VaadinIcon.COPY));
        copy.addClickListener(e -> openMoveWindow("Копирование статьи", "Скопировать статью в", false));
        openNewWindow = new Button(new Icon(VaadinIcon.EXTERNAL_LINK));
        moveArticle = new Button(new Icon(VaadinIcon.FLIP_V));
        Tooltips.addTooltip(moveArticle, "Переместить");
        Tooltips.addTooltip(openNewWindow, "Открыть в новом окне");
        Tooltips.addTooltip(copy, "Копировать");
        Tooltips.addTooltip(uploadPdf, "Загрузить PDF");
        Tooltips.addTooltip(link, "Ссылка");
        openNewWindow.addClickListener(e -> {
            Article selectedArticle = getSelectedArticle();
            if (selectedArticle == null || pdfService.isPdfNull(selectedArticle)) {
                Notification.show("Для данной статьи не создано документа");
                return;
            }
            String href = linkCreator.executeLink(selectedArticle, this);
            getUI().ifPresent(ui -> ui.getPage().open(href));
        });
        link.addClickListener(e -> {
            Article selectedArticle = getSelectedArticle();
            if (selectedArticle == null || pdfService.isPdfNull(selectedArticle)) {
                Notification.show("Для данной статьи не создано документа");
                return;
            }
            linkCreator.executeLink(selectedArticle, this);
            Notification.show("Ссылка скопирована в буфер обмена");
        });

        moveArticle.addClickListener(e -> openMoveWindow("Перенос статьи", "Переместить статью в", true));
        buttons.add(openNewWindow, addElement, uploadPdf, editElement, copy, moveArticle, deleteElement, link);
        buttons.getStyle().set("padding-right", "20px");
        infoPanel.add(buttons);
    }

    private void openMoveWindow(String windowName, String label, boolean isMove) {
        SoupDialog dialog = new SoupDialog(windowName);
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new Label(label));
        ComboBox<Article> themes = new ComboBox<>();
        themes.setWidth("300px");
        themes.setClassName("soup-combobox");
        themes.setItemLabelGenerator(Article::getName);
        themes.setItems(articleRoots);
        HorizontalLayout row1 = new HorizontalLayout(new Label("Тема"), themes);
        row1.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row1.setWidthFull();
        verticalLayout.add(row1);

        ComboBox<Article> sections = new ComboBox<>();
        sections.setWidth("300px");
        sections.setClassName("soup-combobox");
        sections.setItemLabelGenerator(Article::getName);
        HorizontalLayout row2 = new HorizontalLayout(new Label("Раздел"), sections);
        row2.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row2.setWidthFull();
        verticalLayout.add(row2);

        ComboBox<Article> subsections = new ComboBox<>();
        subsections.setWidth("300px");
        subsections.setClassName("soup-combobox");
        subsections.setItemLabelGenerator(Article::getName);
        HorizontalLayout row3 = new HorizontalLayout(new Label("Подраздел"), subsections);
        row3.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row3.setWidthFull();
        verticalLayout.add(row3);

        themes.addValueChangeListener(e -> {
            Article theme = themes.getValue();
            if (theme != null) {
                List<Article> childArticles = getChildArticles(theme);
                sections.setItems(Collections.EMPTY_LIST);
                subsections.setItems(Collections.EMPTY_LIST);
                if (childArticles != null && !childArticles.isEmpty()) {
                    sections.setItems(childArticles);
                    sections.addValueChangeListener(k -> {
                        Article section = sections.getValue();
                        if (section != null) {
                            List<Article> subsectionList = getChildArticles(section);
                            if (subsectionList != null && !subsectionList.isEmpty()) {
                                subsections.setItems(subsectionList);
                            }
                        }
                    });
                }
            }
        });

        dialog.getOkButton().addClickListener(e -> {
            moveArticle(dialog, themes, sections, subsections, isMove);
        });
        dialog.getCancelButton().addClickListener(e -> dialog.close());
        dialog.getMainLayout().addComponentAtIndex(1, verticalLayout);
        dialog.open();
    }

    private void moveArticle(Dialog dialog,
                             ComboBox<Article> themes,
                             ComboBox<Article> sections,
                             ComboBox<Article> subsections,
                             boolean isMove) {
        Article target = subsections.getValue();
        if (target == null) {
            target = sections.getValue();
            if (target == null) {
                target = themes.getValue();
                if (target == null) {
                    return;
                }
            }
        }
        if (!StringUtils.isEmpty(target.getContent()) || !pdfService.isPdfNull(target)) {
            Article finalTarget = target;
            SoupBaseDialog confirm = new SoupBaseDialog(
                    click -> {
                        moveArticleContent(isMove, finalTarget);
                        dialog.close();
                    },
                    click -> dialog.close(),
                    SoupBaseDialog.CONFIRM,
                    "PDF документ уже существует", "Вы действительно хотите заменить существующий документ?"
            );
            confirm.open();
        } else {
            moveArticleContent(isMove, target);
            dialog.close();
        }
    }

    private void moveArticleContent(boolean isMove, Article target) {
        Article selectedArticle = getSelectedArticle();
        target.setContent(selectedArticle.getContent());
        pdfService.copyPdf(target, selectedArticle);
        if (isMove) {
            selectedArticle.setContent(null);
            articleRepository.save(selectedArticle);
            pdfService.deletePdf(selectedArticle);
        }
        articleRepository.save(target);
        articleTreeData.clear();
        updateArticlesMap();
        updateArticleTreeData();
        articleTree.getDataProvider().refreshAll();
    }

    private void initArticleTree() {
        articleTreeData = new TreeData<>();
        updateArticleTreeData();

        TreeDataProvider<Article> articleTreeDataProvider = new TreeDataProvider<>(articleTreeData);

        articleTree = new SoupTreeGrid<>(articleTreeDataProvider);
        articleTree.addHierarchyColumn(Article::getName).setSortable(false).setHeader("Имя");
        articleTree.addSelectionListener(event -> updateArticle(event.getFirstSelectedItem().orElse(null)));

        Div articleTreeDiv = new Div(articleTree);
        articleTreeDiv.setClassName("soup-left-panel-inner-div");
        left.add(articleTreeDiv);
        left.add(createEditTreeButtons());

        List<Article> rootArticles = getArticleRoots();
        if (!rootArticles.isEmpty()) {
            articleTree.select(rootArticles.get(0));
        } else {
            updateArticle(null);
        }
    }

    private void updatePdfViewer(Article article) {
        if (article == null || pdfService.isPdfNull(article)) {
            pdfViewer.setSrc("");
        } else {
            // FIXME добавляем System.currentTimeMillis() для того, чтобы принудительно заставить Vaadin обновить src, чтобы документ перечитался
            pdfViewer.setSrc("/api/pdf/" + article.getId() + ".pdf?time=" + System.currentTimeMillis());
        }
    }

    private void updateSelectedArticle() {
        Article selectedArticle = getSelectedArticle();
        updateArticle(selectedArticle);
    }

    private Article getSelectedArticle() {
        return articleTree.getSelectionModel().getFirstSelectedItem().orElse(null);
    }

    private void updateArticle(Article article) {
        updateContentButtons(article);
        updatePdfViewer(article);
        if (article == null) {
            articleName.setText("");
            addSectionButton.setEnabled(false);
            addSubsectionButton.setEnabled(false);
        } else {
            String name = article.getName();
            articleName.setText(name);
            if (article.getParent() == null) {
                addSectionButton.setEnabled(true);
                addSubsectionButton.setEnabled(false);
            } else if (article.getParent().getParent() == null) {
                addSectionButton.setEnabled(true);
                addSubsectionButton.setEnabled(true);
            } else {
                addSectionButton.setEnabled(true);
                addSubsectionButton.setEnabled(true);
            }
            Tooltips.addTooltip(articleName, article.getName());
        }
    }

    private void updateArticlesMap() {
        List<Article> collect = StreamSupport.stream(articleRepository.findAll(tutorRepository.getCurrentDepartment()).spliterator(), false).collect(Collectors.toList());
        collect.forEach(it -> articles.put(it.getId(), it));
        articlesMap = collect.stream()
                // workaround for bug in JDK https://stackoverflow.com/a/24634007/1285467
                .collect(HashMap::new, (m, v) -> m.put(v, v.getParent()), HashMap::putAll);
        // обновили статьи - следует пересоздать список верхнеуровневых статей
        articleRoots = null;

    }

    private List<Article> getArticleRoots() {
        if (articleRoots == null) {
            articleRoots = articlesMap.entrySet().stream()
                    .filter(it -> it.getValue() == null)
                    .map(Map.Entry::getKey)
                    .sorted(Comparator.comparingLong(Article::getId))
                    .collect(Collectors.toList());
        }
        return articleRoots;
    }

    private Component createEditTreeButtons() {
        FlexLayout mainLayout = new FlexLayout();
        mainLayout.setWrapMode(WrapMode.WRAP);
        mainLayout.setJustifyContentMode(JustifyContentMode.AROUND);
        mainLayout.setClassName("soup-article-edit-theme-buttons");
        mainLayout.setWidthFull();
        addThemeButton = new Button("+/- Тема");
        addThemeButton.addClickListener(e -> openThemeEditDialog());
        mainLayout.add(addThemeButton);

        addSectionButton = new Button("+/- Раздел");
        addSectionButton.addClickListener(e -> openSectionEditDialog());
        mainLayout.add(addSectionButton);

        addSubsectionButton = new Button("+/- Подраздел");
        addSubsectionButton.addClickListener(e -> openSubsectionEditDialog());
        mainLayout.add(addSubsectionButton);
        articleTree.getDataProvider().addDataProviderListener(e -> {
            List<Article> expanded = expandedArticles.stream().map(articles::get).collect(Collectors.toList());
            articleTree.expand(expanded);
            Article selectedArticle = getSelectedArticle();
            if (selectedArticle == null) {
                addThemeButton.setEnabled(false);
                addSectionButton.setEnabled(false);
                addSubsectionButton.setEnabled(false);
            } else {
                Article lastSelected = articles.get(selectedArticle.getId());
                articleTree.select(lastSelected);
            }
        });
        articleTree.addExpandListener(e -> {
            expandedArticles.addAll(e.getItems().stream().map(Article::getId).collect(Collectors.toList()));
        });
        articleTree.addCollapseListener(e -> {
            e.getItems().forEach(it -> expandedArticles.remove(it.getId()));
        });
        return mainLayout;
    }

    private void openThemeEditDialog() {
        new SoupElementWithDepartmentEditDialog<Article>(articleRoots, departmentRepository.findAll(), tutorRepository.getCurrentDepartment(), "РЕДАКТИРОВАНИЕ ТЕМ") {
            @Override
            protected void updateElementList() {
                articleTreeData.clear();
                updateArticlesMap();
                updateArticleTreeData();
                articleTree.getDataProvider().refreshAll();
            }

            @Override
            protected void delete(Article document) {
                articleRepository.delete(document);
                pdfService.deletePdf(document);
            }

            @Override
            protected void save(Article document) {
                articleRepository.save(document);
            }

            @Override
            protected void rename(Article document, String rename) {
                document.setName(rename);
            }

            @Override
            protected Article getNewElement() {
                return new Article("Новая тема");
            }
        };

    }

    private void openSectionEditDialog() {
        Article selectedArticle = getSelectedArticle();
        if (selectedArticle == null) {
            return;
        }
        String themeName;
        Article targetArticle;
        Article firstParent = selectedArticle.getParent();
        if (firstParent != null) {
            Article topParent = firstParent.getParent();
            if (topParent != null) {
                themeName = topParent.getName();
                targetArticle = topParent;
            } else {
                themeName = firstParent.getName();
                targetArticle = firstParent;
            }
        } else {
            themeName = selectedArticle.getName();
            targetArticle = selectedArticle;
        }
        new SoupElementEditDialog<Article>(getChildArticles(targetArticle), "РЕДАКТИРОВАНИЕ РАЗДЕЛОВ", "ТЕМА: " + themeName) {
            @Override
            protected void updateElementList() {
                articleTreeData.clear();
                updateArticlesMap();
                updateArticleTreeData();
                articleTree.getDataProvider().refreshAll();
            }

            @Override
            protected void delete(Article document) {
                articleRepository.delete(document);
                pdfService.deletePdf(document);
            }

            @Override
            protected void save(Article document) {
                articleRepository.save(document);
            }

            @Override
            protected void rename(Article document, String rename) {
                document.setName(rename);
            }

            @Override
            protected Article getNewElement() {
                return new Article("Новый раздел", targetArticle);
            }
        };
    }

    private void openSubsectionEditDialog() {
        Article selectedArticle = getSelectedArticle();
        if (selectedArticle == null) {
            return;
        }
        String themeName;
        Article targetArticle;
        Article firstParent = selectedArticle.getParent();
        if (firstParent != null) {
            Article topParent = firstParent.getParent();
            if (topParent != null) {
                themeName = topParent.getName();
                targetArticle = firstParent;
            } else {
                themeName = firstParent.getName();
                targetArticle = selectedArticle;
            }
        } else {
            themeName = selectedArticle.getName();
            targetArticle = selectedArticle;
        }

        new SoupElementEditDialog<Article>(
                getChildArticles(targetArticle),
                "РЕДАКТИРОВАНИЕ ПОДРАЗДЕЛОВ",
                "ТЕМА: " + themeName,
                "РАЗДЕЛ: " + targetArticle.getName()
        ) {
            @Override
            protected void updateElementList() {
                articleTreeData.clear();
                updateArticlesMap();
                updateArticleTreeData();
                articleTree.getDataProvider().refreshAll();
            }

            @Override
            protected void delete(Article document) {
                articleRepository.delete(document);
                pdfService.deletePdf(document);
            }

            @Override
            protected void save(Article document) {
                articleRepository.save(document);
            }

            @Override
            protected void rename(Article document, String rename) {
                document.setName(rename);
            }

            @Override
            protected Article getNewElement() {
                return new Article("Новый подраздел", targetArticle);
            }
        };
    }

    private void updateArticleTreeData() {
        List<Article> rootArticles = getArticleRoots();
        articleTreeData.addRootItems(rootArticles);
        for (Article rootArticle : rootArticles) {
            List<Article> childArticles = getChildArticles(rootArticle);
            articleTreeData.addItems(rootArticle, childArticles);
            for (Article childArticle : childArticles) {
                articleTreeData.addItems(childArticle, getChildArticles(childArticle));
            }
        }
    }

    private void uploadPdf() {
        SoupDialog dialog = new SoupDialog("Добавление PDF");
        VerticalLayout layout = new VerticalLayout();
        FileBuffer buffer = new FileBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFiles(1);
        int megabytes = 200;
        upload.setMaxFileSize(1024 * 1024 * megabytes);
        upload.setDropLabel(new Label("Перетащите сюда PDF файл"));
        upload.setAcceptedFileTypes("application/pdf");
        upload.setId("i18n-upload");
        UploadI18N i18n = new UploadI18N();
        i18n.setDropFiles(
                        new UploadI18N.DropFiles().setOne("Перетащите файл сюда...")
                                .setMany("Перетащите файлы сюда..."))
                .setAddFiles(new UploadI18N.AddFiles()
                        .setOne("Выбрать файл").setMany("Добавить файлы"))
                .setCancel("Отменить")
                .setError(new UploadI18N.Error()
                        .setTooManyFiles("Слишком много файлов")
                        .setFileIsTooBig("Превышен максимальный размер файла в " + megabytes + " Мбайт")
                        .setIncorrectFileType("Некорректный тип файла"))
                .setUploading(new UploadI18N.Uploading()
                        .setStatus(new UploadI18N.Uploading.Status()
                                .setConnecting("Соединение...")
                                .setStalled("Загрузка застопорилась.")
                                .setProcessing("Обработка файла..."))
                        .setRemainingTime(
                                new UploadI18N.Uploading.RemainingTime()
                                        .setPrefix("оставшееся время: ")
                                        .setUnknown(
                                                "оставшееся время неизвестно"))
                        .setError(new UploadI18N.Uploading.Error()
                                .setServerUnavailable("Сервер недоступен")
                                .setUnexpectedServerError(
                                        "Неожиданная ошибка сервера")
                                .setForbidden("Загрузка запрещена")))
                .setUnits(Stream
                        .of("Б", "Кбайт", "Мбайт", "Гбайт", "Тбайт", "Пбайт",
                                "Эбайт", "Збайт", "Ибайт")
                        .collect(Collectors.toList()));

        upload.setI18n(i18n);
        ContextMenu contextMenu = new ContextMenu(uploadPdf);
        contextMenu.addItem(upload);
        layout.add(upload);
        Label error = new Label();
        error.setVisible(false);
        error.getStyle().set("color", "var(--lumo-error-text-color)");
        layout.add(error);
        upload.addFileRejectedListener(e -> {
            error.setText(e.getErrorMessage());
            error.setVisible(true);
        });
        upload.addSucceededListener(e -> {
            error.setText("");
            error.setVisible(false);
        });
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setJustifyContentMode(JustifyContentMode.END);
        buttons.setWidthFull();
        dialog.getOkButton().addClickListener(e -> {
            if (buffer.getFileData() == null) {
                return;
            }
            Article selectedArticle = getSelectedArticle();
            if (selectedArticle == null) {
                dialog.close();
                return;
            }
            if (!StringUtils.isEmpty(selectedArticle.getContent()) || !pdfService.isPdfNull(selectedArticle)) {
                SoupBaseDialog confirm = new SoupBaseDialog(
                        click -> {
                            savePdfToArticle(buffer, selectedArticle);
                            dialog.close();
                        },
                        click -> dialog.close(),
                        SoupBaseDialog.CONFIRM,
                        "PDF документ уже существует", "Вы действительно хотите заменить существующий документ?"
                );
                confirm.open();
            } else {
                savePdfToArticle(buffer, selectedArticle);
                dialog.close();
            }
        });
        dialog.getCancelButton().addClickListener(e -> dialog.close());
        dialog.getMainLayout().addComponentAtIndex(1, layout);
        dialog.open();
    }

    private void savePdfToArticle(FileBuffer buffer, Article selectedArticle) {
        FileData fileData = buffer.getFileData();
        OutputStream outputBuffer = fileData.getOutputBuffer();
        if (outputBuffer instanceof ByteArrayOutputStream) {
            ByteArrayOutputStream outputStream = (ByteArrayOutputStream) outputBuffer;
            selectedArticle.setContent(null);
            selectedArticle = articleRepository.save(selectedArticle);
            pdfService.createPdf(selectedArticle, StreamUtils.pipe(outputStream));
        } else if (outputBuffer instanceof FileOutputStream) {
            try {
                Field pathField = outputBuffer.getClass().getDeclaredField("path");
                pathField.setAccessible(true);
                String path = (String) pathField.get(outputBuffer);
                selectedArticle.setContent(null);
                selectedArticle = articleRepository.save(selectedArticle);
                pdfService.movePdf(selectedArticle, Paths.get(path));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        updateContentButtons(selectedArticle);
        updatePdfViewer(selectedArticle);
    }

    private List<Article> getChildArticles(Article rootArticle) {
        return articlesMap.entrySet().stream()
                .filter(it -> it != null && rootArticle.equals(it.getValue()))
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparingLong(Article::getId))
                .collect(Collectors.toList());
    }

    private void updateContentButtons(Article article) {
        if (article == null) {
            link.setEnabled(false);
            editElement.setEnabled(false);
            deleteElement.setEnabled(false);
            addElement.setEnabled(false);
            uploadPdf.setEnabled(false);
            copy.setEnabled(false);
            openNewWindow.setEnabled(false);
            moveArticle.setEnabled(false);
        } else {
            boolean hasContent = article.getContent() != null;
            boolean hasPdf = !pdfService.isPdfNull(article);
            boolean hasContentOrPdf = hasContent || hasPdf;
            link.setEnabled(hasContentOrPdf);
            editElement.setEnabled(hasContentOrPdf);
            deleteElement.setEnabled(hasContentOrPdf);
            addElement.setEnabled(!hasPdf);
            uploadPdf.setEnabled(true);
            copy.setEnabled(hasContentOrPdf);
            openNewWindow.setEnabled(hasContentOrPdf);
            moveArticle.setEnabled(hasContentOrPdf);
        }
    }

    private void deleteContent(Article article) {
        SoupBaseDialog dialog = new SoupBaseDialog(
                click -> {
                    article.setContent(null);
                    articleRepository.save(article);
                    pdfService.deletePdf(article);
                    updateSelectedArticle();
                }, SoupBaseDialog.CONFIRM,
                "Удалить содержимое '" + article.getName() + "'?"
        );
        dialog.open();
    }

    private void openContentEditor(Article article) {
        center.setMaxWidth("100vw");
        PdfEditor pdfEditor = new PdfEditor(PdfEditor.Mode.ARTICLE, articleRepository, articleBlockService,tutorRepository);
        pdfEditor.setId("soup-tutor-content-edit-pdf-editor");
        Button saveResult = new Button("Сохранить", e -> {
            pdfEditor.save(result -> {
                article.setContent(result.getHtml());
                articleRepository.save(article);
                pdfService.createPdf(article, result.getPdf());
            });
        });
        Button finish = new Button("Завершить", e -> {
            pdfEditor.isChanged(isChanged -> {
                if (!isChanged) {
                    activateViewMode();
                    return;
                }
                SoupBaseDialog dialog = new SoupBaseDialog(click -> pdfEditor.save(result -> {
                    article.setContent(result.getHtml());
                    articleRepository.save(article);
                    pdfService.createPdf(article, result.getPdf());
                    activateViewMode();
                    updateArticle(article);
                }), "Документ был изменен",
                        "Сохранить",
                        new Button("Не сохранять",
                                click -> {
                                    updateArticle(article);
                                    activateViewMode();
                                }),
                        "Сохранить изменения?");
                dialog.open();
            });
        });
        activateEditorMode(article, pdfEditor, saveResult, finish);
    }

    private void activateEditorMode(Article article, PdfEditor pdfEditor, Button saveResult, Button finish) {
        buttons.setVisible(false);
        HorizontalLayout div = new HorizontalLayout(saveResult, finish);
        div.setWidthFull();
        div.setId("soup-tutor-content-edit-buttons");
        div.setJustifyContentMode(JustifyContentMode.END);
        infoPanel.add(div);
        if (article.getContent() != null) {
            pdfEditor.load(article.getContent());
        }
        left.setVisible(false);
        pdfViewer.setVisible(false);
        center.add(pdfEditor);
    }

    private void activateViewMode() {
        center.setMaxWidth("75vw");
        center.getChildren()
                .filter(it -> "soup-tutor-content-edit-pdf-editor".equals(it.getId().orElse(null)))
                .forEach(center::remove);
        left.setVisible(true);
        infoPanel.getChildren()
                .filter(it -> "soup-tutor-content-edit-buttons".equals(it.getId().orElse(null)))
                .forEach(infoPanel::remove);
        buttons.setVisible(true);
        pdfViewer.setVisible(true);
    }
}
