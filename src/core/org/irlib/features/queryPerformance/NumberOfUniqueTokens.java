/**
 * 
 */
package org.irlib.features.queryPerformance;

import java.util.HashMap;

import org.irlib.features.Feature;
import org.terrier.matching.MatchingQueryTerms;
import org.terrier.structures.Index;

/**
 * @author semanticpc
 * 
 */
public class NumberOfUniqueTokens extends Feature {
  
  MatchingQueryTerms terms;
  
  public NumberOfUniqueTokens(Index _index) {
    super(_index);
    SPECIAL_INPUTS = false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.irlib.features.Feature#computeValue()
   */
  @Override
  public double computeValue() {
    HashMap<String,Integer> uniqueTerms = new HashMap<String,Integer>();
    for (String term : terms.getTerms()) {
      if (!uniqueTerms.containsKey(term)) uniqueTerms.put(term, 1);
      else uniqueTerms.put(term, uniqueTerms.get(term) + 1);
    }
    return uniqueTerms.size();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.irlib.features.Feature#setUp(int[],
   * org.terrier.matching.MatchingQueryTerms)
   */
  @Override
  public void setUp(int[] _docids, MatchingQueryTerms _terms) {
    terms = _terms;
  }
  
}
