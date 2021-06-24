package ra.util.defensive;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import org.json.JSONObject;
import ra.util.annotation.Defenses;
import ra.util.annotation.Optional;

/**
 * Defensive the json request which is unsafe.
 *
 * <pre>
 * Three kinds of the default defensive action：
 * 1. All require parameters exists.
 * reason: DefaultReason.REASON_NOT_INT
 * 2. Type:String exists space.
 * reason: DefaultReason.REASON_HAS_SPACE
 * 3. Type:String only numbers.
 * reason: DefaultReason.REASON_NOT_INT
 * </pre>
 *
 * <p>Add Tag in the object's field：@Defenses(normalInt=true,space=true)
 *
 * @author Kevin Tsai
 */
public class DefensiveWeapon {
  private static DefensiveWeapon sInstance;
  private List<DefensiveFunction> functions;

  private DefensiveWeapon() {
    functions = new CopyOnWriteArrayList<>();
    addDefensive(new ExistsFunction());
    addDefensive(new SpaceFunction());
    addDefensive(new NormalMathFunction());
  }

  /** get the Singleton defensive tool. */
  public static DefensiveWeapon get() {
    if (sInstance == null) {
      synchronized (DefensiveWeapon.class) {
        if (sInstance == null) {
          sInstance = new DefensiveWeapon();
        }
      }
    }
    return sInstance;
  }

  /**
   * Create new Defensive Method.
   *
   * <pre>
   * 1.implements DefensiveFunction interface
   * {@code
   * private class LimitRangeFunction implements DefensiveFunction{
   * &#64;Override
   * public DefensiveEnum apply(Field field,String jsonKey,JSONObject json) {
   * Override
   * public DefensiveReason getReason() {
   *   return 請實作DefensiveReason;
   * }
   *
   * ExpansionDefenses defense = field.getAnnotation(ExpansionDefenses.class);
   * boolean check =  defense != null && defense.others();
   * if (check) {
   *   int value = json.getInt(jsonKey);
   *     if( value < 0 || value > 100 ) {
   *       return DefensiveEnum.FOOL;
   *   }
   *     return DefensiveEnum.NON_FOOL;
   * } else {
   *   return DefensiveEnum.NONE;
   * }
   * }
   *
   * }
   * }
   * 2. Create new Annoation
   * 3. Tag the Object's filed
   * 4. addDefensive(new Annoation tag)
   * </pre>
   *
   * @param function Defensive method.
   */
  public void addDefensive(DefensiveFunction function) {
    functions.add(function);
  }

  /**
   * Enumerate json keys which mapping the class's field defensive.
   *
   * @param clazz transform class (all json values)
   * @param json receive JSONObject.
   * @param defensiveListener (error json key,reason)
   * @return boolean true:all pass.
   */
  public boolean doDefensive(
      Class<?> clazz, JSONObject json, BiConsumer<String, DefensiveReason> defensiveListener) {
    Field[] fields = clazz.getDeclaredFields();
    Field field = null;

    for (int i = 0; i < fields.length; i++) {
      if (fields[i].isSynthetic()) {
        continue;
      }
      field = fields[i];
      field.setAccessible(true);

      for (DefensiveFunction function : functions) {
        if (!function.defensive(field, json, function.getReason(), defensiveListener)) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * type:String all numbers (0~9 x n).
   *
   * @author Kevin Tsai Created on 2019/01/15
   */
  private class NormalMathFunction implements DefensiveFunction {

    @Override
    public DefensiveReason getReason() {
      return DefaultReason.REASON_NOT_INT;
    }

    @Override
    public DefensiveEnum apply(Field field, String jsonKey, JSONObject json) {
      Defenses defense = field.getAnnotation(Defenses.class);
      boolean check = defense != null && defense.normalInt() && json.has(jsonKey);

      if (check) {
        String value = String.valueOf(json.get(jsonKey));
        if (!value.matches("\\d{1,}")) {
          return DefensiveEnum.FOOL;
        }
        return DefensiveEnum.NON_FOOL;
      } else {
        return DefensiveEnum.NONE;
      }
    }
  }

  /**
   * the field be tag Require.
   *
   * @author Kevin Tsai Created on 2019/01/15
   */
  private class ExistsFunction implements DefensiveFunction {

    @Override
    public DefensiveReason getReason() {
      return DefaultReason.REASON_NON_EXISTS;
    }

    @Override
    public DefensiveEnum apply(Field field, String jsonKey, JSONObject json) {
      boolean isOptional = field.isAnnotationPresent(Optional.class);

      if (!isOptional) { // required
        // dismiss field or json's value is null
        if (!json.has(jsonKey) || json.isNull(jsonKey)) {
          return DefensiveEnum.FOOL;
        }
      }
      return DefensiveEnum.NON_FOOL;
    }
  }

  /**
   * type:String exists space.
   *
   * @author Kevin Tsai Created on 2019/01/15
   */
  private class SpaceFunction implements DefensiveFunction {

    @Override
    public DefensiveReason getReason() {
      return DefaultReason.REASON_HAS_SPACE;
    }

    @Override
    public DefensiveEnum apply(Field field, String jsonKey, JSONObject json) {
      Defenses defense = field.getAnnotation(Defenses.class);
      boolean check = defense != null && defense.space() && json.has(jsonKey);

      if (check) {
        String value = String.valueOf(json.get(jsonKey));
        if (value.isEmpty() || value.indexOf(" ") != -1) {
          return DefensiveEnum.FOOL;
        }
        return DefensiveEnum.NON_FOOL;
      } else {
        return DefensiveEnum.NONE;
      }
    }
  }
}
