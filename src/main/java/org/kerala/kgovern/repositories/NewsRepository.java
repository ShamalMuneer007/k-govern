package org.kerala.kgovern.repositories;

import org.kerala.kgovern.entities.DepartmentNews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<DepartmentNews,Long> {
    List<DepartmentNews> findByDepartmentAndDistrict(String department, String district);

    List<DepartmentNews> findByDepartment(String department);
}
