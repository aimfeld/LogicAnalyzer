/*
 * Symbols.java
 *
 * Created on 12. Januar 2004, 16:14
 */

/**
 *
 * @author  Adrian Imfeld
 */
import java.awt.Font;

public class Symbols  
{
    public static final char OpenBracket = '(';
    public static final char CloseBracket = ')';
    
    public static final char UndefinedSymbol ='\n';
    public static final int UndefinedJunctor = -1;
    
    // Binäre Junktor Indizes
    public static final int BJKonjunktion =    0;          //BJ=Binärer Junktor
    public static final int BJPostsektor =     1;
    public static final int BJPraesektor =     2;
    public static final int BJRejektion =      3;
    public static final int BJDisjunktion =    4;
    public static final int BJReplikation =    5;
    public static final int BJKonditional =    6;
    public static final int BJSheffer =        7;
    public static final int BJBikonditional =  8;
    public static final int BJKontravalenz =   9;
    public static final int BJPraepensor =     10;
    public static final int BJPraenonpensor =  11;
    public static final int BJPostpensor =     12;
    public static final int BJPostnonpensor =  13;
    public static final int BJTautologator =   14;
    public static final int BJAntilogator =    15;
    public static final int BJCount =          16;        // Anzahl binärer Junktoren

    // Unäre Junktor Indizes (Es hat nur einen, die Negation)
    public static final int UJNegation =       0;         // UJ=Unärer Junktor
    public static final int UJCount =          1;         // Anzahl unärer Junktoren
    
    // Bindungsstärken (1 = hoch)
    private static final int[] BJBinding = {2, 4, 4, 4, 3, 5, 5, 4, 6, 6, 4, 4, 4, 4, 4, 4}; // Zugriff über Junktorindex   
    private static final int[] UJBinding = {1}; // Zugriff über Junktorindex
    private static final int MinBinding = 1;
    private static final int MaxBinding = 6;

    // Definition der Junktor-Operationen (Bedeutungen der Junktoren))
    private static final boolean[][] BinJunctorDefs = // Zugriff über Junktorindex
    {{true , false, false, false},          // Konjunktion
     {false, true , false, false},          // Postsektor
     {false, false, true , false},          // Präsektor
     {false, false, false, true },          // Rejektion
     {true , true , true , false},          // Disjunktion
     {true , true , false, true },          // Replikation
     {true , false, true , true },          // Konditional
     {false, true , true , true },          // Sheffer-Strich
     {true , false, false, true },          // Bikonditional
     {false, true , true , false},          // Kontravalenz
     {true , true , false, false},          // Präpensor
     {false, false, true , true },          // Pränonpensor
     {true , false, true , false},          // Postpensor
     {false, true , false, true },          // Postnonpensor
     {true , true , true , true },          // Tautologator
     {false, false, false, false},          // Antilogator
    };
    
    // Std(Standard)-Junktorsymbole, falls LogicFont nicht geladem werden kann
    private static final String BinJunctors = "\u0600\u0601\u0602\u0603\u0604\u0605\u0606\u0607\u0608\u0609\u060A\u060B\u060C\u060D\u060E\u060F"; // Zugriff über Junktorindex
    private static final String StdBinJunctors = "\u0668\u2560\u2563\u2020\u0667\u2190\u2192\u007C\u2194\u256B\u2518\u2510\u2514\u250C\u252C\u2534"; // Zugriff über Junktorindex
    private static final String UnJunctors = "\u00AC";                         // Unäre Junktoren (\u0610 does not work properly, use standard
    private static final String StdUnJunctors = "\u00AC";                         // Unäre Junktoren
    
    // Namen der Junktoren, zB für Tooltips
    private static final String[] BinJunctorNames = // Zugriff über Junktorindex
    {"Conjunction (and)", "Postsector", "Presector", "Rejection", "Disjunction (or)", "Replication", "Conditional (if then)",
     "Sheffer-stroke", "Biconditional (if and only if)", "Exclusive or", "Prepensor", "Prenonpensor", "Postpensor", 
     "Postnonpensor", "Tautology", "Contradiction"};
         
    private static final String[] UnJunctorNames =  {"Negation (not)"}; // Zugriff über Junktorindex
    
