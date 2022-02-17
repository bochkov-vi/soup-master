package ru.itain.soup.tool.im_editor.dto.interactive_material;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import ru.itain.soup.common.dto.IWithDepartment;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.syllabus.dto.entity.Department;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * Статья.
 */
@Entity
@Table(schema = "interactive_material")
@Getter
@Setter
@Accessors(chain = true)
public class Article implements VisualEntity, InteractiveMaterial, IWithDepartment<Article> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id = 0;
    @NotNull
    private String name;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Article parent;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @ManyToOne
    @JoinTable(name = "article_department", schema = "interactive_material",
            joinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "department_id", referencedColumnName = "id"))
    private Department department;

    public Article() {
    }

    public Article(
            @NotNull String name
    ) {
        this(name, null);
    }

    public Article(
            @NotNull String name,
            @Null Article parent
    ) {
        this(name, parent, null);
    }

    public Article(
            @NotNull String name,
            @Null Article parent,
            @Null String content
    ) {
        this.name = name;
        this.parent = parent;
        this.content = content;
    }

    @Override
    public String asString() {
        return getName();
    }
}
