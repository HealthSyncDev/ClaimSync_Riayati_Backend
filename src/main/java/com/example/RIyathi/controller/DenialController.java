package com.example.RIyathi.controller;

import com.example.RIyathi.entity.Denial;
import com.example.RIyathi.service.DenialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/denials")
public class DenialController {
    @Autowired private DenialService service;
    
    @GetMapping
    public List<Denial> getAll() { return service.getAll(); }
    
    @PostMapping
    public Denial create(@RequestBody Denial entity) { return service.save(entity); }
    
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
