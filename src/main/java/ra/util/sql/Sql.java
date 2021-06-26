package ra.util.sql;

import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Function;
import ra.util.Utility;
import ra.util.annotation.AutoIncrement;
import ra.util.annotation.BigInt;
import ra.util.annotation.ExcludeIfNull;
import ra.util.annotation.Quote;

/**
 * SQL class.
 *
 * @author Ray Li
 */
public class Sql {
  private static Sql instance;

  private AutoIncrementFunction autoIncrementFunction;
  private StringFunction string;
  private QuoteJson quoteJson;
  private BigIntFunction bigInt;

  private Sql() {
    autoIncrementFunction = new AutoIncrementFunction();
    string = new StringFunction();
    quoteJson = new QuoteJson();
    bigInt = new BigIntFunction();
  }

  /**
   * Returns instance of sql.
   *
   * @return {@link Sql}
   */
  public static Sql get() {
    if (instance == null) {
      synchronized (Sql.class) {
        if (instance == null) {
          instance = new Sql();
        }
      }
    }
    return instance;
  }

  public AutoIncrementFunction getAutoIncrementFunction() {
    return autoIncrementFunction;
  }

  public StringFunction getStringFunction() {
    return string;
  }

  public QuoteJson getQuoteJson() {
    return quoteJson;
  }

  public BigIntFunction getBigIntFunction() {
    return bigInt;
  }

  /**
   * Convert object to the SQL statement.
   *
   * @param <T> class type
   * @param tableName table name
   * @param object target
   * @return SQL statement
   */
  public <T> String toInsert(String tableName, T object) {
    return toInsert(tableName, object, (o) -> o.value(), bigInt, string);
  }

  /**
   * Convert object to the SQL statement.
   *
   * @param <T> class type
   * @param tableName table name
   * @param object target
   * @param index alternate
   * @return SQL statement
   */
  public <T> String toInsert(String tableName, T object, int index) {
    return toInsert(tableName, object, index, bigInt, string);
  }

  /**
   * Convert object to the SQL statement.
   *
   * @see Sql#toInsert(String, Object, int)
   * @param <T> class type
   * @param tableName table name
   * @param object target
   * @param index alternate
   * @param function function
   * @return SQL statement
   */
  public <T> String toInsert(
      String tableName, T object, int index, StatementsFunction... function) {
    return toInsert(
        tableName,
        object,
        o -> o.alternate().length > 0 ? o.alternate()[index] : o.value(),
        function);
  }

  /**
   * Convert object to the SQL statement.
   *
   * @param <T> class type
   * @param tableName table name
   * @param object target
   * @param listener listener
   * @param function function
   * @return SQL statement
   */
  public <T> String toInsert(
      String tableName,
      T object,
      Function<SerializedName, String> listener,
      StatementsFunction... function) {
    StringBuilder sb = new StringBuilder();
    StringBuilder sb1 = new StringBuilder();

    Utility.get()
        .recursiveClassFields(
            object.getClass(),
            (field) -> {
              // Ignore internal class
              if (field.getName().startsWith("this$")) {
                return;
              }

              // Ignore when class field '@ExcludeIfNull' and the value is null.
              if (field.isAnnotationPresent(ExcludeIfNull.class)) {
                if (!field.isAccessible()) {
                  field.setAccessible(true);
                }
                if (field.get(object) == null) {
                  return;
                }
              }
              SerializedName serializedName = field.getAnnotation(SerializedName.class);

              if (serializedName == null) {
                Objects.requireNonNull(
                    serializedName,
                    "the field '" + field.getName() + "' no definite annotation SerializedName");
              }

              if (sb.length() != 0) {
                sb.append(",");
                sb1.append(",");
              }

              sb.append(listener.apply(serializedName));

              if (field.getGenericType() == String.class) {
                for (StatementsFunction f : function) {
                  if (f.apply(field, sb1, object)) {
                    break;
                  }
                }
              } else {
                sb1.append(String.valueOf(field.get(object)));
              }
            });

    StringBuilder ret = new StringBuilder();
    ret.append("INSERT INTO ");
    ret.append(tableName);
    ret.append(" (");
    ret.append(sb.toString());
    ret.append(") VALUES (");
    ret.append(sb1.toString());
    ret.append(");");

    sb.setLength(0);
    sb1.setLength(0);

    return ret.toString();
  }

  /**
   * Convert object to the SQL statement.
   *
   * @param <T> class type
   * @param tableName table name
   * @param object target
   * @return SQL statement
   */
  public <T> String toInsertAutoIncrement(String tableName, T object) {
    return toInsert(tableName, object, (o) -> o.value(), autoIncrementFunction, bigInt, string);
  }

  /**
   * Convert object to the SQL statement.
   *
   * @param <T> class type
   * @param tableName table name
   * @param object target
   * @param listener listener
   * @return SQL statement
   */
  public <T> String toInsertAutoIncrement(
      String tableName, T object, Function<SerializedName, String> listener) {
    return toInsert(tableName, object, listener, autoIncrementFunction, bigInt, string);
  }

  /**
   * Convert object to the SQL statement.
   *
   * @see Sql#toInsert(String, Object, int)
   * @param <T> class type
   * @param tableName table name
   * @param object target
   * @param index alternate
   * @return SQL statement
   */
  public <T> String toInsertUsingAlternate(String tableName, T object, int index) {
    return toInsert(
        tableName,
        object,
        o -> o.alternate().length > 0 ? o.alternate()[index] : o.value(),
        autoIncrementFunction,
        bigInt,
        string);
  }

  /**
   * Convert object to the SQL statement.
   *
   * @param <T> class type
   * @param tableName table name
   * @param object target
   * @return SQL statement
   */
  public <T> String toInsertQuoteJson(String tableName, T object) {
    return toInsert(tableName, object, (o) -> o.value(), quoteJson, bigInt, string);
  }

  private class BigIntFunction implements StatementsFunction {
    @Override
    public Boolean apply(Field field, StringBuilder sb1, Object object) {
      if (field.isAnnotationPresent(BigInt.class)) {
        try {
          sb1.append(String.valueOf(field.get(object)));
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }

        return Boolean.TRUE;
      }

      return Boolean.FALSE;
    }
  }

  private class StringFunction implements StatementsFunction {
    @Override
    public Boolean apply(Field field, StringBuilder sb1, Object object) {
      sb1.append("'");
      try {
        sb1.append(String.valueOf(field.get(object)));
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
      sb1.append("'");

      return Boolean.TRUE;
    }
  }

  private class AutoIncrementFunction implements StatementsFunction {
    @Override
    public Boolean apply(Field field, StringBuilder sb1, Object object) {
      boolean isAutoIncrement = field.isAnnotationPresent(AutoIncrement.class);
      if (isAutoIncrement) {
        sb1.append("null");
        return Boolean.TRUE;
      } else {
        return Boolean.FALSE;
      }
    }
  }

  private class QuoteJson implements StatementsFunction {
    @Override
    public Boolean apply(Field field, StringBuilder sb1, Object object) {
      boolean check = field.isAnnotationPresent(Quote.class);
      if (check) {
        sb1.append("'");
        try {
          sb1.append(EscapeString.mysqlRealEscapeString(String.valueOf(field.get(object))));
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
        sb1.append("'");
        return Boolean.TRUE;
      } else {
        return Boolean.FALSE;
      }
    }
  }
}
