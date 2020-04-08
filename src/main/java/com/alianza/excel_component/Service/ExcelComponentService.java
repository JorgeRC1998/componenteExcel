package com.alianza.excel_component.Service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alianza.excel_component.DAO.ExcelComponentDao;
import com.alianza.excel_component.DTO.ExcelResponseDto;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExcelComponentService {

    @Autowired
    ExcelComponentDao excelComponentDao;

    ExcelResponseDto objExcelResponse = new ExcelResponseDto();

    public ExcelResponseDto getResponse(HttpServletRequest request, Map<String, String> headers, HttpServletResponse responseFile)
            throws IOException, ParseException {
        objExcelResponse = excelComponentDao.getExcel(request, headers, responseFile);
        return objExcelResponse;
    }
}