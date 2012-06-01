package cucumber.examples.java.parallel;

import org.junit.runner.RunWith;

import cucumber.junit.Cucumber;

@RunWith(Cucumber.class)
@Cucumber.Options(features = "classpath:personalGrowth.feature")
public class PersonalGrowth2IT {

}
