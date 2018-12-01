package com.spsw.AnagramFinder;

/**
 * <p>Title: Modified anagram finder. </p>
 *
 * <p>Description: Based on work of Peter van der Linden
 *     for the book Just Java 2, Chapter 12.
 *
 *  </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 *
 * @author mazerlodge
 * @version 1.0
 */
public class Anagram extends WordList {

  // members...
  static Word[] candidate = new Word[MAX_WORDS];
  static int totcandidates=0,
             minimumLength = 3;
  static int sortMask;

  // ctor
  public Anagram() {
  }

  // entry point
  public static void main(String[] argv) {

    Anagram aAna = new Anagram();
    
   if ( argv.length < 1 || argv.length > 3) {
       e.println("Usage: anagram  string-to-anagram "
                 + "[min-len [word file]]");
       return;
    }

    if (argv.length >= 2)
        minimumLength = Integer.parseInt(argv[1]);


    // word filename is optional 3rd argument
    aAna.readDict( argv.length==3? argv[2] : "./data/words.txt" );

    // Do the anagrams
    aAna.doAnagrams(argv[0]);


  }

  // methods...

  public void doAnagrams(String anag) {

      Word myAnagram = new Word(anag);

      // Note: Mask is inverted so it may be OR'd with dictionary masks
      //        to produce a list of dictionary words that are subsets of
      //        this word.  See comment in FindAnagram() for details.
      myAnagram.mask = ~myAnagram.mask;

      getcandidates(myAnagram);

      int RarestLetterLimit = sortcandidates(myAnagram);

      findAnagram(myAnagram, new String[50], 0, 0, RarestLetterLimit);

      o.println("----" + anag + "----");
  }

  private void getcandidates(Word d) {
      // Extracts a list of words from the dictionary that don't have letters
      // not present in the phrase (d) we are anagramming.

      for (int i = totcandidates = 0; i < totWords; i++)  {
     /*  This code for debugging use
          System.out.println (
            Dictionary[i].aword + "=Dict[i].mask=" +
            dump(Dictionary[i].mask) );
          System.out.println (
            d.aword             + "=d.mask="  + dump(d.mask)  );
          System.out.println (
            " |'d="  + dump(Dictionary[i].mask | d.mask)   );
          if ( (Dictionary[i].mask | d.mask) == (int) ~0)
             System.out.println ( " equals all 1's \n");
      */


    /* NB!  Dictionary mask, 0=present, so  GxxxxBx = 0111101
            d mask, 0=absent            so  GxExCBA = 1010111
            so ORing them together, all 1's means that the letters of the
            word in the dictionary is a subset of the word we are anagramming.
            I.e. the word in the dict only uses letters
            that are in the other word.
             dictionary word could be "tar", anagram word could be "rates"

     */

          if (   (  (Dictionary[i].mask | d.mask) == (int)~0)
                /* if dict word only has letters that are in this word */

              && (   Dictionary[i].total >= minimumLength   )
                /* and dict word > minimum length for anagram  */

              && (  (Dictionary[i].total + minimumLength <= d.total)
                /* and dict word len + minimum length for anagram < word len  */

                  || (Dictionary[i].total == d.total) )
                /* or dict word len == word len  */

              && ( fewerOfEachLetter(d.count, Dictionary[i].count) )  )

              candidate[totcandidates++]=Dictionary[i];

     }
     //Printcandidates();
     //e.println("Dictionary of words-that-are-substring-anagrams has "
     //           + totcandidates + " entries.");
  }

  private boolean fewerOfEachLetter(byte anagCount[], byte entryCount[]) {

      // NOTE: rearranged return value, verify new form operates correctly.

      boolean bRval = true;

      for (int i = 25; i >= 0; i--)
          if (entryCount[i] > anagCount[i]) {
            bRval = false;
            break;
          }
      return bRval;

  }

  public void printcandidates()  {
      for (int i = 0; i < totcandidates; i++)
          o.print( candidate[i].aword + ", "
                   + ((i%4 == 3)?"\n":" " ) );
      o.println("");
  }

