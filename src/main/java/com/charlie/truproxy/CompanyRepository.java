package com.charlie.truproxy;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, String> {
    List<CompanyEntity> findByTitleIgnoreCase(String name);

	Optional<CompanyEntity> findByCompanyNumber(String string);

}
