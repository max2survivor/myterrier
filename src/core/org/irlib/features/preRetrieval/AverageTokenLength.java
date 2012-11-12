/**
 * 
 */
package org.irlib.features.preRetrieval;

import java.util.Collection;

import org.irlib.features.Feature;
import org.terrier.matching.MatchingQueryTerms;
import org.terrier.structures.Index;
import org.terrier.utility.StaTools;

/**
 * @author semanticpc
 * 
 */
public class AverageTokenLength extends Feature {
  
  MatchingQueryTerms terms;
  
  public AverageTokenLength(Index _index) {
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
    double[] termLengths = new double[terms.length()];
    int i = -1;
    for (String term : terms.getTerms()) {
      termLengths[++i] = term.length();
    }
    outputFeatures.add(StaTools.mean(termLengths));
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
