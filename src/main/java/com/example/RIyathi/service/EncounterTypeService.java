package com.example.RIyathi.service;

import com.example.RIyathi.entity.EncounterType;
import com.example.RIyathi.repository.EncounterTypeRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class EncounterTypeService {
    @Autowired private EncounterTypeRepository repository;
    
    public void saveBulkFromExcel(MultipartFile file) {
        try {
            List<EncounterType> list = new ArrayList<>();
            InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                EncounterType entity = new EncounterType();
                String encounterTypeNumStr = formatter.formatCellValue(row.getCell(0));
                if (encounterTypeNumStr != null && !encounterTypeNumStr.isEmpty()) {
                    try {
                        entity.setEncounterTypeNum((int) Double.parseDouble(encounterTypeNumStr));
                    } catch (NumberFormatException e) {
                        entity.setEncounterTypeNum(Integer.parseInt(encounterTypeNumStr));
                    }
                }
                entity.setEncounterDesc(formatter.formatCellValue(row.getCell(1)));
                entity.setPatientType(formatter.formatCellValue(row.getCell(2)));
                list.add(entity);
            }
            workbook.close();
            repository.saveAll(list);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store Excel data: " + e.getMessage());
        }
    }
    
    public List<EncounterType> getAll() { return repository.findAll(); }
    public EncounterType save(EncounterType entity) { return repository.save(entity); }
    public void delete(Long id) { repository.deleteById(id); }
}
