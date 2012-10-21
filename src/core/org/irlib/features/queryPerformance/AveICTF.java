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
public class AveICTF extends Feature {
  
  MatchingQueryTerms terms;
  long totalNumberOfTokens;
  
  public AveICTF(Index _index) {
    super(_index);
    SPECIAL_INPUTS = false;
    totalNumberOfTokens = index.getCollectionStatistics().getNumberOfTokens();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.irlib.features.Feature#computeValue()
   */
  @Override
  public double computeValue() {
    double ctf = 1;
    for (String term : terms.getTerms()) {
      if (index.getLexicon().getLexiconEntry(term) != null) ctf *= (double) index
          .getLexicon().getLexiconEntry(term).getFrequency()
          / (double) totalNumberOfTokens;
    }
    
    return (Math.log(ctf) / Math.log(2)) / terms.length();
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
