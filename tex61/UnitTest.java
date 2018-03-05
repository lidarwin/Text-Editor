package tex61;
import ucb.junit.textui;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Text Formatter.
 *  @author Darwin Li
 */
public class UnitTest {

    /** Run the JUnit tests in the tex61 package
     *  textui.runClasses(Tex61.LineAssembler);
     *  import org.junit.Test;. */
    public static void main(String[] ignored) {
        textui.runClasses(tex61.PageAssemblerTest.class);
    }
}
