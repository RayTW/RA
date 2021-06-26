package ra.util;

import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import ra.db.RecordCursor;
import ra.util.parser.VisitClassStrategy;

/**
 * Common tool class.
 *
 * @author Ray Li
 */
public class Utility {
  private static Utility instance = new Utility();
  private String space = "  ";

  private Utility() {}

  public static Utility get() {
    return instance;
  }

  /**
   * Return details of exception.
   *
   * @param exception exception
   * @return stack trace
   */
  public String getThrowableDetail(Throwable exception) {
    StringBuilder errStr = new StringBuilder(exception + System.lineSeparator());

    for (StackTraceElement s : exception.getStackTrace()) {
      errStr.append(s.toString());
      errStr.append(System.lineSeparator());
    }
    return errStr.toString();
  }

  /**
   * Converts exception stack trace to string.
   *
   * @param exception exception
   * @return stack trace
   */
  public String toExceptionStackTrace(Throwable exception) {
    StringWriter writer = new StringWriter();
    exception.printStackTrace(new PrintWriter(writer));

    return writer.toString();
  }

  /**
   * Standardized floating point output.
   *
   * @param points floating point
   * @param value value
   * @return Returns value of already formatted.
   */
  public double sprintf(int points, double value) {
    return Arith.round(value, points);
  }

  /**
   * Numbers are converted from scientific symbols back to numbers.
   *
   * @param value source value
   * @return Returns value of already formatted.
   */
  public String sformat(double value) {
    DecimalFormat f = new DecimalFormat("########################.##");
    return f.format(value);
  }

  /**
   * Replace specific member of the object.
   *
   * @param obj source object
   * @param name member name
   * @param member object member
   */
  public void replaceMember(Object obj, String name, Object member) {
    replaceObjectMember(obj.getClass(), obj, name, member);
  }

  /**
   * Replace specific member of the object.
   *
   * @param clazz class of object
   * @param obj source object
   * @param name member name
   * @param member object member
   */
  private void replaceObjectMember(Class<?> clazz, Object obj, String name, Object member) {
    if (clazz == Object.class) {
      return;
    }

    for (Field field : clazz.getDeclaredFields()) {
      field.setAccessible(true);
      if (field.getName().equals(name)) {
        try {
          field.set(obj, member);
        } catch (Exception e) {
          e.printStackTrace();
        }
        return;
      }
    }

    replaceObjectMember(clazz.getSuperclass(), obj, name, member);
  }

