/**
 * Dermot Cochran, 2010, IT University of Copenhagen
 */

package ie.votail.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A model election scenario is a set of possible outcomes for each candidate. 
 * Each branch in the counting algorithm is associated with at least one
 * such scenario. So, testing all scenarios should achieve full path coverage
 * of the counting system.
 */

public class Scenario {
  
  // Invariants for Scenarios based on Axioms in the Alloy model
  
  public static final boolean AXIOM_validTieBreaker = true;
  /*@
   * public invariant AXIOM_validTieBreaker ==>
   *   (\forall Outcome tl; outcomes.contains(tl) &&
   *   (tl == Outcomes.TiedLoser || tl == outcomes.TiedEarlyLoser || 
   *   tl == outcomes.tiedSoreLoser);
   *   (\exists Outcome tw; outcomes.contains(tw) && tw == Outcomes.TIED_WINNER));
   */
  
  public static final boolean AXIOM_tieBreaker = true;
  /*@ 
   * public invariant AXIOM_tieBreaker ==> 
   *   (\forall Outcome tw; outcomes.contains(tw) && tw == Outcomes.TIED_WINNER;
   *   (\exists Outcome tl; outcomes.contains(tl) &&
   *   (tl == Outcomes.TiedLoser || tl == outcomes.TiedEarlyLoser || 
   *   tl == outcomes.tiedSoreLoser)));
   */
  
  public static final boolean AXIOM_typeOfTiedLoser = true;
  /*@
   * public invariant AXIOM_typeOfTiedLoser ==>
   *  (\forall Outcome a, b; outcomes.contains (a) && outcomes.contains (b);
   *  (a == Outcome.TiedSoreLoser) ==> 
   *    (b != Outcome.TiedLoser && b != Outcome.TiedEarlyLoser && 
   *     b != Outcome.Loser && b != Outcome.EarlyLoser));
   */
  
  /*@
   * public invariant 1 < outcomes.size();
   */
  private /*@ non_null*/ ArrayList<Outcome> outcomes;

  /**
   * Create a new model scenario.
   */
  public Scenario () {
    outcomes = new ArrayList<Outcome>();
  }
  
