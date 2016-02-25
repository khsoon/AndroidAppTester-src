package kr_ac_yonsei_mobilesw_UI;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;

import java.util.Vector;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ReadInfo {
	
		FileInputStream file=null;
		String fileName = null;
		String adb = null;
		Vector<String> resultInfo = new Vector<String>();
		Vector<Vector<String>> groupInfo = new Vector<Vector<String>>();
		
		public void start(String name){
			fileName = name;
			readFile(fileName);	
		}
		
		public Vector<String> getResultInfo(){
			return resultInfo;
		}
		
		public Vector<Vector<String>> getGroupInfo(){
			return groupInfo;
		}
		
		public Vector<Vector<String>> getLog(int index){
			
			Vector<Vector<String>> log = new Vector<Vector<String>>();
			System.out.println(fileName);
			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}		
			
			XSSFSheet sheet = workbook.getSheetAt(0);
					
			int rows = sheet.getPhysicalNumberOfRows();
			boolean logFlag = false;
			boolean readFlag = false;
			int logCount = 0;
			boolean outflag = false;
			for (int rowIndex=0; rowIndex<rows; rowIndex++){
				if(outflag==true)
					break;
				XSSFRow row = sheet.getRow(rowIndex);
				if (row != null) {
					XSSFCell cell = row.getCell(0);
			
					String value = "";
					
					if (cell == null) {
						continue;
					} 
					else {
						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_FORMULA:
							value = cell.getCellFormula();
							break;
						case XSSFCell.CELL_TYPE_NUMERIC:
							value = cell.getNumericCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_STRING:
							value = cell.getStringCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_BLANK:
							value = cell.getBooleanCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_ERROR:
							value = cell.getErrorCellValue() + "";
							break;
						}
					}
					if(value.equals("Fail Log") ) {
						logCount++;
						if(logCount == index + 1){
							logFlag = true;
							readFlag = true;
						}
					}  else if(logFlag == true){
						logFlag = false;
						adb = value;
					} 
					if(readFlag && value.equals("-")){
						outflag=true;
					}
					if( readFlag && ( value.equals("I")|| value.equals("W")|| value.equals("D")|| value.equals("E"))){
						
						Vector<String> tmp = new Vector<String>();
						for(int z=0; z<4; z++){	// 0,1,2,3
							XSSFCell cell3 = row.getCell(z);
							
							if (cell3 != null) {		
								switch (cell3.getCellType()) {
								case XSSFCell.CELL_TYPE_FORMULA:
									value = cell3.getCellFormula();
									break;
								case XSSFCell.CELL_TYPE_NUMERIC:
									value = cell3.getNumericCellValue() + "";
									break;
								case XSSFCell.CELL_TYPE_STRING:
									value = cell3.getStringCellValue() + "";
									break;
								case XSSFCell.CELL_TYPE_BLANK:
									value = cell3.getBooleanCellValue() + "";
									break;
								case XSSFCell.CELL_TYPE_ERROR:
									value = cell3.getErrorCellValue() + "";
									break;
								}
							}
							if (value.equalsIgnoreCase("false")) // 빈칸일때
								value = "";

							tmp.add(value);
						}
						log.add(tmp);
					} 
				}
				else {
					readFlag = false;
				}
			}
			
			return log;
		}
		
		public String getADB(){
			return adb;
		}
		
		public void readFile(String fileName2){
			
			// 1 : 변수 선언 및 초기화
			int rowIndex = 0;	// 행 인덱스
			boolean resultflag = false;
			boolean groupflag = false;
			
			// 2-1 : 파일 입력
			try {
				int index = fileName2.lastIndexOf('/');
				int index2 = fileName2.lastIndexOf('.');
				String path = fileName2.substring(0,index+1);
				String real_name = fileName2.substring(index+1,index2);
				File directory = new File(path);
				File[] files = directory.listFiles();
				for(int j=0; j<files.length; j++){
					String str = files[j].getName();
					if(str.contains("GLog_" + real_name)){
						file = new FileInputStream(path+str);
						fileName = path+str;
						break;
					}	
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			// 2-2 : 엑셀 파일 입력
			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(file);
			} catch (IOException e) {
				e.printStackTrace();
			}		
			
			// 2-3 : 엑셀 파일내 (첫번째) 시트 가져오기
			XSSFSheet sheet = workbook.getSheetAt(0);
					
			
			// 2-4 : 분석
			// 2-4-1 : 엑셀파일 전체/순차 탐색
			int rows = sheet.getPhysicalNumberOfRows();
			for (rowIndex=0; rowIndex<rows; rowIndex++){
				XSSFRow row = sheet.getRow(rowIndex);
				if (row != null) {
					XSSFCell cell = row.getCell(0);
			
					// 2-4-2 : 문자열 저장
					String value = "";
					
					if (cell == null) {
						continue;
					} 
					else {
						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_FORMULA:
							value = cell.getCellFormula();
							break;
						case XSSFCell.CELL_TYPE_NUMERIC:
							value = cell.getNumericCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_STRING:
							value = cell.getStringCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_BLANK:
							value = cell.getBooleanCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_ERROR:
							value = cell.getErrorCellValue() + "";
							break;
						}
					}
					
					if (value.equals("< Test Result >") || resultflag == true ) {
						if(resultflag == false)
							resultflag = true;
						else 	
							resultInfo.add(value);
						
					} else if (value.equals("< Group Info >") || groupflag == true){
						if(groupflag == false)
							groupflag = true;
						else{
							
							Vector<String> tmp = new Vector<String>();
							int i = 3;
							while(true){	// 3 ~
							
								
								XSSFCell cell3 = row.getCell(i);
								String value3 = "";
								if (cell3 == null) {
									
									break;
								} else {
							
									switch (cell3.getCellType()) {
									case XSSFCell.CELL_TYPE_FORMULA:
										value3 = cell3.getCellFormula();
										break;
									case XSSFCell.CELL_TYPE_NUMERIC:
										value3 = cell3.getNumericCellValue() + "";
										break;
									case XSSFCell.CELL_TYPE_STRING:
										value3 = cell3.getStringCellValue() + "";
										break;
									case XSSFCell.CELL_TYPE_BLANK:
										value3 = cell3.getBooleanCellValue() + "";
										break;
									case XSSFCell.CELL_TYPE_ERROR:
										value3 = cell3.getErrorCellValue() + "";
										break;
									}
									
								}
								i++;
								int j=0;
								while(true){
									if(value3.charAt(j) == '.'){
										value3 = value3.substring(0, j);
										break;
									}
									j++;
								}
								tmp.add(value3);
								
							}
							groupInfo.add(tmp);
						
						}
					} else if(value.equals("Fail Log")){
						
					}
					
				}
				else{
					resultflag=false;
					groupflag=false;
				}
			}
		}

}
