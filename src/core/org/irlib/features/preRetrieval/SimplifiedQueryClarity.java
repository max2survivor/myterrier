/**
 * 
 */
package org.irlib.features.preRetrieval;

import java.util.Collection;
import java.util.HashMap;

import org.irlib.features.Feature;
import org.terrier.matching.MatchingQueryTerms;
import org.terrier.matching.models.Idf;
import org.terrier.structures.Index;
import org.terrier.utility.StaTools;

/**
 * @author semanticpc
 * 
 */
public class SimplifiedQueryClarity extends Feature {
  
  Idf computeIDF;
  long totalNumberOfTokens;
  MatchingQueryTerms terms;
  
  public SimplifiedQueryClarity(Index _index) {
    super(_index);
    SPECIAL_INPUTS = false;
    totalNumberOfTokens = index.getCollectionStatistics().getNumberOfTokens();
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
    
    int queryLength = terms.length();
    HashMap<String,Integer> uniqueTerms = new HashMap<String,Integer>();
    for (String term : terms.getTerms()) {
      if (!uniqueTerms.containsKey(term)) uniqueTerms.put(term, 1);
      else uniqueTerms.put(term, uniqueTerms.get(term) + 1);
    }
    
    double[] vals = new double[uniqueTerms.size()];
    int i = -1;
    for (String term : uniqueTerms.keySet()) {
      double Pml = (double) uniqueTerms.get(term) / (double) queryLength;
      double Pcoll = 0;
      if (index.getLexicon().getLexiconEntry(term) != null) {
        Pcoll = (double) index.getLexicon().getLexiconEntry(term)
            .getFrequency()
            / (double) totalNumberOfTokens;
        vals[++i] = Pml * (Math.log(Pml / Pcoll) / Math.log(2));
      } else vals[++i] = 0;
      
    }
    
    outputFeatures.add(StaTools.sum(vals));
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