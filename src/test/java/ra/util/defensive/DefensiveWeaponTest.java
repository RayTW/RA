package ra.util.defensive;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;
import org.junit.Test;
import ra.util.annotation.Defenses;
import ra.util.annotation.Required;

/** Test class. */
public class DefensiveWeaponTest {

  @Test
  public void testDoDefensiveSpace() {
    DefensiveWeapon obj = DefensiveWeapon.get();
    JSONObject json = new JSONObject();
    json.put("name", "xxx");

    boolean result = obj.doDefensive(MyString.class, json, (a, b) -> {});

    assertTrue(result);
  }

  @Test
  public void testDoDefensiveNoSpace() {
    DefensiveWeapon obj = DefensiveWeapon.get();
    JSONObject json = new JSONObject();
    json.put("name", "xxx bbb");

    boolean result = obj.doDefensive(MyString.class, json, (a, b) -> {});

    assertFalse(result);
  }

  @Test
  public void testDoDefensiveValidInt() {
    DefensiveWeapon obj = DefensiveWeapon.get();
    JSONObject json = new JSONObject();
    json.put("id", "2");
    json.put("value", "2");

    boolean result =
        obj.doDefensive(
            MyInt.class,
            json,
            (a, b) -> {
              System.out.println(a + "=" + b);
            });

    assertTrue(result);
  }

  @Test
  public void testDoDefensiveValidRequired() {
    DefensiveWeapon obj = DefensiveWeapon.get();
    JSONObject json = new JSONObject();

    boolean result =
        obj.doDefensive(
            MyRequired.class,
            json,
            (a, b) -> {
              System.out.println(b.getDetail() + "=" + b.getValue());
            });

    assertFalse(result);
  }

  @Test
  public void testDoDefensiveInvalidInt() {
    DefensiveWeapon obj = DefensiveWeapon.get();
    JSONObject json = new JSONObject();
    json.put("id", "abc111");

    boolean result = obj.doDefensive(MyInt.class, json, (a, b) -> {});

    assertFalse(result);
  }

  class MyString {
    @Defenses(space = true)
    private String name;
  }

  class MyInt {
    @Defenses(normalInt = true)
    private int id;

    @SerializedName("value")
    private int value;
  }

  class MyRequired {
    @Required private String name;
  }
}
