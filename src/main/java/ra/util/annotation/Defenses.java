package ra.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Foolproof annotation.
 *
 * <p>normalInt - check argument value valid
 *
 * <p>type=String space - check argument value has no space
 *
 * @author Kevin Tsai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Defenses {
  /** check argument value valid ,type=String. */
  boolean normalInt() default false;

  /** check argument value has no space. */
  boolean space() default false;
}
