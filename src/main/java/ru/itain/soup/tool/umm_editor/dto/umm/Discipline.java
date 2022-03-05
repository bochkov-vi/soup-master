package ru.itain.soup.tool.umm_editor.dto.umm;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.itain.soup.common.dto.IWithDepartment;
import ru.itain.soup.syllabus.dto.entity.Department;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Дисциплина
 **/
@Entity
@Table(schema = "umm")
@Getter
@Setter
@Accessors(chain = true)
public class Discipline implements IWithDepartment<Discipline> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id = 0;
    @NotNull
    private String name;
    @ManyToOne
    @JoinTable(name = "discipline_department", schema = "umm",
            joinColumns = @JoinColumn(name = "discipline_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "department_id", referencedColumnName = "id"))
    private Department department;

    public Discipline() {
    }

    public Discipline(@NotNull String name) {
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
        Discipline that = (Discipline) o;
        return id == that.id &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
