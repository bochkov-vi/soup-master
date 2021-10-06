package ru.itain.soup.syllabus.ui.syllabus;

import lombok.Data;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.syllabus.dto.entity.StudyYear;
import ru.itain.soup.syllabus.dto.entity.Syllabus;

import java.math.BigDecimal;
import java.util.Optional;

@Data
public class SyllabusRow {
    final Integer id;
    private final Integer seminars;
    private final Integer groupExercises;
    private final Integer groupLessons;
    private final Integer laboratoryWorks;
    private final Integer practicalLessons;
    private final Integer specialLessons;
    private final Integer courseWorks;
    private final Integer conferences;
    private final Integer practices;
    private final Integer tests;
    private final Integer credit;
    private final Integer examHours;
    private final Integer selfTraningHours;
    private final Double y1s1i;
    private final Double y1s2i;
    private final Double y2s1i;
    private final Double y2s2i;
    private final Double y3s1i;
    private final Double y3s2i;
    private final Double y4s1i;
    private final Double y4s2i;
    private final Double y5s1i;
    private final Double y5s2i;
    private final Integer y1s1s;
    private final Integer y1s2s;
    private final Integer y2s1s;
    private final Integer y2s2s;
    private final Integer y3s1s;
    private final Integer y3s2s;
    private final Integer y4s1s;
    private final Integer y4s2s;
    private final Integer y5s1s;
    private final Integer y5s2s;
    private final Integer y1s1t;
    private final Integer y1s2t;
    private final Integer y2s1t;
    private final Integer y2s2t;
    private final Integer y3s1t;
    private final Integer y3s2t;
    private final Integer y4s1t;
    private final Integer y4s2t;
    private final Integer y5s1t;
    private final Integer y5s2t;


    Double examControl;
    Double gradedCreditControl;
    Double passWithoutAssessmentControl;
    Double courseWorkControl;

