package test.mock.annotationclass;

import ra.util.annotation.Configuration;
import ra.util.annotation.ServerApplication;

/**
 * Fake class.
 *
 * @author Ray Li
 */
@ServerApplication(serviceMode = TestReadZipCommand.class)
@Configuration("unittest/testReadText.properties")
public class TestReadZip {}
