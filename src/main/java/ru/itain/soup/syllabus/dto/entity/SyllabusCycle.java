package ru.itain.soup.syllabus.dto.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
@Data
public class SyllabusCycle {

    private BigDecimal intensity;

    private Integer trainingHours;

    private Integer selfStudyHours;
}
