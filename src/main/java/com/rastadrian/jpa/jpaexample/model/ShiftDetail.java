package com.rastadrian.jpa.jpaexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Getter
@Setter
@Entity
public class ShiftDetail {

    @Id
    @GeneratedValue
    @Column(name = "Id")
    private Integer id;

    @ManyToMany(mappedBy = "shiftDetails")
    private List<Shift> shift;

    @Column(name = "Message")
    private String message;
}
