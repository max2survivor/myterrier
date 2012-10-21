/**
 * 
 */
package org.irlib.features;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.terrier.structures.Index;
import org.terrier.utility.ApplicationSetup;

/**
 * @author semanticpc
 * 
 */
public class Test {
  public static void main(String[] args) throws IOException {
    FileInputStream in = new FileInputStream(
        "/Users/semanticpc/Documents/Dropbox/workspace/terrier/conf/1Click.index.properties");
    ApplicationSetup.configure(new BufferedInputStream(in));
    Index i = Index.createIndex();
    System.out.println(i.getDocumentIndex().getNumberOfDocuments());
    System.out.println(i.getCollectionStatistics().getAverageDocumentLength());
    System.out.println(i.getMetaIndex().getKeys()[0]);
    System.out.println(i.getMetaIndex().getKeys()[1]);
    System.out.println(i.getMetaIndex().getKeys()[2]);
    System.out.println(i.getMetaIndex().getKeys()[3]);
    System.out.println(i.getMetaIndex().getKeys()[4]);
    int docid = 1;
    System.out.println(i.getMetaIndex().getItem("docno", docid));
    System.out.println(i.getMetaIndex().getItem("url", docid));
    System.out.println(i.getMetaIndex().getItem("giventitle", docid));
    System.out.println(i.getMetaIndex().getItem("givensnippet", docid));
    System.out.println(i.getMetaIndex().getItem("rank", docid));
    
  }
}
