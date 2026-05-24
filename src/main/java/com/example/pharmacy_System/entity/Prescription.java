package com.example.pharmacy_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Lob
    @Column(name = "file_data")
    private byte[] fileData;

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "clinic_name")
    private String clinicName;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
    }
}