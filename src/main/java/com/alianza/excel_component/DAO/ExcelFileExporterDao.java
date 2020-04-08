package com.alianza.excel_component.DAO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alianza.excel_component.DTO.ExcelResponseDto;
import com.alianza.excel_component.Entity.CustomerEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;

public class ExcelFileExporterDao {

	ExcelResponseDto objResponseDto = new ExcelResponseDto();
	
	// Esta funcion permite generar excel dummy con informacion quemada
	public static ByteArrayInputStream contactListToExcelFile(List<CustomerEntity> customers) {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Customers");

			Row row = sheet.createRow(0);
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			// Creating header
			Cell cell = row.createCell(0);
			cell.setCellValue("First Name");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(1);
			cell.setCellValue("Last Name");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(2);
			cell.setCellValue("Mobile");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(3);
			cell.setCellValue("Email");
			cell.setCellStyle(headerCellStyle);

			// Llena la data en el sheet
			for (int i = 0; i < customers.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(customers.get(i).getFirstName());
				dataRow.createCell(1).setCellValue(customers.get(i).getLastName());
				dataRow.createCell(2).setCellValue(customers.get(i).getMobileNumber());
				dataRow.createCell(3).setCellValue(customers.get(i).getEmail());
			}

			// Ajusta automaticamente las columnas de acuerdo al tamaño del dato
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static ByteArrayInputStream JsonResponseToExcelFile(ResponseEntity<String> params, String sheetN,String fileN) throws ParseException {
		try(Workbook workbook = new XSSFWorkbook()){
			Sheet sheet = workbook.createSheet(sheetN);
			String body = params.getBody().replaceAll("\n", "").replaceAll("\t", "");
			JSONParser parser = new JSONParser();
			JSONArray json = null;
			int cont = 0;
			
			try{
				json = (JSONArray) parser.parse(body);
			}catch(Exception e){
				String strAux = "[" + body + "]";
				json = (JSONArray) parser.parse(strAux);
			}

			JSONObject obj = null;
			if(json.size()>0){
				obj = (JSONObject) json.get(0);
			}

			Row row = sheet.createRow(0);
	        CellStyle headerCellStyle = workbook.createCellStyle();
	        headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			
			Iterator<?> keys = obj.keySet().iterator();
			while(keys.hasNext()) {
				Cell cell = row.createCell(cont);
				cell.setCellValue(keys.next().toString());
				cell.setCellStyle(headerCellStyle);
				cont++;
			}

				for(int j=0; j<json.size(); j++){
					Row dataRow = sheet.createRow(j + 1);
					ArrayList<Object> list = handleList(json, j);
					for (int i=0; i<cont; i++){
						dataRow.createCell(i).setCellValue(list.get(i).toString());
					}
				}

			for(int i = 0; i < cont; i++){
				sheet.autoSizeColumn(i);
			}
	        
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        workbook.write(outputStream);
	        return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static ArrayList<Object> handleList(JSONArray jsonParam, int index){

		ArrayList<Object> list = new ArrayList<Object>();     
		Object aux = jsonParam.get(index);
		JSONObject aux2 = (JSONObject) aux;
		ObjectMapper oMapper = new ObjectMapper();

		Map<String, Object> map = oMapper.convertValue(aux2, Map.class);
		for (String key : map.keySet()) {
			//System.out.println(key + " : " + map.get(key).toString());
			list.add(map.get(key).toString());
		 }

		return list;
	}
}
