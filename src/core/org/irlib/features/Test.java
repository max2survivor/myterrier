/**
 * 
 */
package org.irlib.features;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.terrier.matching.CollectionResultSet;
import org.terrier.matching.MatchingQueryTerms;
import org.terrier.matching.ResultSet;
import org.terrier.matching.dsms.DocumentScoreModifier;
import org.terrier.querying.Manager;
import org.terrier.querying.Request;
import org.terrier.structures.CollectionStatistics;
import org.terrier.structures.Index;
import org.terrier.structures.LexiconEntry;
import org.terrier.utility.ApplicationSetup;
import org.terrier.utility.HeapSort;

/**
 * @author semanticpc
 * 
 */
public class Test {
  Index index;
  protected CollectionStatistics collStats;
  protected List<DocumentScoreModifier> dsms;
  
  /** The default namespace for document score modifiers. */
  protected static final String DSMNS = "org.terrier.matching.dsms.";
  
  protected int docid;
  /** The current read score. */
  protected double score;
  protected Manager queryingManager;
  
  public Test(Index _index) throws IOException {
    this.index = _index;
    this.collStats = _index.getCollectionStatistics();
    this.initDSMs("");
    queryingManager = new Manager(index);
  }
  
  protected void initDSMs(String defDSMs) {
    dsms = new ArrayList<DocumentScoreModifier>();
    try {
      for (String modifierName : defDSMs.split("\\s*,\\s*")) {
        if (modifierName.length() == 0) continue;
        if (modifierName.indexOf('.') == -1) modifierName = DSMNS
            + modifierName;
        dsms.add((DocumentScoreModifier) Class.forName(modifierName)
            .newInstance());
      }
    } catch (Exception e) {}
  }
  
  protected boolean read(String _qid) throws IOException {
    docid += 1;
    score = 0;
    if (docid <= 3) return true;
    else return false;
  }
  
  public ResultSet match(String _qid, MatchingQueryTerms mqt)
      throws IOException {
    int max = collStats.getNumberOfDocuments();
    
    ResultSet rs = new CollectionResultSet(max);
    rs.initialise();
    
    int[] docids = rs.getDocids();
    double[] scores = rs.getScores();
    
    int matched = 10;
    while (read(_qid)) {
      docids[matched] = docid;
      scores[matched] = score;
    }
    
    rs.setExactResultSize(matched);
    rs.setResultSize(matched);
    
    // crop to the actual size
    rs = rs.getResultSet(0, matched);
    docids = rs.getDocids();
    scores = rs.getScores();
    
    // the number of document score modifiers
    int numDSMs = dsms.size();
    
    for (int t = 0; t < numDSMs; t++) {
      if (dsms.get(t).modifyScores(index, mqt, rs)) {
        HeapSort.descendingHeapSort(scores, docids, rs.getOccurrences(),
            rs.getResultSize());
      }
    }
    
    return rs;
  }
  
  public static void main1(String[] args) throws IOException {
    
    FileInputStream in = new FileInputStream(
        "/Users/semanticpc/Documents/Dropbox/workspace/terrier/conf/1Click/1Click.mand.properties");
    ApplicationSetup.configure(new BufferedInputStream(in));
    Index i = Index.createIndex();
    Test t = new Test(i);
    Request srq = (Request) t.queryingManager.newSearchRequest("1",
        "michael jackson death");
    t.queryingManager.runPreProcessing(srq);
    ResultSet res = t.match("1", srq.getMatchingQueryTerms());
    System.out.println(res.getExactResultSize());
    int docids[] = null;
    double scores[] = res.getScores();
    int j = 0;
    for (int docid : docids) {
      System.out.println(docid + " " + scores[j++]);
    }
    
  }
  
  public static void main(String[] args) throws IOException {
    FileInputStream in = new FileInputStream(
        "/Users/semanticpc/Documents/Dropbox/workspace/terrier/conf/1Click/1Click.mand.properties");
    ApplicationSetup.configure(new BufferedInputStream(in));
    Index index = Index.createIndex();
    LexiconEntry le = index.getLexicon().getLexiconEntry("jackson");
    int[][] docidlist = index.getInvertedIndex().getDocuments(le);
    System.out.println(docidlist[0][2]);
    System.out.println(docidlist.length);
    for (int i = 0; i < docidlist[0].length; i++)
      System.out.print(" " + docidlist[0][i]);
    System.out.println();
    for (int i = 0; i < docidlist[1].length; i++)
      System.out.print(" " + docidlist[1][i]);
    System.out.println();
    for (int i = 0; i < docidlist[2].length; i++)
      System.out.print(" " + docidlist[2][i]);
    System.out.println();
    for (int i = 0; i < docidlist[3].length; i++)
      System.out.print(" " + docidlist[3][i]);
    System.out.println();
    
  }
}
