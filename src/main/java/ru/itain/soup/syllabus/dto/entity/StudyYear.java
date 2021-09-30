package ru.itain.soup.syllabus.dto.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Data
@Embeddable
public class StudyYear {

    private BigDecimal intensityCycle1;

    private Integer trainingHoursCycle1;

    private Integer selfStudyHoursCycle1;

    private BigDecimal intensityCycle2;

    private Integer trainingHoursCycle2;

    private Integer selfStudyHoursCycle2;
}
