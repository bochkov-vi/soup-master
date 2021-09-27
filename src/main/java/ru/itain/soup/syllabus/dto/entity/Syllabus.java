package ru.itain.soup.syllabus.dto.entity;

import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Optional;

@Entity
@Table(schema = "syllabus")
@Getter
@Setter
@Accessors(chain = true)
public class Syllabus extends BaseEntity implements VisualEntity {

    @ManyToOne
    @JoinColumn(name = "speciality_id")
    private Speciality speciality;

    @ManyToOne
    @JoinColumn(name = "discipline_id")
    private Discipline discipline;

    @ManyToOne
    @JoinColumn(name = "cycle_id")
    private Cycle cycle;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    private boolean base = true;

    //трудоемкость в зачетных еденицах
    @Column(precision = 3, scale = 1)
    private BigDecimal intensity;

    private Integer trainingHours;

    private Integer selfStudyHours;

    @Override
    public String asString() {
        Optional<Syllabus> entity = Optional.ofNullable(this);
        return MoreObjects.toStringHelper("Учебный план")
                .add("Специалность", entity.map(Syllabus::getSpeciality).map(VisualEntity::asString).orElse(null))
                .add("Дисциплина", entity.map(Syllabus::getDiscipline).map(VisualEntity::asString).orElse(null))
                .add("Кафедра", entity.map(Syllabus::getDepartment).map(VisualEntity::asString).orElse(null))
                .add("Цикл", entity.map(Syllabus::getCycle).map(VisualEntity::asString).orElse(null))
                .toString();
    }

}
