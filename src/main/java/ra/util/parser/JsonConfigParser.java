package ra.util.parser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Function;
import org.json.JSONArray;
import org.json.JSONObject;
import ra.util.Utility;

/**
 * A lightweight set of re-usable functions for general purpose parsing. support JSON parsing
 * styles.
 *
 * @author Ray Li
 */
public class JsonConfigParser {

  /**
   * A lightweight set of re-usable functions for general purpose parsing. support JSON parsing
   * styles.
   */
  public JsonConfigParser() {}

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
   * @param path earmarked JSON file path
   */
  public void fill(Class<?> clazz, String path) throws IllegalArgumentException {
    fill(
        key -> {
          if ("config".equals(key)) {
            return clazz;
          }
          return null;
        },
        path);
  }

  /**
   * Setting value into the variable which below the Class, after reading the file that is earmarked
   * path. This can be used in the following example:
   *
   * <pre>{@code
   * public static void initConfig(String path) {
   *   new SimpleXmlParser().fillFromJSON(ConfigReader.class, path);
   * }
   * }</pre>
   *
   * <p>.
   *
   * @param listener Provider the Class which was earmarked by key word in JSON file
   * @param path earmarked JSON file path it´s exists any error and was tag false
   */
  public void fill(Function<String, Class<?>> listener, String path)
      throws IllegalArgumentException {
    if (path.isEmpty()) {
      path = "./";
    }
    JSONObject root = Utility.get().readFileToJsonObject(path);

    root.keySet()
        .forEach(
            key -> {
              JSONObject json = root.getJSONObject(key);
              Class<?> clazz = listener.apply(key);

              if (clazz == null) {
                return;
              }

              Field[] fields = clazz.getDeclaredFields();

              for (Field f : fields) {
                if (Modifier.isFinal(f.getModifiers()) || Modifier.isPrivate(f.getModifiers())) {
                  continue;
                }

                if (json.isNull(f.getName())) {
                  System.out.println("key[" + key + "],Config欄位[" + f.getName() + "]=null");
                  continue;
                }

                try {
                  if (f.getType() == String.class) {
                    f.set(clazz, json.getString(f.getName()));
                  } else if (f.getType() == Integer.TYPE) {
                    f.setInt(clazz, json.getInt(f.getName()));
                  } else if (f.getType() == Long.TYPE) {
                    f.setLong(clazz, json.getLong(f.getName()));
                  } else if (f.getType() == Boolean.TYPE) {
                    f.setBoolean(clazz, json.getBoolean(f.getName()));
                  } else if (f.getType() == Double.TYPE) {
                    f.setDouble(clazz, json.getDouble(f.getName()));
                  } else if (f.getType() == Float.TYPE) {
                    f.setFloat(clazz, Float.parseFloat(json.get(f.getName()).toString()));
                  } else if (f.getType() == Short.TYPE) {
                    f.setShort(clazz, Short.parseShort(json.get(f.getName()).toString()));
                  } else if (f.getType() == JSONObject.class) {
                    f.set(clazz, json.getJSONObject(f.getName()));
                  } else if (f.getType() == JSONArray.class) {
                    f.set(clazz, json.getJSONArray(f.getName()));
                  }
                } catch (Exception e) {
                  throw new IllegalArgumentException(
                      "parser error, field["
                          + f.getName()
                          + "],value["
                          + json.get(f.getName())
                          + "]",
                      e);
                }
              }
            });
  }
}
