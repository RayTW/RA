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
   * 取得錯誤訊息的詳細資料.
   *
   * @param e 要解析的Throwable
   */
  public String getThrowableDetail(Throwable e) {
    StringBuilder errStr = new StringBuilder(e + System.lineSeparator());

    for (StackTraceElement s : e.getStackTrace()) {
      errStr.append(s.toString());
      errStr.append(System.lineSeparator());
    }
    return errStr.toString();
  }

  /**
   * 將例外訊息堆疊轉為字串.
   *
   * @param e 要解析的Throwable
   */
  public String toExceptionStackTrace(Throwable e) {
    StringWriter errors = new StringWriter();
    e.printStackTrace(new PrintWriter(errors));
    return errors.toString();
  }

  /**
   * 標準化浮點數輸出.
   *
   * @param points 小數點第幾位
   * @param vals 要標準化的值
   */
  public double sprintf(int points, double vals) {
    return Arith.round(vals, points);
  }

  /**
   * 讓 double 數字從科學符號轉回數字.
   *
   * @param a 要標準化的值
   */
  public String sformat(double a) {
    DecimalFormat f = new DecimalFormat("########################.##");
    return f.format(a);
  }

  /**
   * 置換指定object的member成員.
   *
   * @param obj 主物件
   * @param name 位於主物件要被置換的參數名
   * @param member 用來替換obj內與name符合的參數
   */
  public void replaceMember(Object obj, String name, Object member) {
    replaceObjectMember(obj.getClass(), obj, name, member);
  }

  /**
   * 置換指定object的member成員.
   *
   * @param clazz obj的類別
   * @param obj 主物件
   * @param name 位於主物件要被置換的參數名
   * @param member 用來替換obj內與name符合的參數
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
   * 列出指定物件全部的member與其member values.
   *
   * @param obj 要檢查的物件
   */
  public void showAll(Object obj) {
    for (Field field : obj.getClass().getDeclaredFields()) {
      field.setAccessible(true);

      try {
        System.out.println(field.getName() + " = " + field.get(obj));
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 幫指定class 輸出get、set的method，下列範例.
   *
   * <pre>
   * <code>
   * code:
   * Utility.get().showSetterGetter(com.chungyo.external.kind24.WagersExtend.class)
   *
   * 輸出:
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
   * @param clazz 要輸出的類別
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
   * 用遞迴往super class取得class fields.
   *
   * @param clazz 要遍歷的類別
   * @param strategy .
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
   * 讀檔.
   *
   * @param filePath 檔案路徑
   * @param charsetName 檔案的編碼格式
   */
  public String readFile(String filePath, String charsetName) {
    try {
      return new String(Files.readAllBytes(Paths.get(filePath)), charsetName);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * .
   *
   * @param filePath 檔案路徑
   * @param listener 接收讀取到的字串用
   * @throws IOException 讀取失敗
   */
  public void readFile(String filePath, java.util.function.Consumer<String> listener)
      throws IOException {
    try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
      lines.forEach(listener);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public JSONObject readFileToJsonObject(String filePath) {
    return new JSONObject(readFile(filePath));
  }

  public JSONArray readFileToJsonArray(String filePath) {
    return new JSONArray(readFile(filePath));
  }

  /**
   * A simple implementation to pretty-print JSON file.
   *
   * @param json JSONObject物件
   * @return 排版後JSON字串
   */
  public String prettyPrintJson(JSONObject json) {
    return prettyPrintJson(json.toString());
  }

  /**
   * A simple implementation to pretty-print JSON file.
   *
   * @param jsonarray JSONArray物件
   * @return 排版後JSON字串
   */
  public String prettyPrintJson(JSONArray jsonarray) {
    return prettyPrintJson(jsonarray.toString());
  }

  /**
   * A simple implementation to pretty-print JSON file.
   *
   * @param unformattedJsonString 未排版的JSON字串
   * @return 排版後JSON字串
   */
  public String prettyPrintJson(String unformattedJsonString) {
    return prettyPrintJson(unformattedJsonString, space);
  }

  /**
   * A simple implementation to pretty-print JSON file.
   *
   * @param unformattedJsonString 未排版的JSON字串
   * @param space JSON間距隔的空白字元
   * @return 排版後JSON字串
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
   * @param indentLevel .
   * @param stringBuilder .
   * @param space .
   */
  private void appendIndentedNewLine(int indentLevel, StringBuilder stringBuilder, String space) {
    stringBuilder.append(System.lineSeparator());
    for (int i = 0; i < indentLevel; i++) {
      stringBuilder.append(space);
    }
  }

  /**
   * 解壓縮zip包.<br>
   *
   * <pre>
   * 範例: {
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
   * @param zipFilePath  zip文件的全路徑
   * @param unzipFolderPath 解壓後的文件保存的路徑
   * @throws IOException io error
   * @throws ZipException zip error
   */
  public void unzip(String zipFilePath, String unzipFolderPath) throws ZipException, IOException {
    File zipFile = new File(zipFilePath);
    // 創建解壓縮文件保存的路徑
    File unzipFileDir = new File(unzipFolderPath);

    if (!unzipFileDir.exists() || !unzipFileDir.isDirectory()) {
      unzipFileDir.mkdirs();
    }

    // 開始解壓
    ZipEntry entry = null;
    String entryFilePath = null;
    String entryDirPath = null;
    File entryFile = null;
    File entryDir = null;
    int index = 0;

    try (ZipFile zip = new ZipFile(zipFile)) {
      Enumeration<? extends ZipEntry> entries = zip.entries();

      // 循環對壓縮包裡的每一個文件進行解壓
      while (entries.hasMoreElements()) {
        entry = entries.nextElement();
        // 構建壓縮包中一個文件解壓後保存的文件全路徑
        entryFilePath = unzipFolderPath + File.separator + entry.getName();
        // 構建解壓後保存的文件夾路徑
        index = entryFilePath.lastIndexOf(File.separator);
        if (index != -1) {
          entryDirPath = entryFilePath.substring(0, index);
        } else {
          entryDirPath = "";
        }
        entryDir = new File(entryDirPath);
        // 如果文件夾路徑不存在，則創建文件夾
        if (!entryDir.exists() || !entryDir.isDirectory()) {
          entryDir.mkdirs();
        }

        // 創建解壓文件
        entryFile = new File(entryFilePath);

        // 若是資料夾就建立
        if (entry.isDirectory()) {
          entryFile.mkdir();
          continue;
        }

        // 寫入文件
        try (InputStream is = zip.getInputStream(entry)) {
          Files.copy(is, entryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
      }
    }
  }

  /**
   * 使用zip壓縮檔案、資料夾.<br>
   * 使用範例:
   *
   * <pre>
   * // 壓縮檔案
   * Utility.get().zip("D:/myFiles/file.log", "D:/test/file.zip");
   * </pre>
   *
   * <pre>
   * // 壓縮資料夾
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
   * 刪除檔案或目錄(含子目錄、檔案).
   *
   * @param path 路徑
   */
  public void deleteFiles(String path) {
    deleteFiles(new File(path));
  }

  /**
   * 刪除檔案或目錄(含子目錄、檔案).
   *
   * @param file 檔案
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
   * 取得RecordCursor的欄位值設置為obj有標記annation SerializedName對應的欄位值.
   *
   * @param obj .
   * @param record .
   */
  public void setFieldsUsingSerializedName(Object obj, RecordCursor record) {
    setAllFieldsValue(obj, record, o -> o.value());
  }

  public void setFieldsUsingAlternate(Object obj, RecordCursor record, int alternateIndex) {
    setAllFieldsValue(
        obj, record, o -> o.alternate().length > 0 ? o.alternate()[alternateIndex] : o.value());
  }

  /**
   * 取得RecordCursor的欄位值設置為obj有標記annation SerializedName對應的欄位值.
   *
   * @param obj .
   * @param record .
   * @param listener .
   */
  public void setAllFieldsValue(
      Object obj, RecordCursor record, Function<SerializedName, String> listener) {
    recursiveClassFields(
        obj.getClass(),
        (field) -> {
          SerializedName serializedName = field.getAnnotation(SerializedName.class);
          String value = record.field(listener.apply(serializedName));

          setValue(obj, field, value);
        });
  }

  /**
   * 對代理物件的指定欄位賦值.
   *
   * @param obj 被代理設值的物件.
   * @param field 欄位.
   * @param value 值.
   * @throws NumberFormatException .
   * @throws IllegalArgumentException .
   * @throws IllegalAccessException .
   */
  public void setValue(Object obj, Field field, String value)
      throws NumberFormatException, IllegalArgumentException, IllegalAccessException {
    Class<?> fieldType = field.getType();

    // 基本型別(非class)
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
