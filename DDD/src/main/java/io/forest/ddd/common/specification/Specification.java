package io.forest.ddd.common.specification;

public interface Specification<T> {

	boolean isSatisfied(T t);
}
