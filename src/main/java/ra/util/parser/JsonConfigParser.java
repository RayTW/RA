package ra.util.parser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Function;
import org.json.JSONArray;
import org.json.JSONObject;
import ra.util.Utility;

/**
 * 解析JSON格式的檔案設定檔.
 *
 * @author Ray Li
 */
public class JsonConfigParser implements ConfigParser {

  public JsonConfigParser() {}

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
   * @param clazz 類別
   * @param path 路徑
   */
  @Override
  public void fill(Class<?> clazz, String path) {
    fill(clazz, path, false);
  }

  @Override
  public void fill(Class<?> clazz, String path, boolean igonreException) {
    fill(
        key -> {
          if ("config".equals(key)) {
            return clazz;
          }
          return null;
        },
        path,
        igonreException);
  }

  /**
   * 把path的JSON格式檔案各欄位讀取後存入clazz對應的類別成員變數，用法如下:
   *
   * <pre>{@code
   * public static void initConfig(String path) {
   *   new SimpleXmlParser().fillFromJSON(ConfigReader.class, path);
   * }
   * }</pre>
   *
   * <p>.
   *
   * @param listener .
   * @param path .
   * @param igonreException 忽略錯誤，若為true時，有某個欄位未設定會繼續往下一個欄位讀取，為flase則會拋出Exception
   */
  @Override
  public void fill(Function<String, Class<?>> listener, String path, boolean igonreException) {
    if (path.isEmpty()) {
      path = "./";
    }
    JSONObject root = Utility.get().readFileToJsonObject(path);

    root.keySet()
        .forEach(
            key -> {
              JSONObject json = root.getJSONObject(key);

              try {
                Class<?> clazz = listener.apply(key);

                if (clazz == null) {
                  return;
                }

                Field[] fields = clazz.getDeclaredFields();

                for (Field f : fields) {
                  if (Modifier.isFinal(f.getModifiers())) {
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
                    if (!igonreException) {
                      throw e;
                    }
                  }
                }
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
  }
}
