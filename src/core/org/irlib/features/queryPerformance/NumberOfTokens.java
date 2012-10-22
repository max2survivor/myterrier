/**
 * 
 */
package org.irlib.features.queryPerformance;

import org.irlib.features.Feature;
import org.terrier.matching.MatchingQueryTerms;
import org.terrier.structures.Index;

/**
 * @author semanticpc
 * 
 */
public class NumberOfTokens extends Feature {
  
  MatchingQueryTerms terms;
  
  public NumberOfTokens(Index _index) {
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
    return terms.length();
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
