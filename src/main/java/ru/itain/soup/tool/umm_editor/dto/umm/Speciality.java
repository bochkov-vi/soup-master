package ru.itain.soup.tool.umm_editor.dto.umm;

import ru.itain.soup.common.dto.VisualEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Специальность
 */
@Entity
@Table(schema = "\"users\"")
public class Speciality implements VisualEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id = 0;
    @NotNull
    private String name;

    public Speciality(String name) {
        this.name = name;
    }

    public Speciality() {
    }

    @Override
    public String asString() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
