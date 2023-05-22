package io.forest.ddd;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.forest.ddd", importOptions = { DoNotIncludeTests.class })
public class ArchitectureTest {

//	private static final JavaClasses classes = new ClassFileImporter()
//			.withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
//			.importPackages("io.forest.ddd");

//	@ArchTest
//	public static ArchRule entityAndValueObjectVisibilityRule = classes().that()
//			.areNotAnnotatedWith(AggregateRoot.class)
//			.and()
//			.areNotAnnotatedWith(Factory.class)
//			.should()
//			.bePackagePrivate();

	@ArchTest
	public static ArchRule domainShouldOnlyBeAccessibleByApplication = classes().that()
			.resideInAPackage("..domain..")
			.should()
			.onlyBeAccessed()
			.byAnyPackage("..domain..", "..application..");

	@ArchTest
	public static ArchRule applicationShouldOnlyBeAccessibleByAdapter = noClasses().that()
			.resideInAPackage("..domain..")
			.should()
			.dependOnClassesThat()
			.resideInAnyPackage("..adapter..", "..application..", "..conf..");

	@ArchTest
	public static ArchRule valueObjectShouldBeImmutable = classes().that()
			.areAnnotatedWith(ValueObject.class)
			.should()
			.haveOnlyFinalFields();

	@ArchTest
	public ArchRule repositoriesShouldBeLocatedAdapter = classes().that()
			.areAnnotatedWith(Repository.class)
			.should()
			.resideInAPackage("..adapter..");

	@ArchTest
	public ArchRule dtoShouldEndWithDTO = classes().that()
			.resideInAPackage("..dto..")
			.should()
			.haveSimpleNameEndingWith("DTO");

	@ArchTest
	public ArchRule confShouldBeAnnotatedWithConfiguration = classes().that()
			.resideInAPackage("..conf..")
			.should()
			.beAnnotatedWith(Configuration.class);

}
