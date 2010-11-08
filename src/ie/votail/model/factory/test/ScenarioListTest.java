package ie.votail.model.factory.test;

import static org.junit.Assert.*;
import ie.votail.model.Outcome;
import ie.votail.model.Scenario;
import ie.votail.model.factory.ScenarioList;

import org.junit.Test;

public class ScenarioListTest {

  @Test
  public void testAddScenario() {
    ScenarioList scenarioList = new ScenarioList();
    Scenario scenario= new Scenario();
    scenarioList.add(scenario);
    scenarioList.add(scenario.canonical());
    assertTrue (scenarioList.contains(scenario.canonical()));
    assertTrue (scenario.equivalentTo(scenario.canonical()));
  }

  @Test
  public void testGetNumberOfScenarios() {
    ScenarioList scenarioList = new ScenarioList();
    Scenario scenario= new Scenario();
    scenario.addOutcome(Outcome.COMPROMISE_WINNER);
    scenarioList.add(scenario);
    assertEquals (1, scenarioList.getNumberOfScenarios(1));
  }

}
