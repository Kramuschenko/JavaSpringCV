package com.cv.demo.db;
import com.fasterxml.jackson.annotation.JsonValue;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "PROJECT")
public class Projects {
    @Id
    @Column(name = "Id" , nullable = false)
    private int id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Comment")
    private String comment;


    public Projects() {
    }

    public Projects(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Projects(int id, String name, String comment) {
        this.id = id;
        this.name = name;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
