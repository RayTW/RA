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
 * A lightweight set of re-usable functions for general purpose parsing. support XML parsing styles.
 *
 * @author Ray Li
 */
public class XmlConfigParser implements ConfigParser {

  public XmlConfigParser() {}

  /**
   * Setting value into the variable which below the Class, after reading the file that is earmarked
   * path. This can be used in the following example:
   *
   * <pre>{@code
   * public static void initConfig(String path) {
   *   new SimpleXmlParser().fill(ConfigReader.class, path);
   * }
   * }</pre>
   *
   * <p>.
   *
   * @param clazz plan to be used Class
   * @param path earmarked xml file path
   */
  @Override
  public void fill(Class<?> clazz, String path) {
    fill(clazz, path, false);
  }

  /**
   * Setting value into the variable which below the Class, after reading the file that is earmarked
   * path. This can be used in the following example:
   *
   * <pre>{@code
   * public static void initConfig(String path) {
   *   new SimpleXmlParser().fill(ConfigReader.class, path);
   * }
   * }</pre>
   *
   * <p>. Skip reading the variable which was tag true,or throw Exception when it`s exists and was
   * tag false
   *
   * @param clazz plan to be used Class
   * @param path earmarked xml file path
   * @param igonreException Skip reading the variable which was tag true, or throw an Exception when
   *     it´s exists any error and was tag false
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
