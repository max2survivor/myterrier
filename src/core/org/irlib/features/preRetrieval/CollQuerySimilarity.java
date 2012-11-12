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
public class CollQuerySimilarity extends Feature {
  
  Idf computeIDF;
  MatchingQueryTerms terms;
  long numberOfDocuments;
  
  public CollQuerySimilarity(Index _index) {
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
  public Collection<? extends Double> computeValue() {
    outputFeatures.removeAllElements();
    
    HashMap<String,Integer> uniqueTerms = new HashMap<String,Integer>();
    for (String term : terms.getTerms()) {
      if (!uniqueTerms.containsKey(term)) uniqueTerms.put(term, 1);
      else uniqueTerms.put(term, uniqueTerms.get(term) + 1);
    }
    
    double[] vals = new double[uniqueTerms.size()];
    int i = -1;
    for (String term : uniqueTerms.keySet()) {
      int df = 0;
      double tf = 0;
      if (index.getLexicon().getLexiconEntry(term) != null) {
        df = index.getLexicon().getLexiconEntry(term).getDocumentFrequency();
        tf = index.getLexicon().getLexiconEntry(term).getFrequency();
        vals[++i] = (1 + Math.log(1 + tf)) * computeIDF.idfNENQUIRY(df);
      } else vals[++i] = 0;
      
    }
    
    outputFeatures.add(StaTools.sum(vals));
    outputFeatures.add(StaTools.max(vals));
    outputFeatures.add(StaTools.variance(vals));
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