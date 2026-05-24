package com.example.pharmacy_System.controller;

import com.example.pharmacy_System.entity.Prescription;
import com.example.pharmacy_System.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadPrescription(
            @RequestParam("prescriptionFile") MultipartFile file,
            @RequestParam(value = "doctorName", required = false) String doctorName,
            @RequestParam(value = "clinicName", required = false) String clinicName,
            @RequestParam(value = "notes", required = false) String notes,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "User not logged in");
            return ResponseEntity.ok(response);
        }

        try {
            // Validate file
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select a file to upload");
                return ResponseEntity.ok(response);
            }

            // Validate file size (10MB limit)
            if (file.getSize() > 10 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "File size must be less than 10MB");
                return ResponseEntity.ok(response);
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("application/pdf") &&
                    !contentType.startsWith("image/") &&
                    !contentType.equals("application/msword") &&
                    !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
                response.put("success", false);
                response.put("message", "Only PDF, images, and Word documents are allowed");
                return ResponseEntity.ok(response);
            }

            Prescription prescription = prescriptionService.uploadPrescription(userId, file, doctorName, clinicName, notes);

            response.put("success", true);
            response.put("message", "Prescription uploaded successfully");
            response.put("prescriptionId", prescription.getId());

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error uploading file: " + e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Upload failed: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-prescriptions")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserPrescriptions(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "User not logged in");
            return ResponseEntity.ok(response);
        }

        try {
            List<Prescription> prescriptions = prescriptionService.getUserPrescriptions(userId);

            List<Map<String, Object>> prescriptionData = prescriptions.stream().map(p -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", p.getId());
                data.put("fileName", p.getFileName());
                data.put("fileType", p.getFileType());
                data.put("fileSize", p.getFileSize());
                data.put("doctorName", p.getDoctorName());
                data.put("clinicName", p.getClinicName());
                data.put("notes", p.getNotes());
                data.put("status", p.getStatus());
                data.put("uploadDate", p.getUploadDate());
                data.put("processedDate", p.getProcessedDate());
                return data;
            }).collect(Collectors.toList());

            response.put("success", true);
            response.put("prescriptions", prescriptionData);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error loading prescriptions: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadPrescription(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Prescription prescription = prescriptionService.getPrescriptionById(id)
                    .orElseThrow(() -> new RuntimeException("Prescription not found"));

            // Check if the prescription belongs to the user
            if (!prescription.getUser().getId().equals(userId)) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok()
                    .header("Content-Type", prescription.getFileType())
                    .header("Content-Disposition", "attachment; filename=\"" + prescription.getFileName() + "\"")
                    .body(prescription.getFileData());

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletePrescription(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "User not logged in");
            return ResponseEntity.ok(response);
        }

        try {
            Optional<Prescription> prescriptionOpt = prescriptionService.getPrescriptionById(id);

            if (prescriptionOpt.isPresent()) {
                Prescription prescription = prescriptionOpt.get();

                // Check if the prescription belongs to the user
                if (!prescription.getUser().getId().equals(userId)) {
                    response.put("success", false);
                    response.put("message", "You are not authorized to delete this prescription");
                    return ResponseEntity.ok(response);
                }

                // Only allow deletion of PENDING prescriptions
                if (!"PENDING".equals(prescription.getStatus())) {
                    response.put("success", false);
                    response.put("message", "Only PENDING prescriptions can be deleted");
                    return ResponseEntity.ok(response);
                }

                boolean deleted = prescriptionService.deletePrescription(id);

                if (deleted) {
                    response.put("success", true);
                    response.put("message", "Prescription deleted successfully");
                } else {
                    response.put("success", false);
                    response.put("message", "Prescription not found");
                }
            } else {
                response.put("success", false);
                response.put("message", "Prescription not found");
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting prescription: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}