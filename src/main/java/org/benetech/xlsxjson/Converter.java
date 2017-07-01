package org.benetech.xlsxjson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;

public class Converter {

  private static Log logger = LogFactory.getLog(Converter.class);

  public static final String ROW_NUM_KEY = "_row_num";

  public boolean dotsToNested = true;

  public boolean addRowNums = true;

  public Set<String> reservedWords;

  public Converter(boolean dotsToNested, boolean addRowNums, Set<String> reservedWords) {
    this.dotsToNested = dotsToNested;
    this.addRowNums = addRowNums;
    this.reservedWords = reservedWords;
  }

  public String convert(File file) throws FileNotFoundException, IOException {

    String result = "";

    Map<String, List<Map<String, Object>>> workbookMap = convertWorkbook(file);
    Gson gson = new Gson();

    return gson.toJson(workbookMap);

  }

  Map<String, List<Map<String, Object>>> convertWorkbook(File file)
      throws FileNotFoundException, IOException {
    Map<String, List<Map<String, Object>>> workbookMap =
        new LinkedHashMap<String, List<Map<String, Object>>>();
    Workbook wb = new XSSFWorkbook(new FileInputStream(file));
    for (int i = 0; i < wb.getNumberOfSheets(); i++) {
      Sheet sheet = wb.getSheetAt(i);
      logger.info("Converting " + wb.getSheetName(i));
      List<Map<String, Object>> sheetList = convertSheetBody(sheet);
      workbookMap.put(wb.getSheetName(i), sheetList);

    }
    wb.close();
    return workbookMap;
  }

  List<Map<String, Object>> convertSheetBody(Sheet sheet) {

    Map<Integer, String> columnNames = new LinkedHashMap<Integer, String>();
    List<Map<String, Object>> sheetList = new ArrayList<Map<String, Object>>();

    if (sheet == null) {
      throw new InvalidSpreadsheetFormatException("Worksheet is null.");
    }
    if (sheet.getRow(sheet.getFirstRowNum()) == null) {
      throw new InvalidSpreadsheetFormatException("Worksheet does not have first row.");
    }
    Row firstRow = sheet.getRow(sheet.getFirstRowNum());
    for (int j = firstRow.getFirstCellNum(); j < firstRow.getLastCellNum(); j++) {
      Cell columnName = firstRow.getCell(j);
      if (columnName != null && !columnName.getStringCellValue().isEmpty()) {
        columnNames.put(j, columnName.getStringCellValue());
      }
    }

    for (int i = sheet.getFirstRowNum() + 1; i < sheet.getLastRowNum(); i++) {
      Row row = sheet.getRow(i);
      Map<String, Object> rowMap = new LinkedHashMap<String, Object>();
      // We only care about named columns.
      for (Integer columnIndex : columnNames.keySet()) {
        Object cellString = cellAsJsonConvertibleType(row.getCell(columnIndex));
        if (cellString != null) {
          rowMap.put(columnNames.get(columnIndex), cellString);
        }
      }
      if (!rowMap.isEmpty()) {
        if (dotsToNested) {
          rowMap = convertDotsToNested(columnNames.values(), rowMap);
        }
        if (addRowNums) {
          rowMap.put(ROW_NUM_KEY, new Integer(i + 1));
        }
        sheetList.add(rowMap);
      }
    }
    return sheetList;
  }

  Map<String, Object> convertDotsToNested(Collection<String> columnNames,
      Map<String, Object> data) {
    Map<String, Object> newMap = new LinkedHashMap<String, Object>();
    for (String columnName : columnNames) {
      if (data.get(columnName) == null) {
        // Do nothing
      } else if (columnName.contains(".")) {
        String[] columnKeys = StringUtils.split(columnName, ".");
        Map<String, Object> parentMap;
        if (newMap.get(columnKeys[0]) == null) {
          // Create root of tree
          parentMap = new LinkedHashMap<String, Object>();
          newMap.put(columnKeys[0], parentMap);
        } else {
          // Get root tree, already exists
          parentMap = (Map<String, Object>) newMap.get(columnKeys[0]);
        }

        for (int i = 1; i < columnKeys.length - 1; i++) {
          if (parentMap.get(columnKeys[i]) == null) {
            // Create new branch for non-terminating column segment (e.g. text in
            // display.text.spanish)
            parentMap.put(columnKeys[i], new LinkedHashMap<String, Object>());
          }
          else if (!(parentMap.get(columnKeys[i]) instanceof Map<?, ?>)) {
            // Move value sitting at root to "default" key. No data in the nodes of this tree!
            String defaultValue = (String) parentMap.get(columnKeys[i]);
            Map<String, Object> newChildMap = new HashMap<String, Object>();
            newChildMap.put("default", defaultValue);
            parentMap.put(columnKeys[i], newChildMap);
          }
          // Move the pointer to the parent map down the branch one segment
          parentMap = (Map<String, Object>) parentMap.get(columnKeys[i]);

        }

        // Terminating segment (spanish in display.text.spanish or text in display.text)
        if (parentMap.get(columnKeys[columnKeys.length - 1]) == null) {
          parentMap.put(columnKeys[columnKeys.length - 1], data.get(columnName));
        } else if (parentMap.get(columnKeys[columnKeys.length - 1]) instanceof Map<?, ?>) {
          // Adding a default value to an existing map
          Map<String, Object> childMap =
              (Map<String, Object>) parentMap.get(columnKeys[columnKeys.length - 1]);
          childMap.put("default", data.get(columnName));
        } else {
          logger.info("Should not get here.");
        }
      } else {
        if (newMap.get(columnName) == null) {
          newMap.put(columnName, data.get(columnName));
        } else {
          // Subtree exists, adding default
          String defaultValue = (String) data.get(columnName);
          Map<String, Object> childMap = (Map<String, Object>) newMap.get(columnName);
          childMap.put("default", defaultValue);
        }
      }
    }
    return newMap;
  }


  Object cellAsJsonConvertibleType(Cell cell) {
    Object result = null;
    
    if (cell != null) {
      switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
          result = cell.getRichStringCellValue().getString();
          break;
        case Cell.CELL_TYPE_NUMERIC:
          if (DateUtil.isCellDateFormatted(cell)) {
            result = cell.getDateCellValue().toString();
          } else if (isDoubleInt(cell.getNumericCellValue())){
            result = new Integer((int) cell.getNumericCellValue());
          } else {
            result = cell.getNumericCellValue();
          }
          break;
        case Cell.CELL_TYPE_BOOLEAN:
          result = cell.getBooleanCellValue();
          break;
        case Cell.CELL_TYPE_FORMULA:
          result = cell.getCellFormula().toString(); 
      }

    }
    return result;
  }

  /**
   * Determine if a double can be represented as an int without loss of precise data.
   * @param d
   * @return
   * @author Ryan Amos
   * @see https://stackoverflow.com/a/12558622
   */
  public boolean isDoubleInt(double d)
  {
      //select a "tolerance range" for being an integer
      double TOLERANCE = 1E-5;
      //do not use (int)d, due to weird floating point conversions!
      return Math.abs(Math.floor(d) - d) < TOLERANCE;
  }
  
  public boolean isDotsToNested() {
    return dotsToNested;
  }

  public void setDotsToNested(boolean dotsToNested) {
    this.dotsToNested = dotsToNested;
  }

  public Set<String> getReservedWords() {
    return reservedWords;
  }

  public void setReservedWords(Set<String> reservedWords) {
    this.reservedWords = reservedWords;
  }

}
