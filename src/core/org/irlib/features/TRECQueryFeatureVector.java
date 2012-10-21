package org.irlib.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.terrier.matching.MatchingQueryTerms;
import org.terrier.querying.Manager;
import org.terrier.querying.Request;
import org.terrier.structures.CollectionStatistics;
import org.terrier.structures.Index;
import org.terrier.structures.TRECQuery;
import org.terrier.utility.ApplicationSetup;
import org.terrier.utility.Files;

/**
 * A Feature Vector implementation that extracts various features for a given
 * TREC result file rather than the current index. The TREC result file should
 * have the following format:
 * 
 * <pre>
 * queryID Q0 docno score rank label
 * </pre>
 * 
 * <p>
 * <b>Properties:</b>
 * <ul>
 * </ul>
 * 
 * @authors Praveen Chandar
 */
public class TRECQueryFeatureVector {
  
  protected static final Pattern SPLIT_SPACE_PLUS = Pattern.compile("\\s+");
  
  /** The underlying index. */
  protected Index index;
  /** The underlying collections statistics. */
  protected CollectionStatistics collStats;
  
  /** The list of document score modifiers to be applied. */
  protected List<Feature> featureList;
  /** The TREC results filename. */
  protected String filename;
  /** The TREC results file reader. */
  protected BufferedReader reader;
  
  /** Whether the current query was found in the results file. */
  protected boolean found;
  /** Whether the current file has already been reset. */
  protected boolean reset;
  /** The TREC query file object. */
  protected TRECQuery queries;
  
  protected Manager queryingManager;
  private String qid;
  
  /**
   * Contructs an instance of the TRECResultsMatching given an index.
   * 
   * @param _index
   * @throws IOException
   */
  public TRECQueryFeatureVector(Index _index) throws IOException {
    this(_index, ApplicationSetup.getProperty("trec.topics", ""),
        ApplicationSetup.getProperty("features.class", ""));
  }
  
  /**
   * Contructs an instance of the TRECResultsMatching.
   * 
   * @param _index
   * @param _filename
   * @throws IOException
   */
  public TRECQueryFeatureVector(Index _index, String _filename)
      throws IOException {
    this(_index, _filename, ApplicationSetup.getProperty("features.class", ""));
  }
  
  /**
   * Contructs an instance of the TRECResultsMatching.
   * 
   * @param _index
   * @param _filename
   * @param defDSMs
   * @throws IOException
   */
  public TRECQueryFeatureVector(Index _index, String _filename,
      String _featureList) throws IOException {
    
    this.index = _index;
    this.collStats = _index.getCollectionStatistics();
    this.initFeatureList(_featureList);
    
    this.filename = _filename;
    queryingManager = new Manager(index);
    // queries = new TRECQuery(filename);
    try {
      queries = Class
          .forName(
              ApplicationSetup.getProperty("trec.topics.parser",
                  "org.terrier.structures.SingleLineTRECQuery"))
          .asSubclass(TRECQuery.class).getConstructor(String.class)
          .newInstance(filename);
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    reopen();
  }
  
  protected void reopen() throws IOException {
    if (reader != null) {
      try {
        reader.close();
      } catch (IOException e) {}
    }
    reader = Files.openFileReader(filename);
  }
  
  protected void initFeatureList(String _featureList) {
    featureList = new ArrayList<Feature>();
    for (String featureClass : _featureList.split(",")) {
      try {
        Feature ft = Class.forName(featureClass).asSubclass(Feature.class)
            .getConstructor(Index.class).newInstance(index);
        
        featureList.add(ft);
        if (ft.SPECIAL_INPUTS) handleSpecialInputs(ft);
      } catch (InstantiationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (SecurityException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
  }
  
  /**
   * @param ft
   */
  private void handleSpecialInputs(Feature ft) {
    // TODO Auto-generated method stub
    
  }
  
  public Vector<Double> nextQuery() {
    if (!queries.hasNext()) return null;
    String queryText = queries.next();
    qid = queries.getQueryId();
    
    Request srq = (Request) queryingManager.newSearchRequest(qid, queryText);
    queryingManager.runPreProcessing(srq);
    
    return extractQueryTextFeatures(qid, srq.getMatchingQueryTerms());
  }
  
  /**
   * Get a Vector of features for the given query
   * 
   * @param queryNumber
   *          - some ID of the query
   * @param queryTerms
   *          - query terms to match
   * @return ResultSet - the matched results
   * @throws IOException
   *           if a problem occurs during matching
   */
  
  public Vector<Double> extractQueryTextFeatures(String _qid,
      MatchingQueryTerms terms) {
    Vector<Double> vec = new Vector<Double>(featureList.size());
    for (int i = 0; i < featureList.size(); i++) {
      
      featureList.get(i).setUp(terms);
      vec.add(featureList.get(i).computeValue());
    }
    return vec;
  }
  
  public void setCollectionStatistics(CollectionStatistics _collStats) {
    this.collStats = _collStats;
  }
  
  /**
   * Returns collection statistics.
   * 
   * @return collection statistics
   */
  public CollectionStatistics getCollectionStatistics() {
    return collStats;
  }
  
  @Override
  protected void finalize() throws Throwable {
    reader.close();
  }
  
  public static void printFeatureVectors(String qid,
      Vector<Double> featureVector) {
    System.out.print(qid);
    for (double value : featureVector) {
      System.out.print("," + value);
    }
    System.out.println(" ");
  }
  
  public static void main(String[] args) {
    // verify command line args
    if (args.length < 1) {
      System.err.println("Usage: java TRECIndexing <config_file>");
      System.exit(1);
    }
    
    // verify input files
    File conf_file = new File(args[0]);
    if (!conf_file.exists() || !conf_file.isFile() || !conf_file.canRead()) {
      System.err.println("cannot find/read config file: "
          + conf_file.getAbsolutePath());
      System.exit(1);
    }
    
    ApplicationSetup.bootstrapInitialisationAlt(args[0]);
    try {
      Index i = Index.createIndex();
      TRECQueryFeatureVector trecFV = new TRECQueryFeatureVector(i);
      
      Vector<Double> features;
      while ((features = trecFV.nextQuery()) != null) {
        printFeatureVectors(trecFV.qid, features);
      }
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