    // Tastatureingabe der Junktoren. (Dürfen einander nicht in die Quere kommen, also zB. nicht "->" und ">")
    public static String[][] BinJunctorCompletion = // Zugriff über Junktorindex
    {
        {"and"},
        {">-"},
        {"-<"},
        {},
        {"or"},
        {"<-", "<="},
        {"->", "=>"},
        {},
        {"<>"},
        {"><", "xor"},
        {},
        {},
        {},
        {},
        {},
        {}
    };
    
    public static String[][] UnJunctorCompletion = // Zugriff über Junktorindex
    {
        {"not", "~", "¬", "!"}
    };
        
    public static final String True = "1";
    public static final String False = "0";
    
    private static final boolean LogicFontInstalled = new Font("LogicFont", Font.PLAIN, 12).getFontName().equals("dialog") ? false : true;
    
    public static boolean IsOpenBracket(char ch)
    {
        return ch == OpenBracket;
    }
   
    public static boolean IsCloseBracket(char ch)
    {
        return ch == CloseBracket;
    }
    
    public static boolean IsBinJunctorSymbol(char ch)
    {
        return IsLogicFontInstalled() ? BinJunctors.indexOf(ch) != -1 : StdBinJunctors.indexOf(ch) != -1;
    }
    
    public static boolean IsUnJunctorSymbol(char ch)
    {
        return IsLogicFontInstalled() ? UnJunctors.indexOf(ch) != -1 : StdUnJunctors.indexOf(ch) != -1;
    }
    
    public static boolean IsJunctorSymbol(char ch)
    {
        return IsBinJunctorSymbol(ch) || IsUnJunctorSymbol(ch);
    }

    public static int GetBinJunctor(char Symbol)
    {
        return IsLogicFontInstalled() ? BinJunctors.indexOf(Symbol) : StdBinJunctors.indexOf(Symbol);
    }
    
    public static int GetUnJunctor(char Symbol)
    {
        return IsLogicFontInstalled() ? UnJunctors.indexOf(Symbol) : StdUnJunctors.indexOf(Symbol);
    }
    
    public static char GetBinJunctorSymbol(int Junctor)
    {
        if ((Junctor >= 0) && (Junctor < BJCount))
            return IsLogicFontInstalled() ? BinJunctors.charAt(Junctor) : StdBinJunctors.charAt(Junctor);
        else
            return UndefinedSymbol;
    }
    
    public static char GetUnJunctorSymbol(int Junctor)
    {
        if ((Junctor >= 0) && (Junctor < UJCount))
            return IsLogicFontInstalled() ? UnJunctors.charAt(Junctor) : StdUnJunctors.charAt(Junctor);
        else
            return UndefinedSymbol;        
    }
    
    public static int GetBinJunctorBinding(int Junctor)
    {
        if ((Junctor >= 0) && (Junctor < BJCount))
            return BJBinding[Junctor];
        else
            return -1;
    }
    
    public static int GetUnJunctorBinding(int Junctor)
    {
        if ((Junctor >= 0) && (Junctor < UJCount))
            return UJBinding[Junctor];
        else
            return -1;
    }
    
    public static boolean IsVariableSymbol(char ch)
    {
        return (GetBinJunctor(ch) == -1 && GetUnJunctor(ch) == -1 &&
                ch != OpenBracket && ch != CloseBracket);
    }
    
    public static boolean CalculateBinJunctorValue(boolean v1, boolean v2, int Junctor)
    {
        if (v1 && v2)
            return BinJunctorDefs[Junctor][0];
        else if (v1 && !v2)
            return BinJunctorDefs[Junctor][1];
        else if (!v1 && v2)
            return BinJunctorDefs[Junctor][2];
        else
            return BinJunctorDefs[Junctor][3];
    }
    
    public static boolean CalculateUnJunctorValue(boolean v, int Junctor)
    {
        if (Junctor == UJNegation) 
            return !v;
        else
            return false;   // Kommt nicht vor
    }    
    
    public static String GetBinJunctorDefString(int Junctor)
    {
        StringBuffer sb = new StringBuffer(4);
        for (int i=0; i<4; i++)
            sb.append(BinJunctorDefs[Junctor][i] ? True : False);
        return sb.toString();
    }
    
    public static String GetBinJunctorName(int Junctor)
    {
        return BinJunctorNames[Junctor];
    }
    
    public static String GetUnJunctorName(int Junctor)
    {
        return UnJunctorNames[Junctor];
    }
    
    public static boolean IsLogicFontInstalled()
    {
        return LogicFontInstalled;
    }
}
