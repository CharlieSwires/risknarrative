package com.charlie.truproxy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.charlie.truproxy.CompanyEntity.OfficerEntity;

@Repository
public interface OfficerRepository extends JpaRepository<CompanyEntity.OfficerEntity, String> {
	List<OfficerEntity> findAllByCompanyId(String id);
}
