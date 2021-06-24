package ra.util.sql;

import static org.junit.Assert.assertNotNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import ra.util.annotation.AutoIncrement;
import ra.util.annotation.BigInt;
import ra.util.annotation.ExcludeIfNull;
import ra.util.annotation.Quote;

/** Test class. */
public class SqlTest {

  @Test
  public void testToInsertUseingObjectDefaultNullWithAnnationExcludeIfNull() {
    MyObject obj = new MyObject();

    obj.name = null;
    obj.value = null;
    obj.value2 = 1;

    String expected = "INSERT INTO tableName (value2) VALUES (1);";
    String actual = Sql.get().toInsert("tableName", obj);

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testToInsertUseingObjectHasValueWithAnnationExcludeIfNull() {
    MyObject obj = new MyObject();

    obj.name = "abc";
    obj.value = 5;
    obj.value2 = 1;

    String expected = "INSERT INTO tableName (name,value,value2) VALUES ('abc',5,1);";
    String actual = Sql.get().toInsert("tableName", obj);

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testGetAutoIncrementFunction() {
    assertNotNull(Sql.get().getAutoIncrementFunction());
  }

  @Test
  public void testGetBigIntFunction() {
    assertNotNull(Sql.get().getBigIntFunction());
  }

  @Test
  public void testGetQuoteJson() {
    assertNotNull(Sql.get().getQuoteJson());
  }

  @Test
  public void testGetStringFunctio() {
    assertNotNull(Sql.get().getStringFunction());
  }

  @Test
  public void testToInsertUseingMyObjectAutoIncrement() {
    MyObjectAutoIncrement obj = new MyObjectAutoIncrement();

    obj.id = 1;
    obj.name = "name";

    String expected = "INSERT INTO tableName (id,name) VALUES (1,'name');";
    String actual = Sql.get().toInsertAutoIncrement("tableName", obj);

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testToInsertUseingMyObjectQuoteJson() {
    MyObjectQuoteJson obj = new MyObjectQuoteJson();

    JSONObject json = new JSONObject();
    json.put("key", "value!@#$%^&*()_");

    obj.name = json.toString();
    obj.bingInt = "" + Integer.MAX_VALUE;

    String expected =
        "INSERT INTO tableName (name,bingInt) VALUES "
            + "('{\\\"key\\\":\\\"value!@#$%^&*()_\\\"}',2147483647);";
    String actual = Sql.get().toInsertQuoteJson("tableName", obj);

    Assert.assertEquals(expected, actual);
  }

  class MyObject {

    @ExcludeIfNull
    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @ExcludeIfNull
    @SerializedName("value")
    private Integer value;

    @Expose
    @ExcludeIfNull
    @SerializedName("value2")
    private int value2;
  }

  class MyObjectAutoIncrement {
    @AutoIncrement
    @Expose
    @SerializedName("id")
    private long id;

    @Expose
    @SerializedName("name")
    private String name;
  }

  class MyObjectQuoteJson {
    @Quote
    @Expose
    @SerializedName("name")
    private String name;

    @BigInt
    @Expose
    @SerializedName("bingInt")
    private String bingInt;
  }
}
