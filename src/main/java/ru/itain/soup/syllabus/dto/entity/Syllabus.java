package ru.itain.soup.syllabus.dto.entity;

import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@NoArgsConstructor
public class Syllabus extends BaseEntity implements VisualEntity {

    @ManyToOne
    @JoinColumn(name = "speciality_id")
    private Speciality speciality;

    @ManyToOne
    @JoinColumn(name = "discipline_id")
    private Discipline discipline;

    @ManyToOne
    @JoinColumn(name = "syllabus_category_id")
    private SyllabusCategory category;


    private String index;

    private int fertileUnits;//зачетные еденицы

    private int lectures;
    private int seminars;
    private int groupExercises;
    private int groupLessons;
    private int laboratoryWorks;
    private int practicalLessons;
    private int specialLessons;
    private int courseWorks;
    private int conferences;
    private int practices;
    private int tests;//контрольные работы
    private int credit;//зачеты
    private int undefiningParameter;

    private boolean base = true;


    //====формы промежуточного и итого контроля

    BigDecimal examControl;
    BigDecimal gradedCreditControl;
    BigDecimal passWithoutAssessmentControl;
    BigDecimal courseWorkControl;


    public int getBaseFertileUnits() {
        return base ? fertileUnits : 0;
    }

    public int getVariatyFertileUnits() {
        return !base ? fertileUnits : 0;
    }

    @AttributeOverrides({
            @AttributeOverride(name = "intensityCycle1", column = @Column(name = "year1_cycle1_intensity")),
            @AttributeOverride(name = "trainingHoursCycle1", column = @Column(name = "year1_cycle1_training_hours")),
            @AttributeOverride(name = "selfStudyHoursCycle1", column = @Column(name = "year1_cycle1_self_study_hours")),
            @AttributeOverride(name = "intensityCycle2", column = @Column(name = "year1_cycle2_intensity")),
            @AttributeOverride(name = "trainingHoursCycle2", column = @Column(name = "year1_cycle2_training_hours")),
            @AttributeOverride(name = "selfStudyHoursCycle2", column = @Column(name = "year1_cycle2_self_study_hours"))
    })
    @Embedded
    StudyYear studyYear1 = new StudyYear();

    @AttributeOverrides({
            @AttributeOverride(name = "intensityCycle1", column = @Column(name = "year2_cycle1_intensity")),
            @AttributeOverride(name = "trainingHoursCycle1", column = @Column(name = "year2_cycle1_training_hours")),
            @AttributeOverride(name = "selfStudyHoursCycle1", column = @Column(name = "year2_cycle1_self_study_hours")),
            @AttributeOverride(name = "intensityCycle2", column = @Column(name = "year2_cycle2_intensity")),
            @AttributeOverride(name = "trainingHoursCycle2", column = @Column(name = "year2_cycle2_training_hours")),
            @AttributeOverride(name = "selfStudyHoursCycle2", column = @Column(name = "year2_cycle2_self_study_hours"))
    })
    @Embedded
    StudyYear studyYear2 = new StudyYear();

    @AttributeOverrides({
            @AttributeOverride(name = "intensityCycle1", column = @Column(name = "year3_cycle1_intensity")),
            @AttributeOverride(name = "trainingHoursCycle1", column = @Column(name = "year3_cycle1_training_hours")),
            @AttributeOverride(name = "selfStudyHoursCycle1", column = @Column(name = "year3_cycle1_self_study_hours")),
            @AttributeOverride(name = "intensityCycle2", column = @Column(name = "year3_cycle2_intensity")),
            @AttributeOverride(name = "trainingHoursCycle2", column = @Column(name = "year3_cycle2_training_hours")),
            @AttributeOverride(name = "selfStudyHoursCycle2", column = @Column(name = "year3_cycle2_self_study_hours"))
    })
    @Embedded
    StudyYear studyYear3 = new StudyYear();

    @AttributeOverrides({
            @AttributeOverride(name = "intensityCycle1", column = @Column(name = "year4_cycle1_intensity")),
            @AttributeOverride(name = "trainingHoursCycle1", column = @Column(name = "year4_cycle1_training_hours")),
            @AttributeOverride(name = "selfStudyHoursCycle1", column = @Column(name = "year4_cycle1_self_study_hours")),
            @AttributeOverride(name = "intensityCycle2", column = @Column(name = "year4_cycle2_intensity")),
            @AttributeOverride(name = "trainingHoursCycle2", column = @Column(name = "year4_cycle2_training_hours")),
            @AttributeOverride(name = "selfStudyHoursCycle2", column = @Column(name = "year4_cycle2_self_study_hours"))
    })
    @Embedded
    StudyYear studyYear4 = new StudyYear();

    @AttributeOverrides({
            @AttributeOverride(name = "intensityCycle1", column = @Column(name = "year5_cycle1_intensity")),
            @AttributeOverride(name = "trainingHoursCycle1", column = @Column(name = "year5_cycle1_training_hours")),
            @AttributeOverride(name = "selfStudyHoursCycle1", column = @Column(name = "year5_cycle1_self_study_hours")),
            @AttributeOverride(name = "intensityCycle2", column = @Column(name = "year5_cycle2_intensity")),
            @AttributeOverride(name = "trainingHoursCycle2", column = @Column(name = "year5_cycle2_training_hours")),
            @AttributeOverride(name = "selfStudyHoursCycle2", column = @Column(name = "year5_cycle2_self_study_hours"))
    })
    @Embedded
    StudyYear studyYear5 = new StudyYear();

    @Override
    public String asString() {
        Optional<Syllabus> entity = Optional.ofNullable(this);
        return MoreObjects.toStringHelper("Учебный план")
                .add("Специалность", entity.map(Syllabus::getSpeciality).map(VisualEntity::asString).orElse(null))
                .add("Дисциплина", entity.map(Syllabus::getDiscipline).map(VisualEntity::asString).orElse(null))
                .toString();
    }

    public StudyYear getStudyYear1() {
        return studyYear1 != null ? studyYear1 : new StudyYear();
    }

    public StudyYear getStudyYear2() {
        return studyYear2 != null ? studyYear2 : new StudyYear();
    }

    public StudyYear getStudyYear3() {
        return studyYear3 != null ? studyYear3 : new StudyYear();
    }

    public StudyYear getStudyYear4() {
        return studyYear4 != null ? studyYear4 : new StudyYear();
    }

    public StudyYear getStudyYear5() {
        return studyYear5 != null ? studyYear5 : new StudyYear();
    }
}
