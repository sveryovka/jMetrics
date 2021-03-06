package com.shykhmat.jmetrics.core.report;

/**
 * Base class for all parts of code, e.g. for Class, Method, etc.
 */
public abstract class CodePartReport implements Comparable<CodePartReport> {
	private String name;
	private Metrics metrics;

	public CodePartReport(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Metrics getMetrics() {
		return metrics;
	}

	public void setMetrics(Metrics metrics) {
		this.metrics = metrics;
	}

	@Override
	public int compareTo(CodePartReport codeToCompare) {
		int result = metrics.getMaintainabilityIndex().compareTo(codeToCompare.getMetrics().getMaintainabilityIndex());
		if (result == 0) {
			return getName().compareTo(codeToCompare.getName());
		}
		return result;
	}

	/**
	 * All supported code parts.
	 */
	public static enum CodePartType {
		NONE, CLASS, INTERFACE, ENUM, METHOD, ANNOTATION, CONSTRUCTOR;
	}
}
