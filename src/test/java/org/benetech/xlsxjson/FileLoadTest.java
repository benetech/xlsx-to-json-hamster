package org.benetech.xlsxjson;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileLoadTest {

  Log logger = LogFactory.getLog(FileLoadTest.class);
  
  @Test
  public void testLoadFile() throws Exception {
    URL url = this.getClass().getResource("/poverty_stoplight_madison.xlsx");
    File xlsxFile = new File(url.getFile());
    assertTrue(xlsxFile.exists());
  }

  @Test
  public void testReadSheet() throws Exception {
    URL url = this.getClass().getResource("/poverty_stoplight_madison.xlsx");
    File xlsxFile = new File(url.getFile());
    Workbook wb = new XSSFWorkbook(new FileInputStream(xlsxFile));

    for (int i = 0; i < wb.getNumberOfSheets(); i++) {
      Sheet sheet = wb.getSheetAt(i);
      logger.info(i + ": " + wb.getSheetName(i));

      Xlsx2JsonConverterBuilder builder = new Xlsx2JsonConverterBuilder();
      Xlsx2JsonConverter converter = builder.build();

      List<Map<String, Object>> result = converter.convertSheetBody(sheet);

      Gson gson = new GsonBuilder().disableHtmlEscaping().create();
      String json = gson.toJson(result);
      
      logger.info(i + ": " + wb.getSheetName(i));

      logger.info(json);

      if (wb.getSheetName(i).equals("survey")) {
        assertThat(result.size(), is(29));
        assertThat((String) result.get(0).get("comments"),
            is("This is the main survey worksheet that is core of the form."));
        assertThat((String) result.get(28).get("comments"),
            is("Exit section is used to go out of section"));
      }

    }

    wb.close();
  }
  
  @Test
  public void testReadWorkbook() throws Exception {
    URL url = this.getClass().getResource("/poverty_stoplight_madison.xlsx");
    File xlsxFile = new File(url.getFile());
    Xlsx2JsonConverterBuilder builder = new Xlsx2JsonConverterBuilder();
    builder.dotsToNested(false);
    Xlsx2JsonConverter converter = builder.build();
    
    String json = converter.convertToJson(xlsxFile);
    logger.info(json);
  }

}
