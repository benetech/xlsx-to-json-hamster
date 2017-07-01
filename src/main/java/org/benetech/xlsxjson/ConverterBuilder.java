package org.benetech.xlsxjson;

import java.util.Set;

public class ConverterBuilder {
  public boolean dotsToNested = true;
  public boolean addRowNums = true;

  public Set<String> reservedWords;

  public ConverterBuilder() {

  }

  public ConverterBuilder dotsToNested(boolean dotsToNested) {
    this.dotsToNested = dotsToNested;
    return this;
  }

  public ConverterBuilder reservedWords(Set<String> reservedWords) {
    this.reservedWords = reservedWords;
    return this;
  }

  public ConverterBuilder addRowNums(boolean addRowNums) {
    this.addRowNums = addRowNums;
    return this;
  }

  public Converter build() {
    return new Converter(dotsToNested, addRowNums, reservedWords);

  }

}
