package ra.util.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * 解析XML格式的檔案設定檔.
 *
 * @author Ray Li
 */
public class XmlConfigParser implements ConfigParser {

  public XmlConfigParser() {}

  /**
   * 把path的xml檔案各欄位讀取後存入clazz對應的類別成員變數，用法如下:
   *
   * <pre>{@code
   * public static void initConfig(String path) {
   *   new SimpleXmlParser().fill(ConfigReader.class, path);
   * }
   * }</pre>
   *
   * <p>.
   *
   * @param clazz .
   * @param path .
   */
  @Override
  public void fill(Class<?> clazz, String path) {
    fill(clazz, path, false);
  }

  /**
   * 把path的xml檔案各欄位讀取後存入clazz對應的類別成員變數，用法如下:
   *
   * <pre>{@code
   * public static void initConfig(String path) {
   *   new SimpleXmlParser().fill(ConfigReader.class, path);
   * }
   * }</pre>
   *
   * <p>.
   *
   * @param clazz .
   * @param path .
   * @param igonreException 忽略錯誤，若為true時，有某個欄位未設定會繼續往下一個欄位讀取，為flase則會拋出Exception
   */
  @Override
  public void fill(Class<?> clazz, String path, boolean igonreException) {
    if (path.isEmpty()) {
      path = "./";
    }

    HashMap<String, String> xml = new HashMap<>();

    try {
      Optional<XMLStreamReader> readerRef = null;
      try {
        readerRef =
            Optional.ofNullable(
                XMLInputFactory.newInstance()
                    .createXMLStreamReader(new BufferedReader(new FileReader(path))));
      } catch (Exception e) {
        ClassLoader cl = clazz.getClassLoader();
        readerRef =
            Optional.ofNullable(
                XMLInputFactory.newInstance().createXMLStreamReader(cl.getResourceAsStream(path)));
      }

      readerRef.ifPresent(
          br -> {
            try {
              br.nextTag();

              if (br.isStartElement() && "CONFIG".equalsIgnoreCase(br.getLocalName())) {
                br.nextTag();
                while (br.hasNext() && br.isStartElement()) {
                  if (br.isStartElement()) {
                    xml.put(br.getLocalName(), br.getElementText());
                  }
                  br.nextTag();
                }
              }

              br.close();
            } catch (XMLStreamException e) {
              e.printStackTrace();
            }
          });

      Field[] fields = clazz.getDeclaredFields();

      for (Field f : fields) {
        if (Modifier.isFinal(f.getModifiers())) {
          continue;
        }

        try {
          if (f.getType() == String.class) {
            f.set(clazz, xml.get(f.getName()));
          } else if (f.getType() == Integer.TYPE) {
            f.setInt(clazz, Integer.parseInt(xml.get(f.getName())));
          } else if (f.getType() == Long.TYPE) {
            f.setLong(clazz, Long.parseLong(xml.get(f.getName())));
          } else if (f.getType() == Boolean.TYPE) {
            f.setBoolean(clazz, Boolean.parseBoolean(xml.get(f.getName())));
          } else if (f.getType() == Double.TYPE) {
            f.setDouble(clazz, Double.parseDouble(xml.get(f.getName())));
          } else if (f.getType() == Float.TYPE) {
            f.setFloat(clazz, Float.parseFloat(xml.get(f.getName())));
          } else if (f.getType() == Short.TYPE) {
            f.setShort(clazz, Short.parseShort(xml.get(f.getName())));
          }
          System.out.println(f.get(clazz));
        } catch (Exception e) {
          System.out.println(
              "parser error, field["
                  + f.getName()
                  + "],file path["
                  + path
                  + "],value["
                  + xml.get(f.getName())
                  + "]");

          if (!igonreException) {
            throw e;
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void fill(Function<String, Class<?>> listener, String path, boolean igonreException) {
    throw new UnsupportedOperationException();
  }
}
