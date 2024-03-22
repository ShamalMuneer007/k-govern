package org.kerala.kgovern.repositories;

import org.kerala.kgovern.entities.DepartmentComplaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentComplaintRepository extends JpaRepository<DepartmentComplaint,Long> {
    List<DepartmentComplaint> findByDepartment(String department);

    List<DepartmentComplaint> findByDepartmentAndDistrict(String department,String district);

    List<DepartmentComplaint> findByUserUsername(String username);

}
