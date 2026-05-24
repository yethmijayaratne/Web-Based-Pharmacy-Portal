package com.example.pharmacy_System.service;

import com.example.pharmacy_System.entity.Prescription;
import com.example.pharmacy_System.entity.User;
import com.example.pharmacy_System.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private UserService userService;

    public Prescription uploadPrescription(Long userId, MultipartFile file, String doctorName, String clinicName, String notes) throws IOException {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Prescription prescription = Prescription.builder()
                .user(user)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .fileData(file.getBytes())
                .doctorName(doctorName)
                .clinicName(clinicName)
                .notes(notes)
                .status("PENDING")
                .uploadDate(LocalDateTime.now())
                .build();

        return prescriptionRepository.save(prescription);
    }

    public List<Prescription> getUserPrescriptions(Long userId) {
        return prescriptionRepository.findByUserIdOrderByUploadDateDesc(userId);
    }

    public Optional<Prescription> getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id);
    }

    public Prescription updatePrescriptionStatus(Long id, String status) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        prescription.setStatus(status);
        prescription.setProcessedDate(LocalDateTime.now());

        return prescriptionRepository.save(prescription);
    }

    public boolean deletePrescription(Long id) {
        if (prescriptionRepository.existsById(id)) {
            prescriptionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Prescription> getPrescriptionsByStatus(String status) {
        return prescriptionRepository.findByStatusOrderByUploadDateAsc(status);
    }

    public long getUserPrescriptionCount(Long userId) {
        return prescriptionRepository.countByUserId(userId);
    }
}