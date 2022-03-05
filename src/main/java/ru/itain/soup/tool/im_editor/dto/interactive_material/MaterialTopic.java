package ru.itain.soup.tool.im_editor.dto.interactive_material;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.itain.soup.common.dto.IWithDepartment;
import ru.itain.soup.syllabus.dto.entity.Department;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(schema = "interactive_material")
@Getter
@Setter
@Accessors(chain = true)
public class MaterialTopic implements IWithDepartment<MaterialTopic> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id = 0;
    @NotNull
    private String name;

    @ManyToOne
    @JoinTable(name = "interactive_material_department", schema = "interactive_material",
            joinColumns = @JoinColumn(name = "interactive_material_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "department_id", referencedColumnName = "id"))
    private Department department;

    public MaterialTopic() {
    }

    public MaterialTopic(@NotNull String name) {
        this.name = name;
    }


    @Override
    public String asString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MaterialTopic topic = (MaterialTopic) o;
        return id == topic.id &&
                Objects.equals(name, topic.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
