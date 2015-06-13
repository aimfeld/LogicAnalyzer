/*
 * AnalyzeMessager.java
 *
 * Created on 19. Januar 2004, 02:10
 */

/**
 *
 * @author  Adrian Imfeld
 */

import javax.swing.JTextPane;

public class AnalyzeMessager 
{
    static JTextPane tp = null;
    
    public static void Init(JTextPane textpane)
    {
        tp = textpane;
    }
    
    public static void AnalyzeMessage(String m)
    {
        if (tp != null)
        {
            String t = tp.getText();
            tp.setText(t + (t.equals("") ? "" : "\n") + m);
        }
    }
}
