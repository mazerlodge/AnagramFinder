package com.spsw.AnagramFinder;

import java.io.*;


public class WordList implements UsefulConstants {

  // members...
  static Word[] Dictionary= new Word[MAX_WORDS];
  static int totWords=0;


  // ctor
  public WordList() {
  }

  // methods...
  public void readDict(String f) {

      FileInputStream fis;
      try {
    	  fis = new FileInputStream(f);

    	  e.println("reading dictionary...");

	      char buffer[] = new char[MAX_WORD_LEN];
	      String s;
	      int r =0;
	      while (r!=EOF) {
	          int i=0;
	              // read a word in from the word file
	              while ( (r=fis.read()) != EOF ) {
	              // read past EOLN on a PC
	                  if ( r == '\r' ) r=fis.read();
	                  if ( r == '\n' ) break;
	                  buffer[i++] = (char) r;
	              }
	          s=new String(buffer,0,i);
	          Dictionary[totWords] = new Word(s);
	          totWords++;
	      }
	      
	      fis.close();
	      e.println("main dictionary has " + totWords + " entries.");
      
      }
      catch (FileNotFoundException fnfe) {
          e.println ("Cannot open the file of words '" + f + "'");
          throw new RuntimeException();
      }
      catch (IOException ioe) {
          e.println ("Cannot read the file of words ");
          throw new RuntimeException();
      }
      
  }
  
  /*
  public void ReadDict_old(String f) {

      FileInputStream fis;
      try {
    	  fis = new FileInputStream(f);
      }
      catch (FileNotFoundException fnfe) {
          e.println ("Cannot open the file of words '" + f + "'");
          throw new RuntimeException();
      }
      e.println("reading dictionary...");

      char buffer[] = new char[MAX_WORD_LEN];
      String s;
      int r =0;
      while (r!=EOF) {
          int i=0;
          try {
              // read a word in from the word file
              while ( (r=fis.read()) != EOF ) {
              // read past EOLN on a PC
                  if ( r == '\r' ) r=fis.read();
                  if ( r == '\n' ) break;
                  buffer[i++] = (char) r;
              }
          } catch (IOException ioe) {
              e.println ("Cannot read the file of words ");
              throw new RuntimeException();
          }
          s=new String(buffer,0,i);
          Dictionary[totWords] = new Word(s);
          totWords++;
      }
      e.println("main dictionary has " + totWords + " entries.");
  }
*/

}
