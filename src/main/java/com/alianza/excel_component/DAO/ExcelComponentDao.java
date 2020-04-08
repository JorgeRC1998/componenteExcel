package com.alianza.excel_component.DAO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alianza.excel_component.DTO.ExcelResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.compress.utils.IOUtils;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class ExcelComponentDao {

    // Objetos
    ExcelResponseDto objResponseDto = new ExcelResponseDto();
    HttpHeaders objHeaders = new HttpHeaders();
    Map<String, Object> map = new HashMap<>();
    ResponseEntity<String> response;
    HttpServletResponse responseFileInDao;

    public ExcelResponseDto getExcel(HttpServletRequest request, Map<String, String> headers, HttpServletResponse responseFile)
            throws IOException, ParseException {

        // Se obtienen headers necesarios para el consumo del recurso y el body
        String aux = request.getReader().lines().collect(Collectors.joining(System.lineSeparator())).replaceAll("\n", "").replaceAll("\t", "");
        String urlResource = headers.get("url-resource");
        String methodResource = headers.get("method-resource").toUpperCase();
        String sheetName = headers.get("sheet-name");
        String fileName = headers.get("file-name");

        // Organiza headers y body para la peticion
        objHeaders = handleHeaders(headers);
        map = handleBody(aux);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, objHeaders);

        // Llamado del recurso especificado en el header
        switch (methodResource) {
            case "GET":
                try {
                    response = restTemplate.getForEntity(urlResource, String.class, entity);
                    //String body = response.getBody();
                    //System.out.println("body -> " + body);
                    downloadCsv(responseFile, response, sheetName, fileName);
                    objResponseDto.setStatus("success");
                    objResponseDto.setDetails("Recurso: " + methodResource + " consumido correctamene, se genera excel: " + fileName);
                } catch (Exception e) {
                    System.out.println(e);
                    objResponseDto.setStatus("failed");
                    objResponseDto.setDetails("Recurso: " + methodResource + " consumido correctamene, NO se genera excel, razon: " + e);
                }
                break;
            case "POST":
                try {
                    response = restTemplate.postForEntity(urlResource, entity, String.class);
                    // String body = response.getBody();
                    // System.out.println("body -> " + body);
                    downloadCsv(responseFile, response, sheetName, fileName);
                    objResponseDto.setStatus("success");
                    objResponseDto.setDetails("Recurso: " + methodResource + " consumido correctamene, se genera excel: " + fileName);
                } catch (Exception e) {
                    System.out.println(e);
                    objResponseDto.setStatus("failed");
                    objResponseDto.setDetails("Recurso: " + methodResource + " consumido correctamene, NO se genera excel, razon: " + e);
                }
                break;
            case "PUT":
                // Do something
                break;
            case "DELETE":
                // Do something
                break;
            default:
                objResponseDto.setStatus("400");
                objResponseDto.setDetails("metodo http no reconocido");
        }
        return objResponseDto;
    }

    // Retorna los headers necesarios para el llamado al recurso y elimina los innecesarios
    public HttpHeaders handleHeaders(Map<String, String> param){
        HttpHeaders obj = new HttpHeaders();

        for (Map.Entry<String,String> entry : param.entrySet()){
            obj.add(entry.getKey(), entry.getValue());
        }
        obj.remove("url-resource");
        obj.remove("method-resource");
        obj.remove("sheet-name");
        obj.remove("file-name");

        return obj;
    }

    // Retorna el body de la peticion como un mapa de datos listo para pasar a la peticion
    public Map<String, Object> handleBody(String request) throws IOException, ParseException {
        Map<String, Object> obj = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        try{
            obj = mapper.readValue(request, Map.class);
        }catch(Exception e){
            System.out.println(e);
        }

        return obj;
    }

    public void downloadCsv(HttpServletResponse response, ResponseEntity<String> responseRecurso, String sheetN, String fileN) throws IOException, ParseException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileN + ".xlsx");
        ByteArrayInputStream stream = ExcelFileExporterDao.JsonResponseToExcelFile(responseRecurso, sheetN, fileN);
        IOUtils.copy(stream, response.getOutputStream());
    }
}