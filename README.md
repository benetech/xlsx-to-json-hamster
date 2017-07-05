# xlsx-to-json-hamster
Converts an Excel file to a JSON file

This was written to act as a Java replacement to the JavaScript library [js-xlsx](https://github.com/SheetJS/js-xlsx) 
for the [OpenDataKit 2.0]()-based [Poverty Stoplight]() project.

Basically, it uses Apache POI to read in a spreadsheet, converts it to Java Collections objects, 
and exports to JSON using the Google Gson library.
You can also just get the Java Collections objects in case you need to do your own manipulation of the objects.

The tree created is strictly a branch OR data node tree.  Strings cannot reside at the same level in the tree 
as references to branches of the tree.  When such a case is encountered, a new leaf called "default" is 
created and the data is placed in that leaf.  This is consistent with how ODK 2.0 uses js-xlsx, but 
it may not be compatible with the original js-xlsx project.

## Options

### Dots to branches 
The converter has a "dots to branches" option.  When this option is set to true, dotted column names can be used to create
deeper branches in the resulting JSON tree.  This is used by the ODK project.

Example spreadsheet header and first data row:

```
| display.text |  display.text.spanish |  display.text.french |
|   cat        |  gato                 |  chat                |
```         

Dots to branches disabled

```javascript
{ "sheet1": [
  {
    "display.text": "cat",
    "display.text.spanish": "gato",
    "display.text.french": "chat"
    }
]}
```

Dots to branches enabled
```javascript
{ "sheet1": [
  {
    "display": {
      "text": {
        "default": "cat",
        "spanish": "gato",
        "french": "chat"
      }
    }
  }
]}
```

Note the creation of the "default" leaf when dots to branches is enabled, because data cannot exist at the same level as branches in this tree implementation.

## Add row numbers

There is also an option to set row numbers.  If enabled, a `_row_num` field will be added corresponding to each row in the database, reflecting its original position in the spreadsheet.  This is used by ODK 2.0.

    

