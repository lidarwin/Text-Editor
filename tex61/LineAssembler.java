package tex61;

import java.util.ArrayList;

import static tex61.FormatException.error;

/** An object that receives a sequence of words of text and formats
 *  the words into filled and justified text lines that are sent to a receiver.
 *  @author Darwin Li
 */
class LineAssembler {

    /** A new, empty line assembler with default settings of all
     *  parameters, sending finished lines to PAGES. */
    LineAssembler(PageAssembler pages) {
        _pages = pages;
        indent = Defaults.INDENTATION;
        parindent = Defaults.PARAGRAPH_INDENTATION;
        textwidth = Defaults.TEXT_WIDTH;
        justify = true;
        fill = true;
        lastl = false;
        endmode = false;
        currword = "";
        currline = new ArrayList<String>();
        charcount = 0;
        wordcount = 0;
        wordcount = 0;
        refnum = 1;
    }

    /** Return the boolean value of fill.*/
    boolean getFill() {
        return fill;
    }

    /** Add the reference number as the first word.*/
    void refnumAdd() {
        addText("[" + refnum + "]");
        finishWord();
    }

    /** Returns the boolean value of Startpar.*/
    boolean getStartpar() {
        return _pages.getStartpar();
    }

    /** Add TEXT to the word currently being built. */
    void addText(String text) {
        currword = currword + text;
    }

    /** Finish the current word, if any, and add to words being accumulated. */
    void finishWord() {
        String reff = "[" + refnum + "]";
        int c = textwidth - charcount - wordcount - currword.length();
        int b = c - indent;
        int a = b - parindent;
        int a1 = c - parindent;
        int d = a - reff.length();
        if (parindent + indent < 0) {
            error("Invalid parindent andor indent");
        }
        if (currword.equals("")) {
            return;
        } else {
            if (getStartpar() && getFill() && endmode) {
                if (d >= 0) {
                    addWord(currword);
                } else {
                    newLine();
                    addWord(currword);
                }
            } else if (getStartpar() && getFill()) {
                if (a >= 0) {
                    addWord(currword);
                } else {
                    newLine();
                    addWord(currword);
                }
            } else if (getStartpar()) {
                addWord(currword);
            } else if (getFill()) {
                if (b >= 0) {
                    addWord(currword);
                } else {
                    newLine();
                    addWord(currword);
                }
            } else {
                if (c >= 0) {
                    addWord(currword);
                } else {
                    addWord(currword);
                }
            }
        }
    }

    /** Add WORD to the formatted text with SPACES STARTPARG and FIL. */
    void addWord(String word) {
        currline.add(word);
        currword = "";
        wordcount += 1;
        charcount += word.length();
    }

    /** Add LINE to our output, with no preceding paragraph skip.  There must
     *  not be an unfinished line pending. */
    void addLine(String line) {
        _pages.write(line);
        currline = new ArrayList<String>();
        wordcount = 0;
        charcount = 0;
    }

    /** Return the current word being built by pointer.*/
    String getCurrword() {
        return currword;
    }

    /** Returns int number of lines currently in _pages.*/
    int getLinecount() {
        return _pages.getLinecount();
    }

    /** Returns the current reference number.*/
    int getRefnumber() {
        return refnum;
    }

    /** Sets the linecount of _pages to an int VAL.*/
    void setLinecount(int val) {
        _pages.setLinecount(val);
    }

    /** Set the current indentation to VAL. VAL >= 0. */
    void setIndentation(int val) {
        indent = val;
    }

    /** Set the current paragraph indentation to VAL. VAL >= 0. */
    void setParIndentation(int val) {
        parindent = val;
    }

    /** Set the text width to VAL, where VAL >= 0. */
    void setTextWidth(int val) {
        textwidth = val;
    }

    /** Iff ON, set fill mode. */
    void setFill(boolean on) {
        fill = on;
    }

    /** Sets the boolean value to ON.*/
    void setFirst(boolean on) {
        firstl = on;
    }

    /** Iff ON, set justify mode (which is active only when filling is
     *  also on). */
    void setJustify(boolean on) {
        justify = on;
    }

    /** Set paragraph skip to VAL.  VAL >= 0. */
    void setParSkip(int val) {
        _pages.setParskip(val);
    }

    /** Set page height to VAL > 0. */
    void setTextHeight(int val) {
        _pages.setTextHeight(val);
    }

    /** Set the endmode to be boolean ON.*/
    void setEnd(boolean on) {
        endmode = on;
    }

    /** Increases reference number by 1.*/
    void incrRef() {
        refnum += 1;
    }

    /** Process the end of the current input line.  No effect if
     *  current line accumulator is empty or in fill mode.  Otherwise,
     *  adds a new complete line to the finished line queue and clears
     *  the line accumulator. */
    void newLine() {
        if (getStartpar() && getFill()) {
            emitLine(indent + parindent, wordcount);
        } else if (getStartpar()) {
            emitLine(parindent, wordcount);
        } else if (getFill()) {
            emitLine(indent, wordcount);
        } else {
            emitLine(0, wordcount);
        }
    }

