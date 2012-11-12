/**
 * 
 */
package org.irlib.features.preRetrieval;

import java.util.Collection;

import org.irlib.features.Feature;
import org.terrier.matching.MatchingQueryTerms;
import org.terrier.matching.models.Idf;
import org.terrier.structures.Index;
import org.terrier.utility.StaTools;

/**
 * @author semanticpc
 * 
 */
public class AveIDF extends Feature {
  
  Idf computeIDF;
  MatchingQueryTerms terms;
  
  public AveIDF(Index _index) {
    super(_index);
    SPECIAL_INPUTS = false;
    computeIDF = new Idf(index.getCollectionStatistics().getNumberOfDocuments());
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.irlib.features.Feature#computeValue()
   */
  @Override
  public Collection<? extends Double> computeValue() {
    outputFeatures.removeAllElements();
    double[] idfVals = new double[terms.length()];
    int i = -1;
    for (String term : terms.getTerms()) {
      if (index.getLexicon().getLexiconEntry(term) != null) {
        int df = index.getLexicon().getLexiconEntry(term)
            .getDocumentFrequency();
        idfVals[++i] = computeIDF.idfNENQUIRY(df);
        
      } else idfVals[++i] = 0;
    }
    outputFeatures.add(StaTools.mean(idfVals));
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