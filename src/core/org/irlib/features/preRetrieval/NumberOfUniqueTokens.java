/**
 * 
 */
package org.irlib.features.preRetrieval;

import java.util.Collection;
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
  public Collection<? extends Double> computeValue() {
    outputFeatures.removeAllElements();
    HashMap<String,Integer> uniqueTerms = new HashMap<String,Integer>();
    for (String term : terms.getTerms()) {
      if (!uniqueTerms.containsKey(term)) uniqueTerms.put(term, 1);
      else uniqueTerms.put(term, uniqueTerms.get(term) + 1);
    }
    outputFeatures.add((double) uniqueTerms.size());
    return outputFeatures;
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
