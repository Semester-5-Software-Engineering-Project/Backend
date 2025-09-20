package com.tutoring.Tutorverse;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Tutorverse Test Suite")
@SelectPackages({
    "com.tutoring.Tutorverse.Services",
    "com.tutoring.Tutorverse.Controller",
    "com.tutoring.Tutorverse.Dto"
})
public class TutorverseTestSuite {
    // This class remains empty. It is used only as a holder for the above annotations.
}
