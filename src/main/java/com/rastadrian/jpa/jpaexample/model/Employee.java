package com.rastadrian.jpa.jpaexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@Entity
public class Employee {

    @Id
    @GeneratedValue
    @Column(name = "Id")
    private Integer id;

    @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private List<Shift> shifts;
}
