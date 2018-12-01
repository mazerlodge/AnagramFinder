package com.spsw.AnagramFinder;

public class Word {

    // members...
    int mask;
    byte count[]= new byte[26];
    int total;
    String aword;

    // ctor
    Word(String s) {
        // construct an entry from a string

        int ch;
        aword = s;
        mask = ~0;
        total = 0;
        s = s.toLowerCase();
        for (int i = 'a'; i <= 'z'; i++) count[i-'a'] = 0;

        for (int i = s.length()-1; i >= 0; i--) {
            ch = s.charAt(i) - 'a';
            if (ch >= 0 && ch < 26) {
                total++;
                count[ch]++;
                mask &= ~(1 << ch);
            }
        }
    }

    // methods...
    public String dumpMask() {
    /* for debugging */

    String s = "";
    for (int i = 0; i < 26; i++)
      s = ( (mask & (1 << i)) >> i) + s;
    return s;

    }


}
