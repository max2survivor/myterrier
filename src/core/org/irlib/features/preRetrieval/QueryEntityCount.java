/**
 * 
 */
package org.irlib.features.preRetrieval;

import java.util.Collection;

import org.irlib.features.Feature;
import org.terrier.matching.MatchingQueryTerms;
import org.terrier.structures.Index;

/**
 * @author semanticpc
 * 
 */
public class QueryEntityCount extends Feature {
  
  /**
   * @param _index
   */
  public QueryEntityCount(Index _index) {
    super(_index);
    // TODO Auto-generated constructor stub
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.irlib.features.Feature#computeValue()
   */
  @Override
  public Collection<? extends Double> computeValue() {
    // TODO Auto-generated method stub
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.irlib.features.Feature#setUp(int[],
   * org.terrier.matching.MatchingQueryTerms)
   */
  @Override
  public void setUp(int[] _docids, MatchingQueryTerms _terms) {
    // TODO Auto-generated method stub
    
  }
  
}
