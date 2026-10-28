package com.example.RIyathi.service;

import com.example.RIyathi.entity.Denial;
import com.example.RIyathi.repository.DenialRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class DenialService {
    @Autowired private DenialRepository repository;
    
    public void saveBulkFromExcel(MultipartFile file) {
        try {
            List<Denial> list = new ArrayList<>();
            InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Denial entity = new Denial();
                entity.setDenialCode(formatter.formatCellValue(row.getCell(0)));
                entity.setDenialType(formatter.formatCellValue(row.getCell(1)));
                entity.setDenialReason(formatter.formatCellValue(row.getCell(2)));
                entity.setDenialCategory(formatter.formatCellValue(row.getCell(3)));
                entity.setCategory(formatter.formatCellValue(row.getCell(4)));
                list.add(entity);
            }
            workbook.close();
            repository.saveAll(list);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store Excel data: " + e.getMessage());
        }
    }
    
    public List<Denial> getAll() { return repository.findAll(); }
    public Denial save(Denial entity) { return repository.save(entity); }
    public void delete(Long id) { repository.deleteById(id); }
}
