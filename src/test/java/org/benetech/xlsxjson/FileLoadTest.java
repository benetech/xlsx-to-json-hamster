package org.benetech.xlsxjson;

import java.io.File;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FileLoadTest extends TestCase {
  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public FileLoadTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(FileLoadTest.class);
  }

  public void testLoadFile() {
    URL url = this.getClass().getResource("/poverty_stoplight_madison.xlsx");
    File xlsxFile = new File(url.getFile());

    assertTrue(xlsxFile.exists());
  }
}
