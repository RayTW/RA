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
 * .
 *
 * @author Ray Li
 */
public class Sql {
  private static Sql sInstance;

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
   * .
   *
   * <p>.
   */
  public static Sql get() {
    if (sInstance == null) {
      synchronized (Sql.class) {
        if (sInstance == null) {
          sInstance = new Sql();
        }
      }
    }
    return sInstance;
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
   * 將指定class有SerializedName標記的member串成insert xxx的sql語法<br>
   * 採用BigIntFunction、StringFunction.
   *
   * @param tableName 資料表名稱
   * @param object 指定class創建的物件
   */
  public <T> String toInsert(String tableName, T object) {
    return toInsert(tableName, object, (o) -> o.value(), bigInt, string);
  }

  /**
   * 將指定class有SerializedName標記的member串成insert xxx的sql語法<br>
   * 預設取SerializedName的alternate，若沒有alternate屬性就取value<br>
   * 採用BigIntFunction、StringFunction.
   *
   * @param tableName 資料表名稱
   * @param object 指定class創建的物件
   * @param index alternate的索引值
   */
  public <T> String toInsert(String tableName, T object, int index) {
    return toInsert(tableName, object, index, bigInt, string);
  }

  /**
   * 將指定class有SerializedName標記的member串成insert xxx的sql語法<br>
   * 預設取SerializedName的alternate，若沒有alternate屬性就取value<br>
   * 採用BigIntFunction、StringFunction<br>
   * 將會處理annotation AutoIncremen，若不處理請改用 Sql#toInsert(String, Object, int).
   *
   * @see Sql#toInsert(String, Object, int)
   * @param tableName 資料表名稱
   * @param object 指定class創建的物件
   * @param index alternate的索引值
   * @param function .
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
   * 將指定class有SerializedName標記的member串成insert xxx的sql語法.
   *
   * @param tableName 資料表名稱
   * @param object 指定class創建的物件
   * @param listener .
   * @param function .
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
              // 內部類別不處理
              if (field.getName().startsWith("this$")) {
                return;
              }

              // 若欄位標記 @ExcludeIfNull且value為null時也不處理
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
   * 將指定class有SerializedName標記的member串成insert xxx的sql語法<br>
   * 採用AutoIncrementFunction、BigIntFunction、StringFunction.
   *
   * @param tableName 資料表名稱
   * @param object 指定class創建的物件
   */
  public <T> String toInsertAutoIncrement(String tableName, T object) {
    return toInsert(tableName, object, (o) -> o.value(), autoIncrementFunction, bigInt, string);
  }

  /**
   * 將指定class有SerializedName標記的member串成insert xxx的sql語法<br>
   * 採用AutoIncrementFunction、BigIntFunction、StringFunction.
   *
   * @param tableName 資料表名稱
   * @param object 指定class創建的物件
   * @param listener .
   */
  public <T> String toInsertAutoIncrement(
      String tableName, T object, Function<SerializedName, String> listener) {
    return toInsert(tableName, object, listener, autoIncrementFunction, bigInt, string);
  }

  /**
   * 將指定class有SerializedName標記的member串成insert xxx的sql語法<br>
   * 預設取SerializedName的alternate，若沒有alternate屬性就取value<br>
   * 採用AutoIncrementFunction、BigIntFunction、StringFunction<br>
   * 將會處理annotation AutoIncremen，若不處理請改用 Sql#toInsert(String, Object, int).
   *
   * @see Sql#toInsert(String, Object, int)
   * @param tableName 資料表名稱
   * @param object 指定class創建的物件
   * @param index alternate的索引值
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
   * 將指定class有SerializedName標記的member串成insert xxx的sql語法，若有field標記為@Quote時會跳脫"\"<br>
   * 採用QuoteJson、BigIntFunction、StringFunction.
   *
   * @param tableName 資料表名稱
   * @param object 指定class創建的物件
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

  /** 若有class標記@AutoIncrement時，將會針對該欄位取值時轉換成"null". */
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

  /** 若有class標記@Quote時，將會對該欄位取值後再經由EscapeString.mysqlRealEscapeString(String)轉換. */
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
