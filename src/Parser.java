/*
 * Parser.java
 *
 * Created on 12. Januar 2004, 16:45
 */

/**
 *
 * @author  Adrian Imfeld
 */
public class Parser 
{
    public static SymbolNode Parse(String s, SymbolNode n, boolean PutMessages) throws SyntaxErrorException
    {                                
        s = s.replaceAll(" ", ""); // Blanks rausputzen
        
        s = RemoveBrackets(s, PutMessages);
        
        if (PutMessages)
            AnalyzeMessager.AnalyzeMessage("processing "+s+" ...");
        
        if (s.length() == 0)  // notwendig, weil der Ausdruck "()" gewesen sein könnte
            throw new SyntaxErrorException(s, "General syntax error", PutMessages);

        if (s.length() > 1)
        {
            int JunctorPos = GetLeastBindingJunctorPos(s, PutMessages);            
                        
            if (JunctorPos == -1)
                throw new SyntaxErrorException(s, "No operator found", PutMessages);
            else
            {             //Junktor gefunden   
                if (Symbols.IsUnJunctorSymbol(s.charAt(JunctorPos)) && JunctorPos != 0)
                    throw new SyntaxErrorException(s, "Left of an unary operator there must not be a complete sub-formula.", PutMessages);
                
                if (PutMessages)
                    AnalyzeMessager.AnalyzeMessage("operator added: "+s.charAt(JunctorPos));                    
                
                try 
                {                                       
                    if (Symbols.IsBinJunctorSymbol(s.charAt(JunctorPos)))    //Rekursion                    
                        n = new BinJunctorNode(Parse(s.substring(0, JunctorPos), null, PutMessages), 
                                               Parse(s.substring(JunctorPos+1, s.length()), null, PutMessages), 
                                               Symbols.GetBinJunctor(s.charAt(JunctorPos)));
                    if (Symbols.IsUnJunctorSymbol(s.charAt(JunctorPos)))    //Rekursion                    
                        n = new UnJunctorNode(Parse(s.substring(JunctorPos+1, s.length()), null, PutMessages), 
                                              Symbols.GetUnJunctor(s.charAt(JunctorPos)));
                }
                catch (IndexOutOfBoundsException e) 
                {
                    throw new SyntaxErrorException(s, "General syntax error", PutMessages);
                }                
            }        
        }
        
        if (s.length() == 1)                 // Astende:Variablen anhängen
            if (Symbols.IsVariableSymbol(s.charAt(0)))
            {
                n = new VariableNode(s.charAt(0));
                
                if (PutMessages)                    
                    AnalyzeMessager.AnalyzeMessage("variable added: "+s);                    
            }
            else
                throw new SyntaxErrorException(s, "General syntax error", PutMessages);
        
        return n;
    }
    
    // Liefert die Stelle, an der die Klammer wieder schliesst
    private static int GetCloseBracketPos(String s, int OpenBracketPos)
    {
        int OpenBrackets = 0;
        for (int i=OpenBracketPos; i<s.length(); i++) 
        {
            if (Symbols.IsOpenBracket(s.charAt(i)))
                OpenBrackets++;
            if (Symbols.IsCloseBracket(s.charAt(i))) 
            {
                OpenBrackets--;
                if (OpenBrackets == 0)
                    return i;
            }
        }
        return -1;
   }
    private static boolean IsInBrackets(String s)
    {
        if (Symbols.IsOpenBracket(s.charAt(0)) && Symbols.IsCloseBracket(s.charAt(s.length()-1)))        
        {
            if (GetCloseBracketPos(s, 0) < s.length()-1)
                return false;            
        }
        else 
            return false;
        
        return true;
    }
    
    private static String RemoveBrackets(String s, boolean PutMessages) throws SyntaxErrorException
    {
        try // Ausdruck aus allfälligen Klammern schälen. Achtung: (p4s)3p1q2(r3p)
        {
            if (s.length() >= 2)            
                while (IsInBrackets(s))
                    s = s.substring(1, s.length()-1);            
        }
        catch (IndexOutOfBoundsException e) {    // Falls s.length() == 0
            throw new SyntaxErrorException(s, "General syntax error", PutMessages);
        }
        return s;
    }
    // Schwächst bindenden Junktor des Ausdrucks s ermitteln
    private static int GetLeastBindingJunctorPos(String s, boolean PutMessages) throws SyntaxErrorException
    {
        int JunctorPos = -1;
        int JunctorBinding = -1;
        int i=0;
        while (i<s.length())        // Hier noch Prioritäten einbringen!
        {
                            //Binäre Junktoren binden links vor rechts, unäre umgekehrt!
           if (Symbols.IsBinJunctorSymbol(s.charAt(i)) && Symbols.GetBinJunctorBinding(Symbols.GetBinJunctor(s.charAt(i))) >= JunctorBinding)
           {
               JunctorPos = i;
               JunctorBinding = Symbols.GetBinJunctorBinding(Symbols.GetBinJunctor(s.charAt(i)));
           }
           if (Symbols.IsUnJunctorSymbol(s.charAt(i)) && Symbols.GetUnJunctorBinding(Symbols.GetUnJunctor(s.charAt(i))) >/*!*/ JunctorBinding) 
           {
               JunctorPos = i;
               JunctorBinding = Symbols.GetUnJunctorBinding(Symbols.GetUnJunctor(s.charAt(i)));
           }
            
            if (Symbols.IsOpenBracket(s.charAt(i))) // Klammern überspringen
            {
                int CloseBracketPos = GetCloseBracketPos(s, i);

                if (CloseBracketPos == -1)
                    throw new SyntaxErrorException(s, "Invalid brackets", PutMessages);
                else
                    i = CloseBracketPos; // Nach der Klammer weiterfahren
            }
            i++;
        }

        return JunctorPos;
    }
}

class SyntaxErrorException extends Exception
{
    SyntaxErrorException(String exp, String m, boolean PutMessages)
    {        
        super("Syntax error : " + m + (PutMessages ? " (see analysis window)" : ""));
        if (PutMessages)
            AnalyzeMessager.AnalyzeMessage("syntax error while processing "+exp);
    }
}
