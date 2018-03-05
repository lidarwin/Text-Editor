package tex61;

import java.util.List;

/** A PageAssembler that collects its lines into a designated List.
 *  @author Darwin Li
 */
class PageCollector extends PageAssembler {

    /** A new PageCollector that stores lines in OUT. */
    PageCollector(List<String> out) {
        _out = out;
        linecount = 0;
        startpar = true;
    }

    /** Add LINE to my List. */
    @Override
    void write(String line) {
        if (startpar) {
            for (int i = 0; i < parskip; i += 1) {
                _out.add("\n");
                linecount += 1;
            }
        }
        setStartpar(false);
        if (line.equals("")) {
            return;
        } else {
            _out.add(line);
            linecount += 1;
        }
    }

    @Override
    /** Set paragraph skip to VAL.  VAL >= 0. */
    void setParskip(int val) {
        parskip = val;
    }

    @Override
    /** Set boolean start of new paragraph to ON.*/
    void setStartpar(boolean on) {
        startpar = on;
    }

    @Override
    /** Returns the boolean value of Startpar.*/
    boolean getStartpar() {
        return startpar;
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

    /** The list of lines that are being built.*/
    private List<String> _out;

    /** Integer variable giving the number of lines for parskip, or
     *  the number of lines betweeen paragraphs. */
    private int parskip;

    /** Integer count of number of lines on the page currently.*/
    private int linecount;

    /** Boolean of whether this is a start of a new paragraph.*/
    private boolean startpar;
}