  private void findAnagram(Word d,
                          String ResultsBuffer[],
                          int Level, int StartAt, int EndAt) {

  /*
   * Look at each word in the candidate list, between StartAt and EndAt,
   * and see if it can be part of our anagram phrase.
   * When we find one, add it to the ResultsBuffer, remove those letters from
   * the phrase we are anagramming, and make a recursive call to deal with
   * the rest.
   */

      int i, j;
      boolean enoughCommonLetters;
      Word WordToPass = new Word("");

      for (i = StartAt; i < EndAt; i++) {
    /* NB!  candidate mask, 0= that letter is present
            d mask, 1 = that letter is present
            so ORing them together,
             all 1's = dict word contains only letters that are in w
            Cand = ABDE = 001001111
            d    = BCEF = 011011000
            or'd =        011011111   so this candidate can't be part of
                                      an anagram of word "d".

            Cand = BEF  = 101100111
            d    = BCEF = 011011000
            or'd =        111111111   so this candidate CAN be part of
                                      an anagram of word "d".

     */

        boolean CandIsPossAngramOfd = false;
        CandIsPossAngramOfd = ( (d.mask | candidate[i].mask) == (int)~0);

        if ( CandIsPossAngramOfd ) {

            enoughCommonLetters = true;
            for (j = 25; j >=0 && enoughCommonLetters; j--)
                if (d.count[j] < candidate[i].count[j])  {
                    enoughCommonLetters = false;
                }

            if (enoughCommonLetters) {
              // OK,
              ResultsBuffer[Level] = candidate[i].aword;
              WordToPass.mask = 0;
              WordToPass.aword = "";
              WordToPass.total = 0;
              for (j = 25; j >= 0; j--) {
                  WordToPass.count[j] = (byte)
                         (d.count[j] - candidate[i].count[j]);
                  if ( WordToPass.count[j] != 0 ) {
                      WordToPass.total +=
                         (int)WordToPass.count[j];
                      WordToPass.mask |= 1 << j;
                      char letter = (char) ('a' + j);
                      WordToPass.aword = WordToPass.aword + letter;
                  }
              }
              if (WordToPass.total == 0) {
                  /* We have reduced the to-anagram phrase to zero! */
                  for (j = 0; j <= Level; j++)
                      o.print(ResultsBuffer[j] + " ");
                  o.println();
              } else if (WordToPass.total < minimumLength) {
                     ; /* go round loop again, seeking a longer word */
              } else {
                  // make a recursive call to find anagrams for the rest of phrase
                  findAnagram(WordToPass, ResultsBuffer, Level+1, i, totcandidates);
              }
           }
       }
     }
  }

  private int sortcandidates(Word d) {
  // it returns the index of the first word in the sorted dictionary of
  // anagram possibilities, that doesn't contain the rarest letter.   The
  // rarest letter will be hardest to match, so we start by using those
  // words in the anagram first.

      int [] MasterCount=new int[26];
      int LeastCommonIndex=0, LeastCommonCount;
      int i, j;

      for (j = 25; j >= 0; j--) MasterCount[j] = 0;

      for (i = totcandidates-1; i >= 0; i--)
          for (j = 25; j >= 0; j--)
              MasterCount[j] += candidate[i].count[j];

      LeastCommonCount = MAX_WORDS * 5;
      for (j = 25; j >= 0; j--)
          if (    MasterCount[j] != 0
               && MasterCount[j] < LeastCommonCount
               && (d.mask & (1 << j) ) != 0  ) {
              LeastCommonCount = MasterCount[j];
              LeastCommonIndex = j;
          }
      // we are looking for the least frequent letter in our phrase

      sortMask = (1 << LeastCommonIndex);

      quickSort(0, totcandidates-1 );

      for (i = 0; i < totcandidates; i++)
          if ((sortMask & ~candidate[i].mask) == 0) {
              break;
          }
      // e.println("least common letter is '"
      //            + (char)(LeastCommonIndex+'a') + "'" );
      // e.println("root breadth is " + i + " words");
      return i;
  }

  private void quickSort(int left, int right) {
      // standard quicksort from any algorithm book

      /** @todo (M) Analyze this, what is sum div by 2 doing? */

      int i, last;
      if (left >= right) return;
      swap(left, (left+right)/2);
      last = left;
      for (i=left+1; i<=right; i++)  /* partition */
          if (multiFieldCompare( candidate[i],
                                 candidate[left] ) == -1 )
               swap( ++last, i);

      swap(last, left);
      quickSort(left, last-1);
      quickSort(last+1,right);
  }

  private int multiFieldCompare(Word s, Word t) {
      if ( (s.mask & sortMask) != (t.mask & sortMask) )
          return ( (s.mask & sortMask)>(t.mask & sortMask)? 1:-1);

      if ( t.total != s.total )
          return (t.total - s.total);

      return (s.aword).compareTo(t.aword);
  }

  private void swap(int d1, int d2) {
      Word tmp = candidate[d1];
      candidate[d1] = candidate[d2];
      candidate[d2] = tmp;
  }



}
