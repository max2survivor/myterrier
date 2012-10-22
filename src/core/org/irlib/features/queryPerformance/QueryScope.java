/**
 * 
 */
package org.irlib.features.queryPerformance;

import org.irlib.features.Feature;
import org.terrier.matching.MatchingQueryTerms;
import org.terrier.matching.models.Idf;
import org.terrier.structures.Index;
import org.terrier.utility.StaTools;

/**
 * @author semanticpc
 * 
 */
public class QueryScope extends Feature {
  
  Idf computeIDF;
  MatchingQueryTerms terms;
  long numberOfDocuments;
  
  public QueryScope(Index _index) {
    super(_index);
    SPECIAL_INPUTS = false;
    numberOfDocuments = index.getCollectionStatistics().getNumberOfDocuments();
    computeIDF = new Idf(index.getCollectionStatistics().getNumberOfDocuments());
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.irlib.features.Feature#computeValue()
   */
  @Override
  public double computeValue() {
    
    double[] idfVals = new double[terms.length()];
    int i = -1;
    for (String term : terms.getTerms()) {
      if (index.getLexicon().getLexiconEntry(term) != null) {
        double df = index.getLexicon().getLexiconEntry(term)
            .getDocumentFrequency();
        idfVals[++i] = computeIDF.idfENQUIRY(df);
        
      } else idfVals[++i] = 0;
    }
    return (Math.log(StaTools.min(idfVals) / numberOfDocuments) * -1);
    
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