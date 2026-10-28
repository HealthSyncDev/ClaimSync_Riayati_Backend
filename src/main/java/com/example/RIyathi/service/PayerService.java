package com.example.RIyathi.service;

import com.example.RIyathi.entity.Payer;
import com.example.RIyathi.repository.PayerRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayerService {
    @Autowired private PayerRepository repository;
    
    public void saveBulkFromExcel(MultipartFile file) {
        try {
            List<Payer> list = new ArrayList<>();
            InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Payer entity = new Payer();
                entity.setPayerId(formatter.formatCellValue(row.getCell(0)));
                entity.setPayerName(formatter.formatCellValue(row.getCell(1)));
                list.add(entity);
            }
            workbook.close();
            repository.saveAll(list);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store Excel data: " + e.getMessage());
        }
    }
    
    public List<Payer> getAll() { return repository.findAll(); }
    public Payer save(Payer entity) { return repository.save(entity); }
    public void delete(Long id) { repository.deleteById(id); }
}
