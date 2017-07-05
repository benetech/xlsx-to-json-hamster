package org.benetech.xlsxjson;

public class ConverterBuilder {
  public boolean dotsToNested = true;
  public boolean addRowNums = true;

  public ConverterBuilder() {

  }

  public ConverterBuilder dotsToNested(boolean dotsToNested) {
    this.dotsToNested = dotsToNested;
    return this;
  }

 
  public ConverterBuilder addRowNums(boolean addRowNums) {
    this.addRowNums = addRowNums;
    return this;
  }

  public Converter build() {
    return new Converter(dotsToNested, addRowNums);

  }

}
