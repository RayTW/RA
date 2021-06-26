package ra.util.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When tag member field as Quote annotation, the field will execute escape string.
 *
 * @author Ray Li
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RequestCommand {
  /**
   * API command.
   *
   * @return default null
   */
  String value();

  /**
   * Enable cache instance of API service.
   *
   * @return default true
   */
  boolean cache() default true;
}