  /**
   * List object all member and value.
   *
   * @param object target object
   */
  public void showAll(Object object) {
    for (Field field : object.getClass().getDeclaredFields()) {
      field.setAccessible(true);

      try {
        System.out.println(field.getName() + " = " + field.get(object));
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * List object that setter, getter method and value.
   *
   * <pre>
   * <code>
   * code:
   * Utility.get().showSetterGetter(com.chungyo.external.kind24.WagersExtend.class)
   *
   * output:
   * public String getWagersID(){return mWagersID;}
   * public void setWagersID(String wagersid){mWagersID=wagersid;}
   * public long getRoundSerial(){return mRoundSerial;}
   * public void setRoundSerial(long roundserial){mRoundSerial=roundserial;}
   * public String getRoundDate(){return mRoundDate;}
   * public void setRoundDate(String rounddate){mRoundDate=rounddate;}
   * public String getContent(){return mContent;}
   * public void setContent(String content){mContent=content;}
   * public int getSerialType(){return mSerialType;}
   * public void setSerialType(int serialtype){mSerialType=serialtype;}
   *
   * </code>
   * </pre>
   *
   * @param <T> class type
   * @param clazz target class
   */
  public <T> void showSetterGetter(Class<T> clazz) {
    String get = "public %s get%s(){return %s;}";
    String set = "public void set%s(%s %s){%s=%s;}";
    String type = null;
    String srcName = null;
    String name = null;
    String paramName = null;
    Class<?> fieldType = null;
    String fieldTypeString = null;
    String genericType = null;

    for (Field field : clazz.getDeclaredFields()) {
      field.setAccessible(true);
      srcName = field.getName();

      if (srcName.indexOf("$") != -1) {
        continue;
      }

      srcName = "this." + srcName;
      name = Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
      paramName = field.getName();
      fieldType = field.getType();
      fieldTypeString = fieldType.toString();
      genericType = field.getGenericType().getTypeName();

      if (fieldType.isArray()) {
        long count = fieldTypeString.chars().filter(ch -> ch == '[').count();
        char primitive = fieldTypeString.charAt(fieldTypeString.length() - 1);
        StringBuilder arrayType = new StringBuilder();

        for (int i = 0; i < count; i++) {
          arrayType.append("[]");
        }

        switch (primitive) {
          case 'I':
            type = "int" + arrayType.toString();
            break;
          case 'J':
            type = "long" + arrayType.toString();
            break;
          case 'B':
            type = "byte" + arrayType.toString();
            break;
          case 'S':
            type = "short" + arrayType.toString();
            break;
          case 'D':
            type = "double" + arrayType.toString();
            break;
          case 'F':
            type = "float" + arrayType.toString();
            break;
          default:
            String[] ary = fieldTypeString.split("[.;]");
            type = ary[ary.length - 1] + arrayType.toString();
        }
      } else if (genericType.contains("<")) { // 處理泛型interface，例如: List<String>
        type = genericType;
      } else {
        String[] typeName = fieldTypeString.split("[.]", -1);
        type = typeName[typeName.length - 1];
      }

      System.out.println(String.format(get, type, name, srcName));
      System.out.println(String.format(set, name, type, paramName, srcName, paramName));
    }
  }

  /**
   * Visit member of class.
   *
   * @param clazz target
   * @param strategy strategy
   * @return Returns true will visit parent class.
   */
  public boolean recursiveClassFields(Class<?> clazz, VisitClassStrategy strategy) {
    Field[] fields = clazz.getDeclaredFields();
    Field field = null;

    try {
      for (int i = 0; i < fields.length; i++) {
        if (fields[i].isSynthetic()) {
          continue;
        }
        field = fields[i];
        field.setAccessible(true);
        strategy.shouldVisitField(field);
      }
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return false;
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      return false;
    }

    if (strategy.shouldSkipClass(clazz.getSuperclass())) {
      return true;
    }
    return recursiveClassFields(clazz.getSuperclass(), strategy);
  }

  public String readFile(String filePath) {
    return readFile(filePath, "UTF-8");
  }

  /**
   * Read file.
   *
   * @param filePath file path
   * @param charset Format of the file ​content.
   * @return Returns file content.
   */
  public String readFile(String filePath, String charset) {
    try {
      return new String(Files.readAllBytes(Paths.get(filePath)), charset);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Read file and convert to {@link JSONObject}.
   *
   * @param filePath file path
   * @return JSON
   */
  public JSONObject readFileToJsonObject(String filePath) {
    return new JSONObject(readFile(filePath));
  }

  /**
   * Read file and convert to {@link JSONArray}.
   *
   * @param filePath file path
   * @return JSON
   */
  public JSONArray readFileToJsonArray(String filePath) {
    return new JSONArray(readFile(filePath));
  }

  /**
   * A simple implementation to pretty-print JSON file.
   *
   * @param json JSONObject物件
   * @return Returns JSON of pretty.
   */
  public String prettyPrintJson(JSONObject json) {
    return prettyPrintJson(json.toString());
  }

  /**
   * A simple implementation to pretty-print JSON file.
   *
   * @param jsonarray JSONArray
   * @return Returns JSON of pretty.
   */
  public String prettyPrintJson(JSONArray jsonarray) {
    return prettyPrintJson(jsonarray.toString());
  }

  /**
   * A simple implementation to pretty-print JSON file.
   *
   * @param unformattedJsonString JSON of not yet pretty.
   * @return Returns JSON of pretty.
   */
  public String prettyPrintJson(String unformattedJsonString) {
    return prettyPrintJson(unformattedJsonString, space);
  }

  /**
   * A simple implementation to pretty-print JSON file.
   *
   * @param unformattedJsonString JSON of not yet pretty.
   * @param space JSON blank space
   * @return Returns JSON of pretty.
   */
  public String prettyPrintJson(String unformattedJsonString, String space) {
    StringBuilder prettyJsonBuilder = new StringBuilder();
    int indentLevel = 0;
    boolean inQuote = false;

    for (char charFromUnformattedJson : unformattedJsonString.toCharArray()) {
      switch (charFromUnformattedJson) {
        case '"':
          // switch the quoting status
          inQuote = !inQuote;
          prettyJsonBuilder.append(charFromUnformattedJson);
          break;
        case ' ':
          // For space: ignore the space if it is not being quoted.
          if (inQuote) {
            prettyJsonBuilder.append(charFromUnformattedJson);
          }
          break;
        case '{':
        case '[':
          // Starting a new block: increase the indent level
          prettyJsonBuilder.append(charFromUnformattedJson);
          indentLevel++;
          appendIndentedNewLine(indentLevel, prettyJsonBuilder, space);
          break;
        case '}':
        case ']':
          // Ending a new block; decrese the indent level
          indentLevel--;
          appendIndentedNewLine(indentLevel, prettyJsonBuilder, space);
          prettyJsonBuilder.append(charFromUnformattedJson);
          break;
        case ',':
          // Ending a json item; create a new line after
          prettyJsonBuilder.append(charFromUnformattedJson);
          if (!inQuote) {
            appendIndentedNewLine(indentLevel, prettyJsonBuilder, space);
          }
          break;
        default:
          prettyJsonBuilder.append(charFromUnformattedJson);
      }
    }
    return prettyJsonBuilder.toString();
  }

  /**
   * Print a new line with indention at the beginning of the new line.
   *
   * @param indentLevel indentLevel.
   * @param stringBuilder stringBuilder.
   * @param space space.
   */
  private void appendIndentedNewLine(int indentLevel, StringBuilder stringBuilder, String space) {
    stringBuilder.append(System.lineSeparator());
    for (int i = 0; i < indentLevel; i++) {
      stringBuilder.append(space);
    }
  }

  /**
   * File uncompression uses ZIP.<br>
   *
   * <pre>
   * example: {
   *   String zipPath = "C:/Users/ray_lee/Desktop/toJavaModel.zip";
   *   String filePath = "C:/Users/ray_lee/Desktop/";
   *
   *   try {
   *     unzip(zipPath, filePath);
   *   } catch (Exception e) {
   *     e.printStackTrace();
   *   }
   * </pre>
   *
   * @param zipFilePath  zip source file path
   * @param unzipFolderPath unzip target path
   * @throws IOException when read file failure
   * @throws ZipException when unzipping file failure
   */
  public void unzip(String zipFilePath, String unzipFolderPath) throws ZipException, IOException {
    File zipFile = new File(zipFilePath);
    // create output store path
    File unzipFileDir = new File(unzipFolderPath);

    if (!unzipFileDir.exists() || !unzipFileDir.isDirectory()) {
      unzipFileDir.mkdirs();
    }

    // star unzip
    ZipEntry entry = null;
    String entryFilePath = null;
    String entryDirPath = null;
    File entryFile = null;
    File entryDir = null;
    int index = 0;

    try (ZipFile zip = new ZipFile(zipFile)) {
      Enumeration<? extends ZipEntry> entries = zip.entries();

      // Unzip each file.
      while (entries.hasMoreElements()) {
        entry = entries.nextElement();
        // Unzip to target file path.
        entryFilePath = unzipFolderPath + File.separator + entry.getName();
        // Process unzip file name.
        index = entryFilePath.lastIndexOf(File.separator);
        if (index != -1) {
          entryDirPath = entryFilePath.substring(0, index);
        } else {
          entryDirPath = "";
        }
        entryDir = new File(entryDirPath);
        if (!entryDir.exists() || !entryDir.isDirectory()) {
          entryDir.mkdirs();
        }

        entryFile = new File(entryFilePath);

        if (entry.isDirectory()) {
          entryFile.mkdir();
          continue;
        }

        // Output to target file.
        try (InputStream is = zip.getInputStream(entry)) {
          Files.copy(is, entryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
      }
    }
  }

  /**
   * File compression uses ZIP.<br>
   * example:
   *
   * <pre>
   * // Compression file:
   * Utility.get().zip("D:/myFiles/file.log", "D:/test/file.zip");
   * </pre>
   *
   * <pre>
   * // Compression directory
   * Utility.get().zip("D:/myFolder", "D:/folder.zip");
   * </pre>
   *
   * @param sourcePath 原始檔案(路徑可為檔案或資料夾)
   * @param targetZipPath 要壓縮的檔案及路徑
   * @throws IOException 當開啟檔案進行壓緒時遭遇非預期失敗時將會拋出
   */
  public void zip(String sourcePath, String targetZipPath) throws IOException {
    Path targetPath = Paths.get(targetZipPath);

    if (targetPath.getParent() != null) {
      Files.createDirectories(targetPath.getParent());
    }

    Path target = Files.createFile(targetPath);

    try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(target))) {
      Path source = Paths.get(sourcePath);

      BiConsumer<Path, String> zipFileHandle =
          (srcPath, srcString) -> {
            ZipEntry zipEntry = new ZipEntry(srcString);

            try {
              zipOut.putNextEntry(zipEntry);
              Files.copy(srcPath, zipOut);
              zipOut.closeEntry();
            } catch (IOException e) {
              e.printStackTrace();
            }
          };

      if (Files.isDirectory(source)) {
        try (Stream<Path> walkStream = Files.walk(source)) {
          walkStream
              .filter(path -> !Files.isDirectory(path))
              .forEach(
                  path -> {
                    zipFileHandle.accept(path, source.relativize(path).toString());
                  });
        }
      } else {
        zipFileHandle.accept(source, source.toFile().getName());
      }
    }
  }

  /**
   * Delete files.
   *
   * @param path path
   */
  public void deleteFiles(String path) {
    deleteFiles(new File(path));
  }

  /**
   * Delete deep files.
   *
   * @param file file
   */
  public void deleteFiles(File file) {
    Consumer<File> deleteFile =
        (f) -> {
          file.delete();
        };

    if (!file.isDirectory()) {
      deleteFile.accept(file);
      return;
    }

    for (File f : file.listFiles()) {
      deleteFiles(f);
    }

    deleteFile.accept(file);
  }

  /**
   * Assign an object member value.
   *
   * @param object object.
   * @param record record.
   */
  public void setFieldsUsingSerializedName(Object object, RecordCursor record) {
    setAllFieldsValue(object, record, o -> o.value());
  }

  /**
   * Assign an object member value.
   *
   * @param object object
   * @param record record
   * @param alternateIndex alternate index
   */
  public void setFieldsUsingAlternate(Object object, RecordCursor record, int alternateIndex) {
    setAllFieldsValue(
        object, record, o -> o.alternate().length > 0 ? o.alternate()[alternateIndex] : o.value());
  }

  /**
   * Assign value to object member from {@link RecordCursor}.
   *
   * @param object source object
   * @param record The record of query from database.
   * @param listener value
   */
  public void setAllFieldsValue(
      Object object, RecordCursor record, Function<SerializedName, String> listener) {
    recursiveClassFields(
        object.getClass(),
        (field) -> {
          SerializedName serializedName = field.getAnnotation(SerializedName.class);
          String value = record.field(listener.apply(serializedName));

          setValue(object, field, value);
        });
  }

  /**
   * Assign value to object member.
   *
   * @param obj source object
   * @param field member field
   * @param value field value
   * @throws NumberFormatException NumberFormatException
   * @throws IllegalArgumentException IllegalArgumentException
   * @throws IllegalAccessException IllegalAccessException
   */
  public void setValue(Object obj, Field field, String value)
      throws NumberFormatException, IllegalArgumentException, IllegalAccessException {
    Class<?> fieldType = field.getType();

    if (fieldType.isPrimitive()) {
      if (int.class == fieldType) {
        field.setInt(obj, Integer.parseInt(value));
      } else if (long.class == fieldType) {
        field.setLong(obj, Long.parseLong(value));
      } else if (double.class == fieldType) {
        field.setDouble(obj, Double.parseDouble(value));
      } else if (short.class == fieldType) {
        field.setShort(obj, Short.parseShort(value));
      } else if (float.class == fieldType) {
        field.setFloat(obj, Float.parseFloat(value));
      } else if (boolean.class == fieldType) {
        field.setBoolean(obj, Boolean.parseBoolean(value));
      } else if (byte.class == fieldType) {
        field.setByte(obj, Byte.parseByte(value));
      } else if (char.class == fieldType) {
        field.setChar(obj, value.toCharArray()[0]);
      }
    } else { // wrapper class
      if (fieldType == Integer.class) {
        field.set(obj, Integer.valueOf(value));
      } else if (fieldType == Long.class) {
        field.set(obj, Long.parseLong(value));
      } else if (fieldType == Double.class) {
        field.set(obj, Double.parseDouble(value));
      } else if (fieldType == Short.class) {
        field.set(obj, Short.parseShort(value));
      } else if (fieldType == Float.class) {
        field.set(obj, Float.parseFloat(value));
      } else if (fieldType == Boolean.class) {
        field.set(obj, Boolean.parseBoolean(value));
      } else if (fieldType == Byte.class) {
        field.set(obj, Byte.parseByte(value));
      } else if (fieldType == Character.class) {
        field.set(obj, value.toCharArray()[0]);
      } else {
        field.set(obj, value);
      }
    }
  }
}
