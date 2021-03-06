package tex61;

import java.io.PrintWriter;
import java.util.ArrayList;

import static tex61.FormatException.reportError;

/** Receives (partial) words and commands, performs commands, and
 *  accumulates and formats words into lines of text, which are sent to a
 *  designated PageAssembler.  At any given time, a Controller has a
 *  current word, which may be added to by addText, a current list of
 *  words that are being accumulated into a line of text, and a list of
 *  lines of endnotes.
 *  @author Darwin Li
 */
class Controller {

    /** A new Controller that sends formatted output to OUT. */
    Controller(PrintWriter out) {
        _out = out;
        pprint = new PagePrinter(_out);
        buffer = new ArrayList<String>();
        pcollect = new PageCollector(buffer);
        linecoll = new LineAssembler(pcollect);
        linecoll.setIndentation(Defaults.ENDNOTE_INDENTATION);
        linecoll.setParIndentation(Defaults.ENDNOTE_PARAGRAPH_INDENTATION);
        linecoll.setParSkip(Defaults.ENDNOTE_PARAGRAPH_SKIP);
        linecoll.setTextWidth(Defaults.ENDNOTE_TEXT_WIDTH);
        linecoll.setEnd(true);
        lineprint = new LineAssembler(pprint);
        lineprint.setTextHeight(Defaults.TEXT_HEIGHT);
        pointer = lineprint;
        endmode = false;
    }

    /** Returns the current reference number.*/
    int getRefnumber() {
        return linecoll.getRefnumber();
    }

    /** Add TEXT to the end of the word of formatted text currently
     *  being accumulated. */
    void addText(String text) {
        pointer.addText(text);
    }

    /** Finish any current word of text and, if present, add to the
     *  list of words for the next line.  Has no effect if no unfinished
     *  word is being accumulated. */
    void endWord() {
        pointer.finishWord();
    }

    /** Finish any current word of formatted text and process an end-of-line
     *  according to the current formatting parameters. */
    void addNewline() {
        pointer.newLine();
    }

    /** Finish any current word of formatted text, format and output any
     *  current line of text, and start a new paragraph. */
    void endParagraph() {
        pointer.endParagraph();
    }


    /** If valid, process TEXT into an endnote, first appending a reference
     *  to it to the line currently being accumulated. */
    void formatEndnote(String text) {
        setEndnoteMode();
        InputParser ender = new InputParser(text, this);
        ender.process();
        setNormalMode();
    }

    /** Set the current text height (number of lines per page) to VAL, if
     *  it is a valid setting.  Ignored when accumulating an endnote. */
    void setTextHeight(int val) {
        if (val < 0) {
            reportError("Error: Height must be positive");
        }
        if (!endmode) {
            pprint.setTextHeight(val);
            linecoll.setTextHeight(val);
        }
    }

    /** Set the current text width (width of lines including indentation)
     *  to VAL, if it is a valid setting. */
    void setTextWidth(int val) {
        if (val < 0) {
            reportError("Error: Width must be positive");
        }
        pointer.setTextWidth(val);
    }

    /** Set the current text indentation (number of spaces inserted before
     *  each line of formatted text) to VAL, if it is a valid setting. */
    void setIndentation(int val) {
        pointer.setIndentation(val);
        if (val < 0) {
            reportError("Error: Indent must be positive");
        }
    }

    /** Set the current paragraph indentation (number of spaces inserted before
     *  first line of a paragraph in addition to indentation) to VAL, if it is
     *  a valid setting. */
    void setParIndentation(int val) {
        pointer.setParIndentation(val);
    }

    /** Set the current paragraph skip (number of blank lines inserted before
     *  a new paragraph, if it is not the first on a page) to VAL, if it is
     *  a valid setting. */
    void setParSkip(int val) {
        pointer.setParSkip(val);
    }

    /** Iff ON, begin filling lines of formatted text. */
    void setFill(boolean on) {
        pointer.setFill(on);
    }

    /** Iff ON, begin justifying lines of formatted text whenever filling is
     *  also on. */
    void setJustify(boolean on) {
        pointer.setJustify(on);
    }

    /** Finish the current formatted document or endnote (depending on mode).
     *  Formats and outputs all pending text. The endnote version will be
     *  called at the very end of Main. */
    void close() {
        if (endmode) {
            pointer.incrRef();
            setNormalMode();
        } else {
            writeEndnotes();
        }
    }

    /** Return the current word being built by pointer.*/
    String getCurrword() {
        return pointer.getCurrword();
    }

    /** Return the boolean value of fill.*/
    boolean getFill() {
        return pointer.getFill();
    }

    /** Return the boolean value of endmode.*/
    boolean endnoteMode() {
        return endmode;
    }
    /** Start directing all formatted text to the endnote assembler. */
    private void setEndnoteMode() {
        pointer = linecoll;
        endmode = true;
    }

    /** Return to directing all formatted text to _mainText. */
    private void setNormalMode() {
        pointer = lineprint;
        endmode = false;
    }

    /** Write all accumulated endnotes to _mainText. */
    private void writeEndnotes() {
        int temp = linecoll.getLinecount() + lineprint.getLinecount();
        if (temp <= pprint.getTextHeight()) {
            for (int i = 0; i < buffer.size(); i += 1) {
                _out.println(buffer.get(i));
            }
        } else if (temp > pprint.getTextHeight()) {
            int b = pprint.getTextHeight() - lineprint.getLinecount();
            int n = 0;
            for (; n < b && pcollect.getLinecount() >= 0; n += 1) {
                _out.println(buffer.get(n));
                pcollect.setLinecount(pcollect.getLinecount() - 1);
            }
            _out.print("\f");
            int a = pprint.getTextHeight() % pcollect.getLinecount();
            int c = pprint.getTextHeight() / pcollect.getLinecount();
            for (int d = c; d >= 0 && pcollect.getLinecount() >= 0; d -= 1) {
                for (int e = pprint.getTextHeight();
                    pcollect.getLinecount() >= 0 && e >= 0; e -= 1, n += 1) {
                    _out.println(buffer.get(n));
                    pcollect.setLinecount(pcollect.getLinecount() - 1);
                }
                if (pcollect.getLinecount() >= 0) {
                    _out.print("\f");
                }
            }
            for (; a >= 0 && pcollect.getLinecount() >= 0; a -= 1, n += 1) {
                _out.println(buffer.get(n));
                pcollect.setLinecount(pcollect.getLinecount() - 1);
            }
        }
    }

    /** True iff we are currently processing an endnote. */
    private boolean endmode;

    /** The PrintWriter that we also have access to in PagePrinter.*/
    private PrintWriter _out;

    /** The PagePrinter will write to our OUT.*/
    private PagePrinter pprint;

    /** The PageCollector that Collector will send to be collected.*/
    private PageCollector pcollect;

    /** The LineAssembler that has well-defined methods for
    /*  collecting text with a PageCollector.*/
    private LineAssembler linecoll;

    /** The LineAssembler that has well-defined methods for
    /*  printing directly to output with a PagePrinter.*/
    private LineAssembler lineprint;

    /** The buffer that is an ArrayList for our PageCollector.*/
    private ArrayList<String> buffer;

    /** PageAssembler pointer that we are workign with.*/
    private LineAssembler pointer;

}
