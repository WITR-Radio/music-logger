package edu.rit.witr.musiclogger.entities;

import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @NonNull
    @Column(name = "created_at", nullable = false)
    private java.sql.Date created;

    @NonNull
    @Column(name = "updated_at", nullable = false)
    private java.sql.Date updated;

    public Group() {}

    public Group(String name, @NonNull Date created, @NonNull Date updated) {
        this.name = name;
        this.created = created;
        this.updated = updated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public @NonNull Date getCreated() {
        return created;
    }

    public void setCreated(@NonNull Date created) {
        this.created = created;
    }

    public @NonNull Date getUpdated() {
        return updated;
    }

    public void setUpdated(@NonNull Date updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }
}
