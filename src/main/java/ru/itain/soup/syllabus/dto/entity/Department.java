package ru.itain.soup.syllabus.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.itain.soup.common.dto.VisualEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(schema = "syllabus")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Department extends BaseEntity implements VisualEntity {

    String name;

    @Override
    public String asString() {
        return name;
    }
}
