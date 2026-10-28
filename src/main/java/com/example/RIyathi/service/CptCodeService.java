package com.example.RIyathi.service;

import com.example.RIyathi.entity.CptCode;
import com.example.RIyathi.repository.CptCodeRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class CptCodeService {
    @Autowired private CptCodeRepository repository;
    
    public void saveBulkFromExcel(MultipartFile file) {
        try {
            List<CptCode> list = new ArrayList<>();
            InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                CptCode entity = new CptCode();
                entity.setCptCode(formatter.formatCellValue(row.getCell(0)));
                entity.setCptCategory(formatter.formatCellValue(row.getCell(1)));
                list.add(entity);
            }
            workbook.close();
            repository.saveAll(list);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store Excel data: " + e.getMessage());
        }
    }
    
    public List<CptCode> getAll() { return repository.findAll(); }
    public CptCode save(CptCode entity) { return repository.save(entity); }
    public void delete(Long id) { repository.deleteById(id); }
}
