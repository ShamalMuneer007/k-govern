package org.kerala.kgovern.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long departmentId;
    @OneToOne(cascade = CascadeType.REMOVE)
    private User employee;
    private boolean isHead;
    private String district;
    private String department;
}
