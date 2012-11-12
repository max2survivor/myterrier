/**
 * 
 */
package org.irlib.features.preRetrieval;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.irlib.features.Feature;
import org.terrier.matching.MatchingQueryTerms;
import org.terrier.matching.models.Idf;
import org.terrier.structures.Index;
import org.terrier.structures.LexiconEntry;

import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

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
  public Collection<? extends Double> computeValue() {
    outputFeatures.removeAllElements();
    boolean first = true;
    Set<Integer> unionSet = null;
    for (String term : terms.getTerms()) {
      if (index.getLexicon().getLexiconEntry(term) != null) {
        LexiconEntry le = index.getLexicon().getLexiconEntry(term);
        int[] docidlist = index.getInvertedIndex().getDocuments(le)[0];
        Set<Integer> termDocList = new HashSet<Integer>(Ints.asList(docidlist));
        if (first) {
          unionSet = termDocList;
          first = false;
        } else unionSet = Sets.union(unionSet, termDocList);
        
      }
    }
    double nq = 0;
    if (unionSet != null) nq = unionSet.size();
    
    outputFeatures.add(-1 * Math.log(nq / numberOfDocuments));
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