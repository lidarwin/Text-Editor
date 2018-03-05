package tex61;

import java.io.PrintWriter;

/** A PageAssembler that sends lines immediately to a PrintWriter, with
 *  terminating newlines.
 *  @author Darwin Li
 */
class PagePrinter extends PageAssembler {

    /** A new PagePrinter that sends lines to OUT. */
    PagePrinter(PrintWriter out) {
        _out = out;
        parskip = 1;
        linecount = 0;
        startpar = true;
    }

    /** Print LINE to my output. */
    @Override
    void write(String line) {
        if (linecount == getTextHeight()) {
            _out.print("\f");
            linecount = 0;
        }
        if (startpar) {
            for (int i = 0; i < parskip; i += 1) {
                if (linecount == 0) {
                    break;
                }
                _out.println();
                linecount += 1;
            }
            setStartpar(false);
        }
        _out.println(line);
        linecount += 1;
    }

    @Override
    /** Set paragraph skip to VAL.  VAL >= 0. */
    void setParskip(int val) {
        parskip = val;
    }

    @Override
    /** Returns the boolean value of Startpar.*/
    boolean getStartpar() {
        return startpar;
    }

    @Override
    /** Set boolean start of new paragraph to ON.*/
    void setStartpar(boolean on) {
        startpar = on;
    }

    @Override
    /** Sets the linecount to an int VAL.*/
    void setLinecount(int val) {
        linecount = val;
    }

    @Override
    /** Returns int number of lines currently.*/
    int getLinecount() {
        return linecount;
    }

    /** Integer variable giving the number of lines for parskip, or
     *  the number of lines betweeen paragraphs. */
    private int parskip;

    /** Integer count of number of lines on the page currently.*/
    private int linecount;


    /** The PrintWriter we will write to.*/
    private PrintWriter _out;

    /** Boolean of whether this is a start of a new paragraph.*/
    private boolean startpar;

}
