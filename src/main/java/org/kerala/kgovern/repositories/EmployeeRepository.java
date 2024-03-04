package org.kerala.kgovern.repositories;

import org.kerala.kgovern.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    List<Employee> findByDepartment(String departmentName);

    List<Employee> findByDepartmentAndDistrict(String department,String district);

    Employee findEmployeeByEmployeeUsername(String username);
}
