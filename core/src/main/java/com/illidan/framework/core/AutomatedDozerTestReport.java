package com.illidan.framework.core;

import java.util.ArrayList;
import java.util.List;

public class AutomatedDozerTestReport<T, P> {

	private Class<T> sourceClass;
	private Class<P> destClass;

	private T sourceInstance;
	private P destInstance;

	private List<String> unmappedSourceAttributes = new ArrayList<String>();

	private List<AttributePair> mappingFailures = new ArrayList<AttributePair>();

	public AutomatedDozerTestReport(Class<T> sourceClass, Class<P> destClass) {
		this.sourceClass = sourceClass;
		this.destClass = destClass;
	}

	public Class<T> getSourceClass() {
		return sourceClass;
	}

	public void setSourceClass(Class<T> sourceClass) {
		this.sourceClass = sourceClass;
	}

	public Class<P> getDestClass() {
		return destClass;
	}

	public void setDestClass(Class<P> destClass) {
		this.destClass = destClass;
	}

	public T getSourceInstance() {
		return sourceInstance;
	}

	public void setSourceInstance(T sourceInstance) {
		this.sourceInstance = sourceInstance;
	}

	public P getDestInstance() {
		return destInstance;
	}

	public void setDestInstance(P destInstance) {
		this.destInstance = destInstance;
	}

	public List<String> getUnmappedSourceAttributes() {
		if (unmappedSourceAttributes == null) {
			this.unmappedSourceAttributes = new ArrayList<String>();
		}
		return unmappedSourceAttributes;
	}

	public void setUnmappedSourceAttributes(List<String> unmappedSourceAttributes) {
		this.unmappedSourceAttributes = unmappedSourceAttributes;
	}

	public List<AttributePair> getMappingFailures() {
		if (this.mappingFailures == null) {
			this.mappingFailures = new ArrayList<AttributePair>();
		}
		return mappingFailures;
	}

	public void setMappingFailures(List<AttributePair> mappingFailures) {
		this.mappingFailures = mappingFailures;
	}

	@Override
	public String toString() {
		StringBuilder report = new StringBuilder();
		report.append("\n");

		report.append("Source:");
		report.append(sourceClass);
		report.append("\n");

		report.append("Destination:");
		report.append(destClass);
		report.append("\n");

		report.append("Unmapped Source Attributes:");
		report.append(unmappedSourceAttributes);
		report.append("\n");
		report.append("Mapping Failures: ");
		report.append(mappingFailures);
		report.append("\n");

		report.append("Source Instance:");
		report.append(sourceInstance);
		report.append("\n");

		report.append("Destination Instance:");
		report.append(destInstance);
		report.append("\n");

		return report.toString();
	}

}
