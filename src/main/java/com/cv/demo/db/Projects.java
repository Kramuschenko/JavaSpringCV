package com.cv.demo.db;
import com.fasterxml.jackson.annotation.JsonValue;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "PROJECT")
public class Projects {
    @Id
    @GeneratedValue
    @Column(name = "Id" , nullable = false)
    private Long id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Comment")
    private String comment;


    public Projects() {
    }

    public Projects(String name) {
        this.name = name;
    }

    public Projects(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
