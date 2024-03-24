package org.kerala.kgovern.repositories;

import org.kerala.kgovern.entities.DepartmentComplaint;
import org.kerala.kgovern.entities.DepartmentMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<DepartmentMessage,Long> {

    List<DepartmentMessage> findByComplaintAndDepartmentAndDistrict(DepartmentComplaint complaint, String department, String district);
}