  /**
   * Textual representation of a model election scenario.
   */
  public /*@ pure*/ String toString() {
    Iterator<Outcome> iterator = outcomes.iterator();
    StringBuffer stringBuffer = new StringBuffer ("Scenario: (");
    if (iterator.hasNext()) {
      stringBuffer.append(iterator.next().toString());
    }
    while (iterator.hasNext()) {
      stringBuffer.append(",");
      stringBuffer.append(iterator.next().toString());
    }
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
   
  /**
   * Add a candidate outcome to this scenario.
   * 
   * @param outcome The candidate outcome to be added to this scenario
   */
  /*@
   * ensures 1 + \old(numberOfOutcomes) == numberOfOutcomes;
   * ensures outcomes.contains(outcome);
   */
  public void addOutcome(/*@ non_null */ Outcome outcome) {
    outcomes.add(outcome);
  }
  
  /**
   * Create an <a href="http://alloy.mit.edu">Alloy</a> predicate expression 
   * for this scenario, for example:
   * <p> 
   *   some disj c0,c1,c2,c3,c4,c5,c6,c7,c8: Candidate |
   *     <li>c0.outcome = Winner and</li>
   *     <li>c1.outcome = QuotaWinner and</li>
   *     <li>c2.outcome = CompromiseWinner and</li>
   *     <li>c3.outcome = Loser and</li>
   *     <li>c4.outcome = EarlyLoser and</li>
   *     <li>c5.outcome = SoreLoser and</li>
   *     <li>c6.outcome = TiedWinner and</li>
   *     <li>c7.outcome = TiedLoser and</li>
   *     <li>c8.outcome = TiedEarlyLoser</li>
   * </p>
   * @return The <code>Alloy</code> predicate as a string
   */
  /*@
   * requires 0 < outcomes.size();
   */
  public /*@ pure*/ String toPredicate() {
    Iterator<Outcome> iterator = outcomes.iterator();
    StringBuffer predicateStringBuffer = new StringBuffer("some disj ");
    for (int i=0; i < outcomes.size(); i++) {
      if (i > 0 ) {
        predicateStringBuffer.append(","); 
      }
      predicateStringBuffer.append("c" + i);
    }
    predicateStringBuffer.append(": Candidate | ");
    int i=0;
    while (iterator.hasNext()) {
      if (i > 0 ) {
        predicateStringBuffer.append(" and "); 
      }
      predicateStringBuffer.append("c" + i + ".outcome = " + iterator.next().toString());
      i++;
    }
    return predicateStringBuffer.toString();
  }

  /**
   * Sort the candidate outcomes events into a canonical order
   *
   * @return The equivalent scenario with the candidate outcomes in canonical order
   */
  /*@
   * ensures this.outcomes.size() == \result.outcomes.size();
   */
  public Scenario canonical () {
    Scenario sorted = new Scenario();
    // Extract each type of outcome in canonical order
    for (Outcome outcome : Outcome.values()) {
      Iterator<Outcome> iterator = this.outcomes.iterator();
      while (iterator.hasNext()) {
        if (outcome.equals(iterator.next())) {
          sorted.addOutcome(outcome);
        }
      }
    }
    return sorted;
  }
  
  /**
   * Compare two scenarios for the same multiset of outcomes
   * 
   * @param other The other scenario
   * @return Two scenarios are equivalent of they contain the same quantity of
   * each kind of outcome
   */
  /*@
   * ensures \result <==> this.canonical().equals(other.canonical());
   */
  public /*@ pure*/ boolean equivalentTo (/*@ non_null*/ Scenario other) {
    if (this.outcomes.size() != other.outcomes.size()) {
      return false;
    }
    Iterator<Outcome> it1 = this.canonical().outcomes.iterator();
    Iterator<Outcome> it2 = other.canonical().outcomes.iterator();
    while (it1.hasNext() && it2.hasNext()) {
      if (!it1.next().equals(it2.next())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Append an outcome to this scenario.
   * 
   * @param outcome The outcome to be appened
   * @return The scenario with the outcome appended
   */
  /*@
   * ensures 1 + this.size() == \result.size();
   * ensures \result.contains (outcome);
   * ensures \result.equivalentTo (this.addOutcome(outcome));
   */
  public /*@ pure*/ Scenario append(/*@ non_null*/ Outcome outcome) {
    Scenario result = this.copy();
    result.addOutcome(outcome);
    return result;
  }

  /**
   * Create a scenario with an equivalent set of outcomes
   * 
   * @return An equivalent scenario
   */
  /*@
   * ensures \result.equals(this);
   */
  private /*@ pure*/ Scenario copy() {
    Scenario clone = new Scenario();
    Iterator<Outcome> iterator = this.outcomes.iterator();
    while (iterator.hasNext()) {
      clone.addOutcome(iterator.next());
    }
    return clone;
  }

  /**
   * Check if this scenario involves a tie between equal candidates
   * 
   * @return True if any tied outcomes exist
   */
  public /*@ pure*/ boolean isTied() {
    Iterator<Outcome> iterator = this.outcomes.iterator();
    while (iterator.hasNext()) {
      Outcome outcome = iterator.next();
      if (outcome.isTied()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if this scenario involves a tie between a sore loser and another
   * equal candidate
   * 
   * @return True if there are any tied sore losers in this scenario
   */
  /*@
   * ensures \result ==> isTied();
   */
  public /*@ pure*/ boolean hasTiedSoreLoser() {
    Iterator<Outcome> iterator = this.outcomes.iterator();
    while (iterator.hasNext()) {
      Outcome outcome = iterator.next();
      if (outcome == Outcome.TiedSoreLoser) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Count the number of winners in this scenario.
   * <p>
   * There is at least one winner and one loser in each valid scenario
   * </p>
   * 
   * @return The number of winners
   */
  /*@
   * ensures 0 < \result;
   * ensures \result < this.outcomes.size();
   */
  public int numberOfWinners() {
   int count = 0;
   Iterator<Outcome> iterator = this.outcomes.iterator();
   while (iterator.hasNext()) {
     Outcome outcome = iterator.next();
     if (outcome == Outcome.CompromiseWinner || 
         outcome == Outcome.TiedWinner ||
         outcome == Outcome.QuotaWinner ||
         outcome == Outcome.Winner) {
       count++;
     }
   }
   return count; 
  }
  
  /**
   * Using the formulae in the ICSE 2011 paper calculate the maximum number
   * of distinct scenarios when the number of winners and losers is known,
   * adjusted by restricting invalid combinations of tied outcomes
   * 
   * @param winners The number of winners
   * @param losers The number of losers
   * @return The number of distinct scenarios
   */
  /*@
   * requires 0 < winners;
   * requires 0 < losers;
   * ensures 4 <= \result;
   */
  public static int numberOfScenarios (int winners, int losers) {
    if (winners == 1) {
      if (losers == 1) {
        return 4;
      }
      // Although there are six kinds of loser outcome, a Tied Sore Loser can
      // only be combined with an existing Tied Sore Loser
      return 5 * numberOfScenarios (winners, losers-1);
    }
    return 4 * numberOfScenarios (winners-1, losers);
  }
  
  /**
   * Estimate the number of distinct scenarios when the number of candidates is
   * known.
   * 
   * @param numberOfOutcomes The number of candidate outcomes
   * @return The total number of distinct outcomes
   */
  /*@
   * requires 1 < numberOfOutcomes
   * ensures 4 <= \result
   */
  public static int totalNumberOfScenarios (int numberOfOutcomes) {
    int result = 0;
    for (int numberOfWinners = 1; numberOfWinners < numberOfOutcomes; 
      numberOfWinners++) {
        result += numberOfScenarios (numberOfWinners, numberOfOutcomes - numberOfWinners);
    }
    return result;
  }
}
