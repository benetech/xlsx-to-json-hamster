package org.benetech.xlsxjson;

public class Xlsx2JsonConverterBuilder {
  public boolean dotsToNested = true;
  public boolean addRowNums = true;

  public Xlsx2JsonConverterBuilder() {

  }

  public Xlsx2JsonConverterBuilder dotsToNested(boolean dotsToNested) {
    this.dotsToNested = dotsToNested;
    return this;
  }

 
  public Xlsx2JsonConverterBuilder addRowNums(boolean addRowNums) {
    this.addRowNums = addRowNums;
    return this;
  }

  public Xlsx2JsonConverter build() {
    return new Xlsx2JsonConverter(dotsToNested, addRowNums);

  }

}
