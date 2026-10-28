package com.example.RIyathi.service;

import com.example.RIyathi.entity.Clinician;
import com.example.RIyathi.entity.Department;
import com.example.RIyathi.repository.ClinicianRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClinicianService {
    @Autowired private ClinicianRepository repository;
    
    public void saveBulkFromExcel(MultipartFile file) {
        try {
            List<Clinician> list = new ArrayList<>();
            InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Clinician entity = new Clinician();
                entity.setClinicianId(formatter.formatCellValue(row.getCell(0)));
                entity.setClinicianName(formatter.formatCellValue(row.getCell(1)));
                
                String deptIdStr = formatter.formatCellValue(row.getCell(2));
                if (deptIdStr != null && !deptIdStr.isEmpty()) {
                    Department d = new Department();
                    try {
                        d.setId((long) Double.parseDouble(deptIdStr));
                    } catch (NumberFormatException e) {
                        d.setId(Long.parseLong(deptIdStr));
                    }
                    entity.setDepartment(d);
                }
                list.add(entity);
            }
            workbook.close();
            repository.saveAll(list);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store Excel data: " + e.getMessage());
        }
    }
    
    public List<Clinician> getAll() { return repository.findAll(); }
    public Clinician save(Clinician entity) { return repository.save(entity); }
    public void delete(Long id) { repository.deleteById(id); }
}
