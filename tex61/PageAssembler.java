package tex61;

import static tex61.FormatException.error;

/** A PageAssembler accepts complete lines of text (minus any
 *  terminating newlines) and turns them into pages, adding form
 *  feeds as needed.  It prepends a form feed (Control-L  or ASCII 12)
 *  to the first line of each page after the first.  By overriding the
 *  'write' method, subtypes can determine what is done with
 *  the finished lines.
 *  @author Darwin Li
 */
abstract class PageAssembler {

    /** Create a new PageAssembler that sends its output to OUT.
     *  Initially, its text height is unlimited. It prepends a form
     *  feed character to the first line of each page except the first. */
    PageAssembler() {
        heightLimit = tenthou;
    }

    /** Get the text height and return it.*/
    int getTextHeight() {
        return heightLimit;
    }

    /** Add LINE to the current page, starting a new page with it if
     *  the previous page is full. A null LINE indicates a skipped line,
     *  and has no effect at the top of a page. */
    void addLine(String line) {
        setTextHeight(getTextHeight() - 1);
    }

    /** Set text height to VAL, where VAL > 0. */
    void setTextHeight(int val) {
        if (val < 0) {
            error("TextHeight must be positive");
        }
        heightLimit = val;
    }

    /** Perform final disposition of LINE, as determined by the
     *  concrete subtype. */
    abstract void write(String line);

    /** Set paragraph skip to VAL.  VAL >= 0. */
    abstract void setParskip(int val);

    /** Set boolean start of new paragraph to ON.*/
    abstract void setStartpar(boolean on);

    /** Sets the linecount to an int VAL.*/
    abstract void setLinecount(int val);

    /** Returns int number of lines currently.*/
    abstract int getLinecount();

    /** Returns the boolean value of Startpar.*/
    abstract boolean getStartpar();

    /** Integer number of lines that we will start a new page for.*/
    private int heightLimit;

    /** Integer to be used as 10000.*/
    private final int tenthou = 10000;


}
