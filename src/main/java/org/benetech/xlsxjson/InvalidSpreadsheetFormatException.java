package org.benetech.xlsxjson;

public class InvalidSpreadsheetFormatException extends RuntimeException {
  private static final long serialVersionUID = -7842523517977105408L;

  public InvalidSpreadsheetFormatException(String message) {
    super(message);
  }
}
