package org.kerala.kgovern.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kerala.kgovern.Enums.ComplaintStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime issuedDateTime;
    private LocalDate rejectedAt;
    private LocalDate inProgressAt;
    private LocalDate resolvedAt;
    private LocalDate underConsiderationAt;
    private ComplaintStatus status;
    private String response;
    private String district;
    private String userResponse;
    private String higherResponse;

}
