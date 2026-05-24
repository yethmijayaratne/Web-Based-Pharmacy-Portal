package com.example.pharmacy_System.repository;

import com.example.pharmacy_System.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByUserIdOrderByUploadDateDesc(Long userId);

    List<Prescription> findByStatusOrderByUploadDateAsc(String status);

    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Prescription p WHERE p.user.id = :userId AND p.status = :status")
    List<Prescription> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
}