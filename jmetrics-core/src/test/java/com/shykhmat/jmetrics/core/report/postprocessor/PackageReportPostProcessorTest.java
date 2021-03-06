package com.shykhmat.jmetrics.core.report.postprocessor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.shykhmat.jmetrics.core.report.ClassReport;
import com.shykhmat.jmetrics.core.report.Metrics;
import com.shykhmat.jmetrics.core.report.PackageReport;
import com.shykhmat.jmetrics.core.report.ProjectReport;

public class PackageReportPostProcessorTest {
	private PackageReportPostProcessor packageReportPostProcessor = new PackageReportPostProcessor();

	@Test
	public void testProcessing() {
		ClassReport a = new ClassReport("com.shykhmat.A");
		a.setEfferentCouplingUsed(new HashSet<>(Arrays.asList("com.shykhmat.B", "default.C")));
		a.setIsAbstractOrInterface(true);
		Metrics aMetrics = new Metrics();
		aMetrics.setCyclomaticComplexity(1);
		aMetrics.setLinesOfCode(10);
		a.setMetrics(aMetrics);
		ClassReport b = new ClassReport("com.shykhmat.B");
		b.setEfferentCouplingUsed(new HashSet<>(Arrays.asList("default.C")));
		b.setAfferentCouplingUsed(new HashSet<>(Arrays.asList("com.shykhmat.A")));
		Metrics bMetrics = new Metrics();
		bMetrics.setCyclomaticComplexity(1);
		bMetrics.setLinesOfCode(10);
		b.setMetrics(bMetrics);
		ClassReport c = new ClassReport("default.C");
		c.setAfferentCouplingUsed(new HashSet<>(Arrays.asList("com.shykhmat.A", "com.shykhmat.B")));
		Metrics cMetrics = new Metrics();
		cMetrics.setCyclomaticComplexity(1);
		cMetrics.setLinesOfCode(10);
		c.setMetrics(cMetrics);
		ClassReport d = new ClassReport("com.shykhmat.D");
		d.setIsAbstractOrInterface(true);
		d.setAfferentCouplingUsed(new HashSet<>(Arrays.asList("com.shykhmat.E")));
		d.setEfferentCouplingUsed(new HashSet<>(Arrays.asList("com.shykhmat.all.All")));
		Metrics dMetrics = new Metrics();
		dMetrics.setCyclomaticComplexity(1);
		dMetrics.setLinesOfCode(10);
		d.setMetrics(dMetrics);
		ClassReport e = new ClassReport("com.shykhmat.E");
		e.setEfferentCouplingUsed(new HashSet<>(Arrays.asList("com.shykhmat.all.All", "Unknown", "com.shykhmat.D")));
		Metrics eMetrics = new Metrics();
		eMetrics.setCyclomaticComplexity(1);
		eMetrics.setLinesOfCode(10);
		e.setMetrics(eMetrics);
		ProjectReport projectReport = new ProjectReport();
		projectReport.setClasses(new HashSet<>(Arrays.asList(a, b, c, d, e)));
		packageReportPostProcessor.process(projectReport);
		PackageReport expectedShykhmatPackage = new PackageReport("com.shykhmat");
		expectedShykhmatPackage.setEfferentCouplingUsed(new HashSet<>(Arrays.asList("default", "com.shykhmat.all")));
		expectedShykhmatPackage.setAbstractClassesInterfacesNumber(2);
		expectedShykhmatPackage.setNonAbstractClassesNumber(2);
		expectedShykhmatPackage.setAbstractness(0.5);
		expectedShykhmatPackage.setInstability(1.);
		expectedShykhmatPackage.setDistance(0.5);
		Metrics expectedShykhmatPackageMetrics = new Metrics();
		expectedShykhmatPackageMetrics.setCyclomaticComplexity(1);
		expectedShykhmatPackageMetrics.setLinesOfCode(40);
		expectedShykhmatPackage.setMetrics(expectedShykhmatPackageMetrics);
		PackageReport expectedDefaultPackage = new PackageReport("default");
		expectedDefaultPackage.setAfferentCouplingUsed(new HashSet<>(Arrays.asList("com.shykhmat")));
		expectedDefaultPackage.setAbstractClassesInterfacesNumber(0);
		expectedDefaultPackage.setNonAbstractClassesNumber(1);
		expectedDefaultPackage.setAbstractness(0.);
		expectedDefaultPackage.setInstability(0.);
		expectedDefaultPackage.setDistance(1.);
		Metrics expectedDefaultPackageMetrics = new Metrics();
		expectedDefaultPackageMetrics.setCyclomaticComplexity(1);
		expectedDefaultPackageMetrics.setLinesOfCode(10);
		expectedDefaultPackage.setMetrics(expectedDefaultPackageMetrics);
		Set<PackageReport> expectedData = new HashSet<>(Arrays.asList(expectedShykhmatPackage, expectedDefaultPackage));
		assertThat(projectReport.getPackages().size(), is(expectedData.size()));
		expectedData.forEach(expectedPackageReport -> {
			PackageReport actualPackageReport = projectReport.getPackages().stream()
					.filter(packageReport -> expectedPackageReport.getName().equals(packageReport.getName()))
					.findFirst().orElse(null);
			assertNotNull(actualPackageReport);
			Set<String> actualEfferentCouplingUsed = actualPackageReport.getEfferentCouplingUsed();
			assertThat(actualEfferentCouplingUsed.size(), is(expectedPackageReport.getEfferentCouplingUsed().size()));
			assertThat(actualEfferentCouplingUsed, is(expectedPackageReport.getEfferentCouplingUsed()));
			Set<String> actualAfferentCouplingUsed = actualPackageReport.getAfferentCouplingUsed();
			assertThat(actualAfferentCouplingUsed.size(), is(expectedPackageReport.getAfferentCouplingUsed().size()));
			assertThat(actualAfferentCouplingUsed, is(expectedPackageReport.getAfferentCouplingUsed()));
			assertThat(actualPackageReport.getInstability(), is(expectedPackageReport.getInstability()));
			assertThat(actualPackageReport.getAbstractClassesInterfacesNumber(),
					is(expectedPackageReport.getAbstractClassesInterfacesNumber()));
			assertThat(actualPackageReport.getNonAbstractClassesNumber(),
					is(expectedPackageReport.getNonAbstractClassesNumber()));
			assertThat(actualPackageReport.getAbstractness(), is(expectedPackageReport.getAbstractness()));
			assertThat(actualPackageReport.getDistance(), is(expectedPackageReport.getDistance()));
			Metrics actualMetrics = actualPackageReport.getMetrics();
			assertNotNull(actualMetrics);
			assertThat(actualMetrics.getCyclomaticComplexity(),
					is(expectedPackageReport.getMetrics().getCyclomaticComplexity()));
			assertThat(actualMetrics.getLinesOfCode(), is(expectedPackageReport.getMetrics().getLinesOfCode()));
		});
	}
}
