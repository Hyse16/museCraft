package com.musecraft.brand.repository;

import com.musecraft.brand.domain.Brand;
import com.musecraft.brand.domain.BrandStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByCreatorId(Long creatorId);
    boolean existsByCreatorId(Long creatorId);
    Page<Brand> findByStatus(BrandStatus status, Pageable pageable);
    List<Brand> findAllByStatus(BrandStatus status);
}
