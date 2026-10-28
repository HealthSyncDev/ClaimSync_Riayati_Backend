package com.example.RIyathi.controller;

import com.example.RIyathi.entity.Clinician;
import com.example.RIyathi.service.ClinicianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/clinicians")
public class ClinicianController {
    @Autowired private ClinicianService service;
    
    @GetMapping
    public List<Clinician> getAll() { return service.getAll(); }
    
    @PostMapping
    public Clinician create(@RequestBody Clinician entity) { return service.save(entity); }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted Successfully");
    }
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        service.saveBulkFromExcel(file);
        return ResponseEntity.ok("File uploaded successfully.");
    }
}
