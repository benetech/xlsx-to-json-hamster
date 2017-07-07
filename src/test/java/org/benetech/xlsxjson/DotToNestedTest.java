package org.benetech.xlsxjson;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class DotToNestedTest {

  Log logger = LogFactory.getLog(DotToNestedTest.class);

  @Test
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


    Xlsx2JsonConverterBuilder builder = new Xlsx2JsonConverterBuilder();
    Xlsx2JsonConverter converter = builder.build();

    Map<String, Object> newRowMap = converter.convertDotsToNested(columnNames, rowMap);
    logger.info(newRowMap);
    
    Map<String, Object> aNode = (Map<String, Object>) newRowMap.get("a");
    Map<String, Object> bNode = (Map<String, Object>) aNode.get("b");
    String cNode = (String) bNode.get("c");
    String dNode = (String) bNode.get("default");
    Map<String, Object> eNode = (Map<String, Object>) newRowMap.get("e");
    String fNode = (String) eNode.get("f");
    String gNode = (String) eNode.get("default");
    assertThat(cNode, is("blue"));
    assertThat(dNode, is("red"));
    assertThat(fNode, is("green"));
    assertThat(gNode, is("yellow"));
  }

  @Test
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

    Xlsx2JsonConverterBuilder builder = new Xlsx2JsonConverterBuilder();
    Xlsx2JsonConverter converter = builder.build();

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
