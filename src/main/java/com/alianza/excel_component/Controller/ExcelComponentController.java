package com.alianza.excel_component.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alianza.excel_component.DAO.ExcelFileExporterDao;
import com.alianza.excel_component.DTO.ExcelResponseDto;
import com.alianza.excel_component.Entity.CustomerEntity;
import com.alianza.excel_component.Service.ExcelComponentService;

import org.apache.commons.compress.utils.IOUtils;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/excelcomponent")
public class ExcelComponentController {

    @Autowired
    ExcelComponentService excelComponentService;

    // Recurso para generar archivos excel
    @PostMapping(path = "/v1/excel", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ExcelResponseDto generarExcel(HttpServletRequest request, @RequestHeader Map<String, String> headers, HttpServletResponse responseFile)
            throws IOException, ParseException {

        try{
            return excelComponentService.getResponse(request, headers, responseFile);
        }catch(Exception e){
            return null;
        }
    }

    // Ejemplo para generar archivos con objeto quemado
    @PostMapping("/download")
    public void downloadCsv(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=CustomerEntity.xlsx");
        ByteArrayInputStream stream = ExcelFileExporterDao.contactListToExcelFile(createTestData());
        IOUtils.copy(stream, response.getOutputStream());
    }

	private List<CustomerEntity> createTestData(){
    	List<CustomerEntity> customers = new ArrayList<CustomerEntity>();
    	customers.add(new CustomerEntity("Vernon", "Barlow", "0123456789", "test1@simplesolution.dev"));
    	customers.add(new CustomerEntity("Maud", "Brock", "0123456788", "test2@simplesolution.dev"));
    	customers.add(new CustomerEntity("Chyna", "Cowan", "0123456787", "test3@simplesolution.dev"));
    	customers.add(new CustomerEntity("Krisha", "Tierney", "0123456786", "test4@simplesolution.dev"));
    	customers.add(new CustomerEntity("Sherry", "Rosas", "0123456785", "test5@simplesolution.dev"));
    	return customers;
    }
}