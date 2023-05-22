package io.forest.ddd.common.specification;

public class OrCompositeSpecification<T> extends CompositeSpecification<T> {

	CompositeSpecification<T> leftOperand;
	CompositeSpecification<T> rightOperand;
	
	public OrCompositeSpecification(CompositeSpecification<T> leftOperand, CompositeSpecification<T> rightOperand) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	@Override
	public boolean isSatisfied(T o) {
		return this.leftOperand.isSatisfied(o) || this.rightOperand.isSatisfied(o);
	}
}
