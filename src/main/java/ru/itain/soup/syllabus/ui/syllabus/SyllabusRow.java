package ru.itain.soup.syllabus.ui.syllabus;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.syllabus.dto.entity.StudyYear;
import ru.itain.soup.syllabus.dto.entity.Syllabus;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
public class SyllabusRow {
    private Integer id;
    private Integer seminars;
    private Integer groupExercises;
    private Integer groupLessons;
    private Integer laboratoryWorks;
    private Integer practicalLessons;
    private Integer specialLessons;
    private Integer courseWorks;
    private Integer conferences;
    private Integer practices;
    private Integer tests;
    private Integer credit;
    private Integer examHours;
    private Integer selfTraningHours;
    private Double y1s1i;
    private Double y1s2i;
    private Double y2s1i;
    private Double y2s2i;
    private Double y3s1i;
    private Double y3s2i;
    private Double y4s1i;
    private Double y4s2i;
    private Double y5s1i;
    private Double y5s2i;
    private Integer y1s1s;
    private Integer y1s2s;
    private Integer y2s1s;
    private Integer y2s2s;
    private Integer y3s1s;
    private Integer y3s2s;
    private Integer y4s1s;
    private Integer y4s2s;
    private Integer y5s1s;
    private Integer y5s2s;
    private Integer y1s1t;
    private Integer y1s2t;
    private Integer y2s1t;
    private Integer y2s2t;
    private Integer y3s1t;
    private Integer y3s2t;
    private Integer y4s1t;
    private Integer y4s2t;
    private Integer y5s1t;
    private Integer y5s2t;


    Double examControl;
    Double gradedCreditControl;
    Double passWithoutAssessmentControl;
    Double courseWorkControl;

    public static SyllabusRow total(List<SyllabusRow>... data) {
        SyllabusRow row = new SyllabusRow();
        List<SyllabusRow> rows = Stream.of(data).flatMap(Collection::stream).collect(Collectors.toList());
        row.discipline = ("Итого");
        row.seminars = rows.stream().map(s -> s.seminars).filter(Objects::nonNull).map(v -> v).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.groupExercises = rows.stream().map(s -> s.groupExercises).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.groupLessons = rows.stream().map(s -> s.groupLessons).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.laboratoryWorks = rows.stream().map(s -> s.laboratoryWorks).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.practicalLessons = rows.stream().map(s -> s.practicalLessons).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.specialLessons = rows.stream().map(s -> s.specialLessons).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.courseWorks = rows.stream().map(s -> s.courseWorks).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.conferences = rows.stream().map(s -> s.conferences).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.practices = rows.stream().map(s -> s.practices).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.tests = rows.stream().map(s -> s.tests).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.credit = rows.stream().map(s -> s.credit).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.examHours = rows.stream().map(s -> s.examHours).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.selfTraningHours = rows.stream().map(s -> s.selfTraningHours).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y1s1i = rows.stream().map(s -> s.y1s1i).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.y1s2i = rows.stream().map(s -> s.y1s2i).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.y2s1i = rows.stream().map(s -> s.y2s1i).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.y2s2i = rows.stream().map(s -> s.y2s2i).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.y3s1i = rows.stream().map(s -> s.y3s1i).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.y3s2i = rows.stream().map(s -> s.y3s2i).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.y4s1i = rows.stream().map(s -> s.y4s1i).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.y4s2i = rows.stream().map(s -> s.y4s2i).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.y5s1i = rows.stream().map(s -> s.y5s1i).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.y5s2i = rows.stream().map(s -> s.y5s2i).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.y1s1s = rows.stream().map(s -> s.y1s1s).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y1s2s = rows.stream().map(s -> s.y1s2s).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y2s1s = rows.stream().map(s -> s.y2s1s).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y2s2s = rows.stream().map(s -> s.y2s2s).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y3s1s = rows.stream().map(s -> s.y3s1s).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y3s2s = rows.stream().map(s -> s.y3s2s).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y4s1s = rows.stream().map(s -> s.y4s1s).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y4s2s = rows.stream().map(s -> s.y4s2s).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y5s1s = rows.stream().map(s -> s.y5s1s).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y5s2s = rows.stream().map(s -> s.y5s2s).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y1s1t = rows.stream().map(s -> s.y1s1t).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y1s2t = rows.stream().map(s -> s.y1s2t).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y2s1t = rows.stream().map(s -> s.y2s1t).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y2s2t = rows.stream().map(s -> s.y2s2t).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y3s1t = rows.stream().map(s -> s.y3s1t).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y3s2t = rows.stream().map(s -> s.y3s2t).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y4s1t = rows.stream().map(s -> s.y4s1t).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y4s2t = rows.stream().map(s -> s.y4s2t).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y5s1t = rows.stream().map(s -> s.y5s1t).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.y5s2t = rows.stream().map(s -> s.y5s2t).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.examControl = rows.stream().map(s -> s.examControl).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.gradedCreditControl = rows.stream().map(s -> s.gradedCreditControl).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.passWithoutAssessmentControl = rows.stream().map(s -> s.passWithoutAssessmentControl).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.courseWorkControl = rows.stream().map(s -> s.courseWorkControl).filter(Objects::nonNull).mapToDouble(v -> v).sum();
        row.bFertileUnits = rows.stream().map(s -> s.bFertileUnits).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.vFertileUnits = rows.stream().map(s -> s.vFertileUnits).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.fertileUnits = rows.stream().map(s -> s.fertileUnits).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.fertileHours = rows.stream().map(s -> s.fertileHours).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.totalHours = rows.stream().map(s -> s.totalHours).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.hoursWithTeacher = rows.stream().map(s -> s.hoursWithTeacher).filter(Objects::nonNull).mapToInt(v -> v).sum();
        //row.undefiningParameter = rows.stream().map(s -> s.undefiningParameter).filter(Objects::nonNull).mapToInt(v -> v).sum();
        row.lectures = rows.stream().map(s -> s.lectures).filter(Objects::nonNull).mapToInt(v -> v).sum();


        return row;
    }

   public SyllabusRow(Syllabus syllabus) {
        Optional<Syllabus> o = Optional.ofNullable(syllabus);
        this.id = new Long(syllabus.getId()).intValue();
        this.index = syllabus.getIndex();
        if (Strings.isNullOrEmpty(index)) {
            index = id.toString();
        }
        this.discipline = o.map(Syllabus::getDiscipline).map(VisualEntity::asString).orElse(null);
        this.fertileUnits = o.map(Syllabus::getFertileUnits).orElse(0);
        this.bFertileUnits = o.filter(Syllabus::isBase).map(Syllabus::getFertileUnits).filter(v -> v > 0).orElse(null);
        this.vFertileUnits = o.filter(s -> !s.isBase()).map(Syllabus::getFertileUnits).filter(v -> v > 0).orElse(null);
        this.fertileHours = fertileUnits * 36;
        this.totalHours = this.fertileHours;
        this.undefiningParameter = o.map(Syllabus::getUndefiningParameter).orElse(null);
        if (fertileHours != null) {
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
