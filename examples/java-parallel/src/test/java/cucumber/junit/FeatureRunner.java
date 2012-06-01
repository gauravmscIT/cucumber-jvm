package cucumber.junit;

import static cucumber.junit.DescriptionFactory.createDescription;
import gherkin.formatter.model.Feature;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import cucumber.runtime.CucumberException;
import cucumber.runtime.Runtime;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberScenario;
import cucumber.runtime.model.CucumberScenarioOutline;
import cucumber.runtime.model.CucumberTagStatement;

class FeatureRunner extends ParentRunner<ParentRunner> {
	private final List<ParentRunner> children = new ArrayList<ParentRunner>();

	private final CucumberFeature cucumberFeature;
	private final Runtime runtime;
	private final JUnitReporter jUnitReporter;
	private Description description;

	protected FeatureRunner(CucumberFeature cucumberFeature, Runtime runtime, JUnitReporter jUnitReporter) throws InitializationError {
		super(null);
		this.cucumberFeature = cucumberFeature;
		this.runtime = runtime;
		this.jUnitReporter = jUnitReporter;
		buildFeatureElementRunners();
	}

	@Override
	public String getName() {
		Feature feature = cucumberFeature.getFeature();
		return feature.getKeyword() + ": " + feature.getName();
	}

	@Override
	public Description getDescription() {
		if (description == null) {
			description = createDescription(getName(), cucumberFeature);
			for (ParentRunner child : getChildren()) {
				description.addChild(describeChild(getName(), (FeatureRunner) child));
			}
		}
		return description;
	}

	@Override
	protected List<ParentRunner> getChildren() {
		return children;
	}

	@Override
	public Description describeChild(String clazz, ParentRunner child) {
		Description desc = child.getDescription();
		desc.setClazz(clazz);
		for (Description descChild : desc.getChildren()) {
			descChild.setClazz(clazz);
		}
		return desc;
	}

	public Description getDescription(String clazz) {
		if (description == null) {
			description = createDescription(clazz, getName(), cucumberFeature);
			for (ParentRunner child : getChildren()) {
				description.addChild(describeChild(clazz, child));
			}
		}
		return description;
	}

	@Override
	protected Description describeChild(ParentRunner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(ParentRunner child, RunNotifier notifier) {
		child.run(notifier);
	}

	@Override
	public void run(RunNotifier notifier) {
		jUnitReporter.uri(cucumberFeature.getUri());
		jUnitReporter.feature(cucumberFeature.getFeature());
		super.run(notifier);
		jUnitReporter.eof();
	}

	private void buildFeatureElementRunners() {
		for (CucumberTagStatement cucumberTagStatement : cucumberFeature.getFeatureElements()) {
			try {
				ParentRunner featureElementRunner;
				if (cucumberTagStatement instanceof CucumberScenario) {
					featureElementRunner = new ExecutionUnitRunner(runtime, (CucumberScenario) cucumberTagStatement, jUnitReporter);
				} else {
					featureElementRunner = new ScenarioOutlineRunner(runtime, (CucumberScenarioOutline) cucumberTagStatement, jUnitReporter);
				}
				children.add(featureElementRunner);
			} catch (InitializationError e) {
				throw new CucumberException("Failed to create scenario runner", e);
			}
		}
	}

}
