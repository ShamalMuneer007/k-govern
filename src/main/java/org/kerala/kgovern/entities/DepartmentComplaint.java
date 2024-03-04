package org.kerala.kgovern.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentComplaint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long complaintId;
    @ManyToOne
    private User user;
    private String department;
    private String complaintSubject;
    private String complaint;
    private String district;

}
