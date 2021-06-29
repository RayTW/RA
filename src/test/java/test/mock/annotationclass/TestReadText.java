package test.mock.annotationclass;

import ra.util.annotation.Configuration;
import ra.util.annotation.ServerApplication;

/** Fake class. */
@ServerApplication(serviceMode = TestReadTextApplication.class)
@Configuration("unittest/testReadText.properties")
public class TestReadText {}
