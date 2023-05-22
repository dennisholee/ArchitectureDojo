package io.forest.ddd;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.forest.ddd")
class DomainModelRulesTest {

	@ArchTest
	static final ArchRule controllers_should_not_have_Gui_in_name = classes().that().resideInAPackage("..model..").should()
			.haveSimpleNameNotContaining("Gui");

}
