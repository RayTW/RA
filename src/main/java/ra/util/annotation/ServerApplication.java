package ra.util.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ra.net.processor.CommandProcessorProvider;

/**
 * When tag member field as Quote annotation, the field will execute escape string.
 *
 * @author Ray Li
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ServerApplication {
  /** Each command handler of the service. */
  Class<? extends CommandProcessorProvider<?>> serviceMode();
}