    SyllabusRow(Syllabus syllabus) {
        Optional<Syllabus> o = Optional.ofNullable(syllabus);
        this.id = new Long(syllabus.getId()).intValue();
        this.index = syllabus.getIndex();
        if (index == null) {
            index = id.toString();
        }
        this.discipline = o.map(Syllabus::getDiscipline).map(VisualEntity::asString).orElse(null);
        this.fertileUnits = o.map(Syllabus::getFertileUnits).orElse(0);
        this.bFertileUnits = o.filter(Syllabus::isBase).map(Syllabus::getFertileUnits).filter(v -> v > 0).orElse(null);
        this.vFertileUnits = o.filter(s -> !s.isBase()).map(Syllabus::getFertileUnits).filter(v -> v > 0).orElse(null);
        this.fertileHours = fertileUnits * 36;
        this.totalHours = this.fertileHours;
        this.undefiningParameter = o.map(Syllabus::getUndefiningParameter).orElse(null);
        if (bFertileUnits != null) {
            hoursWithTeacher = this.fertileHours - this.fertileHours / 3 - undefiningParameter * 18;
        }
        this.lectures = o.map(Syllabus::getLectures).filter(v -> v > 0).orElse(null);
        this.seminars = o.map(Syllabus::getSeminars).filter(v -> v > 0).orElse(null);
        this.groupExercises = o.map(Syllabus::getGroupExercises).filter(v -> v > 0).orElse(null);
        this.groupLessons = o.map(Syllabus::getGroupLessons).filter(v -> v > 0).orElse(null);
        this.laboratoryWorks = o.map(Syllabus::getLaboratoryWorks).filter(v -> v > 0).orElse(null);
        this.practicalLessons = o.map(Syllabus::getPracticalLessons).filter(v -> v > 0).orElse(null);
        this.specialLessons = o.map(Syllabus::getSpecialLessons).filter(v -> v > 0).orElse(null);
        this.courseWorks = o.map(Syllabus::getCourseWorks).filter(v -> v > 0).orElse(null);
        this.conferences = o.map(Syllabus::getConferences).filter(v -> v > 0).orElse(null);
        this.practices = o.map(Syllabus::getPractices).filter(v -> v > 0).orElse(null);
        this.tests = o.map(Syllabus::getTests).filter(v -> v > 0).orElse(null);
        this.credit = o.map(Syllabus::getCredit).filter(v -> v > 0).orElse(null);
        this.examHours = Optional.ofNullable(undefiningParameter).map(v -> v * 24).orElse(null);
        this.selfTraningHours = Optional.of(Optional.ofNullable(totalHours).orElse(0) - Optional.ofNullable(hoursWithTeacher).orElse(0) - Optional.ofNullable(examHours).orElse(0)).filter(v -> v > 0).orElse(null);

        //==================================

        this.y1s1i = o.map(Syllabus::getStudyYear1).map(StudyYear::getIntensityCycle1).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);
        this.y1s2i = o.map(Syllabus::getStudyYear1).map(StudyYear::getIntensityCycle2).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);

        this.y2s1i = o.map(Syllabus::getStudyYear2).map(StudyYear::getIntensityCycle1).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);
        this.y2s2i = o.map(Syllabus::getStudyYear2).map(StudyYear::getIntensityCycle2).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);

        this.y3s1i = o.map(Syllabus::getStudyYear3).map(StudyYear::getIntensityCycle1).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);
        this.y3s2i = o.map(Syllabus::getStudyYear3).map(StudyYear::getIntensityCycle2).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);

        this.y4s1i = o.map(Syllabus::getStudyYear4).map(StudyYear::getIntensityCycle1).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);
        this.y4s2i = o.map(Syllabus::getStudyYear5).map(StudyYear::getIntensityCycle2).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);

        this.y5s1i = o.map(Syllabus::getStudyYear5).map(StudyYear::getIntensityCycle1).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);
        this.y5s2i = o.map(Syllabus::getStudyYear5).map(StudyYear::getIntensityCycle2).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);

        this.y1s1s = o.map(Syllabus::getStudyYear1).map(StudyYear::getSelfStudyHoursCycle1).filter(v -> v > 0).orElse(null);
        this.y1s2s = o.map(Syllabus::getStudyYear1).map(StudyYear::getSelfStudyHoursCycle2).filter(v -> v > 0).orElse(null);

        this.y2s1s = o.map(Syllabus::getStudyYear2).map(StudyYear::getSelfStudyHoursCycle1).filter(v -> v > 0).orElse(null);
        this.y2s2s = o.map(Syllabus::getStudyYear2).map(StudyYear::getSelfStudyHoursCycle2).filter(v -> v > 0).orElse(null);

        this.y3s1s = o.map(Syllabus::getStudyYear3).map(StudyYear::getSelfStudyHoursCycle1).filter(v -> v > 0).orElse(null);
        this.y3s2s = o.map(Syllabus::getStudyYear3).map(StudyYear::getSelfStudyHoursCycle2).filter(v -> v > 0).orElse(null);

        this.y4s1s = o.map(Syllabus::getStudyYear4).map(StudyYear::getSelfStudyHoursCycle1).filter(v -> v > 0).orElse(null);
        this.y4s2s = o.map(Syllabus::getStudyYear4).map(StudyYear::getSelfStudyHoursCycle2).filter(v -> v > 0).orElse(null);

        this.y5s1s = o.map(Syllabus::getStudyYear5).map(StudyYear::getSelfStudyHoursCycle1).filter(v -> v > 0).orElse(null);
        this.y5s2s = o.map(Syllabus::getStudyYear5).map(StudyYear::getSelfStudyHoursCycle2).filter(v -> v > 0).orElse(null);

        this.y1s1t = o.map(Syllabus::getStudyYear1).map(StudyYear::getTrainingHoursCycle1).filter(v -> v > 0).orElse(null);
        this.y1s2t = o.map(Syllabus::getStudyYear1).map(StudyYear::getTrainingHoursCycle2).filter(v -> v > 0).orElse(null);

        this.y2s1t = o.map(Syllabus::getStudyYear2).map(StudyYear::getTrainingHoursCycle1).filter(v -> v > 0).orElse(null);
        this.y2s2t = o.map(Syllabus::getStudyYear2).map(StudyYear::getTrainingHoursCycle2).filter(v -> v > 0).orElse(null);

        this.y3s1t = o.map(Syllabus::getStudyYear3).map(StudyYear::getTrainingHoursCycle1).filter(v -> v > 0).orElse(null);
        this.y3s2t = o.map(Syllabus::getStudyYear3).map(StudyYear::getTrainingHoursCycle2).filter(v -> v > 0).orElse(null);

        this.y4s1t = o.map(Syllabus::getStudyYear4).map(StudyYear::getTrainingHoursCycle1).filter(v -> v > 0).orElse(null);
        this.y4s2t = o.map(Syllabus::getStudyYear4).map(StudyYear::getTrainingHoursCycle2).filter(v -> v > 0).orElse(null);

        this.y5s1t = o.map(Syllabus::getStudyYear5).map(StudyYear::getTrainingHoursCycle1).filter(v -> v > 0).orElse(null);
        this.y5s2t = o.map(Syllabus::getStudyYear5).map(StudyYear::getTrainingHoursCycle2).filter(v -> v > 0).orElse(null);


        //========================
        this.examControl = o.map(Syllabus::getExamControl).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);
        this.gradedCreditControl = o.map(Syllabus::getGradedCreditControl).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);
        this.passWithoutAssessmentControl = o.map(Syllabus::getPassWithoutAssessmentControl).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);
        this.courseWorkControl = o.map(Syllabus::getCourseWorkControl).map(BigDecimal::doubleValue).filter(v -> v > 0).orElse(null);

    }

    String index;
    String discipline;
    Integer bFertileUnits;
    Integer vFertileUnits;
    Integer fertileUnits;
    Integer fertileHours;
    Integer totalHours;
    Integer hoursWithTeacher;
    Integer undefiningParameter;
    Integer lectures;

}
