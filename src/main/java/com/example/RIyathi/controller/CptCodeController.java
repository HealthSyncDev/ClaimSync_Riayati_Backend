package com.example.RIyathi.controller;

import com.example.RIyathi.entity.CptCode;
import com.example.RIyathi.service.CptCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/cpt-codes")
public class CptCodeController {
    @Autowired private CptCodeService service;
    
    @GetMapping
    public List<CptCode> getAll() { return service.getAll(); }
    
    @PostMapping
    public CptCode create(@RequestBody CptCode entity) { return service.save(entity); }
    
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
