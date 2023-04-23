package io.forest.ddd.specification;


public class SpecificationPattern {
	public static void main(String args[]) {
		
		CompositeSpecification<Student> highSchoolSpec = new CompositeSpecification<Student>() {

			@Override
			public boolean isSatified(Student o) {
				return o.isHighSchool();
			}
		};
		
		CompositeSpecification<Student> artStudentSpec = new CompositeSpecification<Student>() {

			@Override
			public boolean isSatified(Student o) {
				return o.isArtStudent();
			}
		};
		
		Student highSchoolArtStudent = new Student(true, true);
		Student hightSchoolNotArtStudent = new Student(true, false);
		Student notHightSchoolArtStudent = new Student(false, true);
		Student notHighSchoolNotArtStudent = new Student(false, false);
		
		
		CompositeSpecification<Student> highSchoolStudentSpec = highSchoolSpec.and(artStudentSpec);
		
		System.out.println("highSchoolArtStudent " + highSchoolStudentSpec.isSatified(highSchoolArtStudent));
		
		System.out.println("hightSchoolNotArtStudent " + highSchoolStudentSpec.isSatified(hightSchoolNotArtStudent));
		
		System.out.println("notHightSchoolArtStudent " + highSchoolStudentSpec.isSatified(notHightSchoolArtStudent));
		
		System.out.println("notHighSchoolNotArtStudent " + highSchoolStudentSpec.isSatified(notHighSchoolNotArtStudent));
	}
}

class Student {
	
	private boolean isArtStudent;
	private boolean isHighSchoolStudent;

	public Student(boolean isHighSchoolStudent, boolean isArtStudent) {
		this.isHighSchoolStudent = isHighSchoolStudent;
		this.isArtStudent = isArtStudent;
	}

	public boolean isHighSchool() {
		return this.isHighSchoolStudent;
	}

	public boolean isArtStudent() {
		return this.isArtStudent;
	}
}


interface Specification <T> {
	abstract boolean isSatified(T o);
}

abstract class CompositeSpecification<T> implements Specification<T> {
	
	public CompositeSpecification<T> and(CompositeSpecification<T> o) {
		return new AndCompositeSpecification<T>(this, o);
	}
}

class AndCompositeSpecification<T> extends CompositeSpecification<T> {
	
	private CompositeSpecification<T> leftOperand;
	
	private CompositeSpecification<T> rightOperand;
	
	public AndCompositeSpecification(CompositeSpecification<T> leftOperand, CompositeSpecification<T> rightOperand) {
		setLeftOperand(leftOperand);
		setRightOperand(rightOperand);
	}

	void setLeftOperand(CompositeSpecification<T> leftOperand) {
		this.leftOperand = leftOperand;
	}
	
	void setRightOperand(CompositeSpecification<T> rightOperand) {
		this.rightOperand = rightOperand;
	}
	
	@Override
	public boolean isSatified(T o) {
		return this.leftOperand.isSatified(o) && this.rightOperand.isSatified(o);
	}	
}
