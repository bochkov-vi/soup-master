package ru.itain.soup.common.dto.users;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.itain.soup.syllabus.dto.entity.Department;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Преподаватель.
 */
@Entity
@Table(schema = "users")
@Getter
@Setter
@Accessors(chain = true)
public class Tutor extends Person {
    @ManyToOne
    private Position position;
    @ManyToOne
    private Rank rank;
    private boolean createStudentProfile;
    private boolean createStudentGroups;
    private boolean updateStudentProfile;
    private boolean deleteStudentProfile;
    private boolean createInteractiveMaterials;
    private boolean createUmm;

    @ManyToOne
    @JoinTable(schema = "users",name = "tutor_department", joinColumns = {@JoinColumn(name = "department_id")}, inverseJoinColumns = {@JoinColumn(name = "tutor_id")})
    private Department department;

    public Tutor() {
    }

    public Tutor(
            @NotNull String firstName,
            @NotNull String lastName,
            @NotNull String middleName,
            @NotNull Position position,
            @NotNull Rank rank,
            @NotNull User user
    ) {
        super(firstName, lastName, middleName, user);
        this.createStudentProfile = false;
        this.createStudentGroups = false;
        this.updateStudentProfile = false;
        this.deleteStudentProfile = false;
        this.createInteractiveMaterials = false;
        this.createUmm = false;
        this.position = position;
        this.rank = rank;
    }

}
