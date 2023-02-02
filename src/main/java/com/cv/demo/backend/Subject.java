package com.cv.demo.backend;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "SUBJECT")
public class Subject {
    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ABBREVIATION")
    private String abbreviation;

    @Column(name = "TEACHER")
    private String teacher;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "SUBJECT_ID")
    @ToString.Exclude
    private List<Project> projects = new ArrayList<>();

    @Column(name = "CREATED_AT", columnDefinition = "TIMESTAMP(3)")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "MODIFIED_AT", columnDefinition = "TIMESTAMP(3)")
    @UpdateTimestamp
    private LocalDateTime modifiedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Subject subject = (Subject) o;
        return Objects.equals(id, subject.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
