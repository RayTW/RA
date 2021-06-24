package ra.util.defensive;

import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import org.json.JSONObject;
import ra.util.TernaryFunction;

/** Defensive Function's interface. */
public interface DefensiveFunction
    extends TernaryFunction<Field, String, JSONObject, DefensiveEnum> {

  /**
   * the default method to defensive.
   *
   * @param field the field which needs to checked.
   * @param json the json container exists all the values.
   * @param reason the object defined the reason.
   * @param defensiveListener listener the reason.
   * @return all pass：true,exists error：false
   */
  default boolean defensive(
      Field field,
      JSONObject json,
      DefensiveReason reason,
      BiConsumer<String, DefensiveReason> defensiveListener) {
    SerializedName serializeObj = field.getAnnotation(SerializedName.class);
    String serializedName = field.getName();
    if (serializeObj != null) {
      serializedName = serializeObj.value();
    }

    if (apply(field, serializedName, json) == DefensiveEnum.FOOL) {
      defensiveListener.accept(serializedName, reason);
      return false;
    }
    return true;
  }

  DefensiveReason getReason();
}
