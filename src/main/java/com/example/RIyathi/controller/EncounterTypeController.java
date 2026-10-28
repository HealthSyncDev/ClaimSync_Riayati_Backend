package com.example.RIyathi.controller;

import com.example.RIyathi.entity.EncounterType;
import com.example.RIyathi.service.EncounterTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/encounter-types")
public class EncounterTypeController {
    @Autowired private EncounterTypeService service;
    
    @GetMapping
    public List<EncounterType> getAll() { return service.getAll(); }
    
    @PostMapping
    public EncounterType create(@RequestBody EncounterType entity) { return service.save(entity); }
    
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