    /** If there is a current unfinished paragraph pending, close it
     *  out and start a new one. */
    void endParagraph() {
        String workingline;
        if (wordcount == 0) {
            addLine("\n");
        } else if (getStartpar() && endmode) {
            String workingline2 = "[" + refnum + "]" + " ";
            int temp = workingline2.length();
            workingline = spacecreator(indent + parindent) + workingline2;
            workingline = workingline
                + emitLinehelper(currline, 0, 0);
            addLine(workingline);
            _pages.setStartpar(true);
        } else if (getStartpar()) {
            workingline = spacecreator(indent + parindent);
            workingline = workingline
                + emitLinehelper(currline, 0, 0);
            addLine(workingline);
            _pages.setStartpar(true);
        } else {
            workingline = spacecreator(indent);
            workingline = workingline + emitLinehelper(currline, 0, 0);
            addLine(workingline);
            _pages.setStartpar(true);
        }
    }

    /** Takes in SPACI, ARRY, KI and returns justification
      * algorithim interger.*/
    int justify(int spaci, int arry, int ki) {
        double sspaci = (double) spaci;
        double aarry = (double) arry;
        double kki = (double) ki + 1.0;
        double temp = (sspaci * kki) / (aarry - 1.0);
        return (int) (.5 + temp);
    }

    /** Function that takes in an ArrayList ARR, int SPAC, and int HOW
      * to return a String that will be used for emitLine.*/
    String emitLinehelper(ArrayList<String> arr, int spac, int how) {
        String workingline = "";
        int insert = 0;
        int prev;
        if (how == 1) {
            for (int k = 0; k < arr.size(); k = k + 1) {
                if (k == arr.size() - 1) {
                    workingline = workingline + arr.get(k);
                    break;
                }
                prev = insert;
                insert = justify(spac, arr.size(), k);
                workingline = workingline + currline.get(k);
                workingline = workingline + spacecreator(insert - prev);
            }
            return workingline;
        } else if (how == 0) {
            for (int k = 0; k < arr.size(); k = k + 1) {
                if (k == wordcount - 1) {
                    workingline = workingline + arr.get(k);
                    break;
                }
                workingline = workingline + currline.get(k) + " ";
            }
            return workingline;
        } else if (how == 2) {
            for (int k = 0; k < arr.size(); k = k + 1) {
                if (k == wordcount - 1) {
                    workingline = workingline + arr.get(k);
                    break;
                }
                workingline = workingline + currline.get(k) + "   ";
            }
            return workingline;
        }
        return "Uh Oh";
    }

    /** Transfer contents of currword to _pages, adding IND characters of
     *  indentation, and a total of SPACES spaces between words, evenly
     *  distributed.  Assumes _words is not empty.  Clears currword
     *  and _chars. */
    private void emitLine(int ind, int spaces) {
        String workingline = spacecreator(ind);
        int spacing = textwidth - ind - charcount;
        int insert = 0;
        int prev;
        if (getFill() && justify) {
            int now = 1;
            if (spacing >= 3 * (wordcount - 1)) {
                now = 2;
            }
            if (getStartpar() && endmode) {
                String workingline2 = "[" + refnum + "]" + " ";
                int temp = workingline2.length();
                workingline = spacecreator(ind) + workingline2;
                spacing = spacing - temp;
                workingline = workingline
                    + emitLinehelper(currline, spacing, now);
                addLine(workingline);
            } else if (getStartpar()) {
                workingline = workingline
                    + emitLinehelper(currline, spacing, now);
                addLine(workingline);
            } else {
                workingline = workingline
                    + emitLinehelper(currline, spacing, now);
                addLine(workingline);
            }
        } else {
            workingline = workingline
                + emitLinehelper(currline, spacing, 0);
            addLine(workingline);
        }
    }

    /** Returns a String of N number of spaces.*/
    String spacecreator(int n) {
        String temp = "";
        for (int k = 0; k < n; k = k + 1) {
            temp = temp + " ";
        }
        return temp;
    }

    /** If the line accumulator is non-empty, justify its current
     *  contents, if needed, add a new complete line to _pages,
     *  and clear the line accumulator. LASTLINE indicates the last line
     *  of a paragraph. */
    private void outputLine(boolean lastLine) {

    }

    /** Destination given in constructor for formatted lines. */
    private final PageAssembler _pages;

    /** Integer variable giving the indentation value.*/
    private int indent;

    /** Integer variable giving the paragraph indentation value. */
    private int parindent;

    /** Integer variable giving the text width.*/
    private int textwidth;

    /** Integer that tells us the current character count of words.*/
    private int charcount;

    /** Integer that gives the reference number for Endnotes.*/
    private int refnum;

    /** Integer that tells us the number of words.*/
    private int wordcount;

    /** Boolean variable for whether to justify.*/
    private boolean justify;

    /** Boolean variable for whether to fill.*/
    private boolean fill;

    /** Boolean for whether this is the endnote mode LineAssembler.*/
    private boolean endmode;

    /** String for building the current word.*/
    private String currword;

    /** List of strings for building the current line.*/
    private ArrayList<String> currline;

    /** Boolean that tells whether this is the last line.*/
    private Boolean lastl;

    /** Boolean that tells whether this is the first line.*/
    private Boolean firstl;

}
