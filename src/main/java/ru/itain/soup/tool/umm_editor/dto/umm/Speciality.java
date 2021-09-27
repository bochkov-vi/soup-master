package ru.itain.soup.tool.umm_editor.dto.umm;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.itain.soup.common.dto.VisualEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Специальность
 */
@Entity
@Table(schema = "\"users\"")
@Getter
@Setter
@Accessors(chain = true)
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

}
