package io.forest.ddd.common.specification;

public abstract class CompositeSpecification<T> implements Specification<T> {

	public CompositeSpecification<T> and(CompositeSpecification<T> o) {
		return new AndCompositeSpecification<T>(this, o);
	}
	
	public CompositeSpecification<T> or(CompositeSpecification<T> o) {
		return new OrCompositeSpecification<T>(this, o);
	}
}
