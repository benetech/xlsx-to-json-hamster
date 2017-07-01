package org.benetech.xlsxjson;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DotToNestedTest extends TestCase {

  Log logger = LogFactory.getLog(DotToNestedTest.class);

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public DotToNestedTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(DotToNestedTest.class);
  }

  public void testRowConvertDotsToNestedDefaults() {
    Map<String, Object> rowMap = new HashMap<String, Object>();

    rowMap.put("a.b", "red");
    rowMap.put("a.b.c", "blue");
    rowMap.put("e.f", "green");
    rowMap.put("e", "yellow");
  


    List<String> columnNames = new ArrayList<String>();
    columnNames.add("a.b.c");
    columnNames.add("a.b");
    columnNames.add("e.f");
    columnNames.add("e");


    ConverterBuilder builder = new ConverterBuilder();
    Converter converter = builder.build();

    Map<String, Object> newRowMap = converter.convertDotsToNested(columnNames, rowMap);
    logger.info(newRowMap);

  }
  

  public void testRowConvertDotsToNested() {
    Map<String, Object> rowMap = new HashMap<String, Object>();

    rowMap.put("a.b.c", "red");
    rowMap.put("a.b.d", "blue");
    rowMap.put("e.f", "green");
    rowMap.put("e.g", "yellow");
    rowMap.put("j.k.l.m.n", "orange");
    rowMap.put("j.k.l.m.o", "violet");
    rowMap.put("j.k.l.m.p", "indigo");
    rowMap.put("q", "black");
    rowMap.put("r", "white");


    List<String> columnNames = new ArrayList<String>();
    columnNames.add("a.b.c");
    columnNames.add("a.b.d");
    columnNames.add("e.f");
    columnNames.add("e.g");
    columnNames.add("j.k.l.m.n");
    columnNames.add("j.k.l.m.o");
    columnNames.add("j.k.l.m.p");
    columnNames.add("q");
    columnNames.add("r");

    ConverterBuilder builder = new ConverterBuilder();
    Converter converter = builder.build();

    Map<String, Object> newRowMap = converter.convertDotsToNested(columnNames, rowMap);
    logger.info(newRowMap);
    // {a={b={c=red, d=blue}}, q=black, r=white, e={f=green, g=yellow}, j={k={l={m={p=indigo,
    // n=orange, o=violet}}}}}
    Map<String, Object> aNode = (Map<String, Object>) newRowMap.get("a");
    Map<String, Object> bNode = (Map<String, Object>) aNode.get("b");
    String cNode = (String) bNode.get("c");
    String dNode = (String) bNode.get("d");
    String qNode = (String) newRowMap.get("q");
    Map<String, Object> eNode = (Map<String, Object>) newRowMap.get("e");
    String gNode = (String) eNode.get("g");
    Map<String, Object> jNode = (Map<String, Object>) newRowMap.get("j");
    Map<String, Object> kNode = (Map<String, Object>) jNode.get("k");
    Map<String, Object> lNode = (Map<String, Object>) kNode.get("l");
    Map<String, Object> mNode = (Map<String, Object>) lNode.get("m");
    String pNode = (String) mNode.get("p");


    assertThat(cNode, is("red"));
    assertThat(gNode, is("yellow"));
    assertThat(qNode, is("black"));
    assertThat(pNode, is("indigo"));
  }
}
