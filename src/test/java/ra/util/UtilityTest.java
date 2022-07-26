package ra.util;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Function;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

/** Test class. */
public class UtilityTest {

  @Test
  public void testGetThrowableDetail() {
    String actual = Utility.get().getThrowableDetail(new RuntimeException());

    assertThat(actual, startsWith("java.lang.RuntimeException"));
  }

  @Test
  public void testToExceptionStackTracel() {
    String actual = Utility.get().toExceptionStackTrace(new RuntimeException());

    assertThat(actual, startsWith("java.lang.RuntimeException"));
  }

  @Test
  public void testSformat() {
    String actual = Utility.get().sformat(Double.MAX_VALUE);

    assertThat(actual, startsWith("17976931348623157000"));
  }

  @Test
  public void testGetFormatData() {
    MyObject obj = new MyObject();

    obj.objObject = "123";
    Utility.get().replaceMember(obj, "name", "123");

    assertSame(obj.objObject, "123");
  }

  @Test
  public void testShowAll() {
    Utility.get().showAll(Utility.class);

    assertTrue(true);
  }

  @Test
  public void testShowSetterGetter() {
    Utility.get().showSetterGetter(MyObject.class);

    assertTrue(true);
  }

  @Test
  public void testSetValue()
      throws NumberFormatException, IllegalArgumentException, IllegalAccessException {
    MyObject obj = new MyObject();
    Function<String, Field> fields =
        t -> {
          try {
            Field f = null;
            f = obj.getClass().getDeclaredField(t);
            f.setAccessible(true);
            return f;
          } catch (Exception e) {
            e.printStackTrace();
          }
          return null;
        };

    Utility.get().setValue(obj, fields.apply("valueInt"), "123");
    assertEquals(obj.valueInt, 123);

    Utility.get().setValue(obj, fields.apply("valueLong"), "123");
    assertEquals(obj.valueLong, 123);

    Utility.get().setValue(obj, fields.apply("valueDouble"), "123.123");
    assertEquals(obj.valueDouble, 123.123, 3);

    Utility.get().setValue(obj, fields.apply("valueShort"), "123");
    assertEquals(obj.valueShort, 123);

    Utility.get().setValue(obj, fields.apply("valueFloat"), "123.123");
    assertEquals(obj.valueFloat, 123.123, 3);

    Utility.get().setValue(obj, fields.apply("valueBolean"), "false");
    assertEquals(obj.valueBolean, false);

    Utility.get().setValue(obj, fields.apply("valueByte"), "127");
    assertEquals(obj.valueByte, 127);

    Utility.get().setValue(obj, fields.apply("valueChar"), "a");
    assertEquals(obj.valueChar, 'a');

    Utility.get().setValue(obj, fields.apply("objInteger"), "123");
    assertThat(obj.objInteger, equalTo(Integer.valueOf("123")));

    Utility.get().setValue(obj, fields.apply("objLong"), "123");
    assertThat(obj.objLong, equalTo(Long.valueOf("123")));

    Utility.get().setValue(obj, fields.apply("objDouble"), "123.123");
    assertEquals(obj.objDouble, 123.123, 3);

    Utility.get().setValue(obj, fields.apply("objShort"), "123");
    assertThat(obj.objShort, equalTo(Short.valueOf("123")));

    Utility.get().setValue(obj, fields.apply("objFloat"), "123.123");
    assertEquals(obj.objFloat, 123.123, 3);

    Utility.get().setValue(obj, fields.apply("objBoolean"), "true");
    assertThat(obj.objBoolean, equalTo(Boolean.TRUE));

    Utility.get().setValue(obj, fields.apply("objByte"), "127");
    assertThat(obj.objByte, equalTo(Byte.valueOf("127")));

    Utility.get().setValue(obj, fields.apply("objCharacter"), "z");
    assertThat(obj.objCharacter, equalTo('z'));

    Utility.get().setValue(obj, fields.apply("objObject"), "Object");
    assertThat(obj.objObject, equalTo("Object"));
  }

  @SuppressWarnings("unused")
  class MyObject {
    private int valueInt;
    private long valueLong;
    private double valueDouble;
    private short valueShort;
    private float valueFloat;
    private boolean valueBolean;
    private byte valueByte;
    private char valueChar;
    private Integer objInteger;
    private Long objLong;
    private Double objDouble;
    private Short objShort;
    private Float objFloat;
    private Boolean objBoolean;
    private Byte objByte;
    private Character objCharacter;
    private Object objObject;
    private int[] valueIntArray;
    private long[] valueLongArray;
    private double[] valueDoubleArray;
    private short[] valueShortArray;
    private float[] valueFloatArray;
    private boolean[] valueBoleanArray;
    private byte[] valueByteArray;
    private char[] valueCharArray;
    private Integer[] objIntegerArray;
    private Long[] objLongArray;
    private Double[] objDoubleArray;
    private Short[] objShortArray;
    private Float[] objFloatArray;
    private Boolean[] objBooleanArray;
    private Byte[] objByteArray;
    private Character[] objCharacterArray;
    private Object[] objObjectArray;
  }

  @Test
  public void testZipUnzip() throws IOException {
    String path = null;
    try {
      Path root = Files.createTempDirectory(UUID.randomUUID().toString());
      path = root.toFile().getPath();
      String textPath = path + "/test.txt";

      Files.write(Paths.get(textPath), "test".getBytes());
      Utility.get().zip(textPath, path + "/zip/test.zip");
      Utility.get().unzip(path + "/zip/test.zip", path + "/zip");

      assertEquals("test", Utility.get().readFile(path + "/zip/test.txt"));
    } finally {
      Utility.get().deleteFiles(path);
    }
  }

  @Test
  public void testPrettyPrintJson() throws IOException {
    String path = null;
    try {
      Path root = Files.createTempDirectory(UUID.randomUUID().toString());
      path = root.toFile().getPath();
      final String textPath = path + "/test.json";
      JSONObject json = new JSONObject();

      json.put("key", "value");

      JSONArray ary = new JSONArray();

      ary.put("111");

      JSONObject value = new JSONObject();

      value.put("name", "name");
      value.put("id", 111);
      ary.put(value);

      json.put("array", ary);

      Files.write(Paths.get(textPath), json.toString().getBytes());
      JSONObject actul = Utility.get().readFileToJsonObject(textPath);
      String actual = Utility.get().prettyPrintJson(actul);

      assertThat(actual, both(startsWith("{")).and(endsWith("}")));
    } finally {
      Utility.get().deleteFiles(path);
    }
  }
}
