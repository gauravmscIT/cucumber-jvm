package cucumber.examples.java.parallel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;

public class PersonalGrowthSteps {

	private Person person;

	@Given("^a person of (\\d+) feet$")
	public void thePersonExists(double height) {
		System.out.println("given");
		person = new Person(height);
		fail();
	}

	@When("^the person grows by (\\d+) feet$")
	public void thePersonGrowsBy(double extraHeight) {
		System.out.println("when");
		person.growBy(extraHeight);
		fail();
	}

	@Then("^the person will be (\\d+) feet tall$")
	public void thePersonWillBe(double expectedHeight) {
		System.out.println("then");
		assertEquals(person.getHeight(), expectedHeight, 0.0);
		fail();
	}
}
