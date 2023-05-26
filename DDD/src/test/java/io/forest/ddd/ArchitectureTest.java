package io.forest.ddd;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.constructors;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import io.forest.ddd.common.domain.event.DomainEvent;
import io.forest.ddd.common.specification.Specification;

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

//	@ArchTest
//	public ArchRule dtoShouldEndWithDTO = classes().that()
//			.resideInAPackage("..dto..")
//			.should()
//			.haveSimpleNameEndingWith("DTO");

	@ArchTest
	public ArchRule confShouldBeAnnotatedWithConfiguration = classes().that()
			.resideInAPackage("..conf..")
			.should()
			.beAnnotatedWith(Configuration.class);

	@ArchTest
	public ArchRule constructorsShouldBeProtectedInAggregates = constructors().that()
			.areDeclaredInClassesThat()
			.areAnnotatedWith(AggregateRoot.class)
			.should()
			.haveModifier(JavaModifier.PROTECTED);

	@ArchTest
	public ArchRule specShouldBeSuffixedWithSpec = classes().that()
			.implement(Specification.class)
			.and()
			.resideInAPackage("..domain..")
			.should()
			.haveSimpleNameEndingWith("Spec");

	@ArchTest
	public ArchRule eventShouldBeSuffixedWithEvent = classes().that()
			.implement(DomainEvent.class)
			.and()
			.resideInAPackage("..domain..")
			.should()
			.haveSimpleNameEndingWith("Event");

//	@ArchTest
//	public ArchRule eventShouldBeAnnotatedWithDomainEvent = classes().that()
//			.resideInAPackage("..event..")
//			.should()
//			.beAnnotatedWith(DomainEvent.class);

}
