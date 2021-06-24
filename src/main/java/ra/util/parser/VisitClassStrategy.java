package ra.util.parser;

import java.lang.reflect.Field;

/**
 * .
 *
 * @author Ray Li
 */
public interface VisitClassStrategy {
  /**
   *
   *
   * <pre>
   * SQLStatementUtility.get().recursiveClassFields(MyObject.class, obj, (field) -> {
   *   // 可對指定的class取得每個欄位物件
   * });
   * </pre>
   *
   * @param field .
   * @throws IllegalAccessException .
   */
  public void shouldVisitField(Field field) throws IllegalAccessException;

  /**
   * 覆寫後，可判斷clazz是否是想要跳過的class，回傳true，預設遇到Object時，就不再往super class訪問fields.
   *
   * @param clazz .
   */
  public default boolean shouldSkipClass(Class<?> clazz) {
    if (clazz == Object.class) {
      return true;
    }

    return false;
  }
}
