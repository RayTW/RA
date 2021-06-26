package ra.util.parser;

import java.lang.reflect.Field;

/**
 * Visits class.
 *
 * @author Ray Li
 */
public interface VisitClassStrategy {
  /**
   * SQLStatementUtility.get().recursiveClassFields(MyObject.class, obj, (field) -> { Each field
   * object can be obtained for the specified class. });
   *
   * @param field target field.
   * @throws IllegalAccessException IllegalAccessException.
   */
  public void shouldVisitField(Field field) throws IllegalAccessException;

  /**
   * Decide whether to skip access to the parent class.
   *
   * @param clazz target class.
   * @return If return true will skip visit parent class.
   */
  public default boolean shouldSkipClass(Class<?> clazz) {
    if (clazz == Object.class) {
      return true;
    }

    return false;
  }
}
