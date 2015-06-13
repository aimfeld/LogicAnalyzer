/*
 * SymbolNode.java
 *
 * Created on 12. Januar 2004, 16:38
 */

/**
 *
 * @author  Adrian Imfeld
 */
import java.util.*;  // Vector

/**
 * Ein SymbolNode repräsentiert einen Junktor oder eine Variable.
 * Bei binären Junktoren werden die Bezugsvariablen als LeftChild und
 * RightChild, bei unären Junktoren als RightChild angehängt.
 */
public abstract class SymbolNode 
{    
    public static final int NTVariable = 0;     // Die verschiedenen Knoten-Typen 
    public static final int NTUnJunctor = 1;
    public static final int NTBinJunctor = 2;
    
    public boolean[] Values = null ; // Hier werden die versch. Werte einer Wahrheitstafelspalte gespeichert
        
    public abstract char GetSymbol();
    public abstract int GetNodeType();
    public abstract boolean IsJunctor();
    public abstract boolean IsTautology();    
    public abstract boolean IsAntilogy();
                    
    public void CalculateTruthTable(boolean PutMessages) 
    {
        int Rows = GetRowCount();
        SetVariableValues(Rows, new Vector());   // Variablenwerte setzen
        Calculate(Rows, PutMessages);                          // Junktor-Werte berechnen
    }
    
    public int GetRowCount()
    {
        return 2 << (GetVariableCount(new StringBuffer("")) - 1);
    }
    
    public int GetVariableCount(StringBuffer s)
    {
        if (GetNodeType() == NTVariable && s.toString().indexOf(GetSymbol()) == -1)
        {
            s.append(GetSymbol());
            return 1;
        }
        
        int cnt = 0;
        if (GetNodeType() == NTUnJunctor)            
            cnt += ((UnJunctorNode)this).GetRightChild().GetVariableCount(s);
        if (GetNodeType() == NTBinJunctor)
        {
            cnt += ((BinJunctorNode)this).GetLeftChild().GetVariableCount(s);
            cnt += ((BinJunctorNode)this).GetRightChild().GetVariableCount(s);
        }
        return cnt;
    }
        
    private String SetBrackets(String s)
    {
        return Symbols.OpenBracket + s + Symbols.CloseBracket;
    }
    
    public String GetExpression()
    {
        String s = "";
        // Falls ein ChildNode schwächer bindet, müssen Klammern um diesen gesetzt werden
        if (GetNodeType() == NTBinJunctor)            
        {
            BinJunctorNode bjn = (BinJunctorNode)this;
            s = bjn.GetLeftChild().GetExpression();
            if (bjn.GetLeftChild().IsJunctor() &&                  
                bjn.BindsStrongerThan((JunctorNode)bjn.GetLeftChild(), true))
                s = SetBrackets(s);
                       
            s += GetSymbol();
            
            if (bjn.GetRightChild().IsJunctor() && 
                bjn.BindsStrongerThan((JunctorNode)bjn.GetRightChild(), false))
                s += SetBrackets(bjn.GetRightChild().GetExpression());
            else 
                s += bjn.GetRightChild().GetExpression();
        }
                
        if (GetNodeType() == NTUnJunctor) 
        {
            UnJunctorNode ujn = (UnJunctorNode)this;
            s = ujn.GetRightChild().GetExpression();
            if (ujn.GetRightChild().IsJunctor() && 
                ujn.BindsStrongerThan((JunctorNode)ujn.GetRightChild()))
                s = SetBrackets(s);
            
            s = GetSymbol() + s;
        }   
        
        if (GetNodeType() == NTVariable)
            s =  ""+GetSymbol();    //Kleiner Trick, weils kein String(char) Konstruktor gibt
        
        return s; 
    }
    
    /*
     * Setzt die Wahrheitswerte der Variablen des Baumes.
     * Muss VOR Calculate() aufgerufen werden!
     */
    private void SetVariableValues(int Rows, Vector vadded)
    {           
        if (GetNodeType() == NTBinJunctor) 
        {   // Hier wird RightChild zuerst gesetzt, damits für die Tabellensortierung einfach wird!
            // Die Wertzuweisung ist so menschlich intuitiver
            ((BinJunctorNode)this).GetRightChild().SetVariableValues(Rows, vadded);
            ((BinJunctorNode)this).GetLeftChild().SetVariableValues(Rows, vadded);            
        }
        
        if (GetNodeType() == NTUnJunctor) 
        {
            ((UnJunctorNode)this).GetRightChild().SetVariableValues(Rows, vadded);
        }
        
        if (GetNodeType() == SymbolNode.NTVariable) 
        {
            Values = new boolean[Rows];
            
            int AddedPos = -1;
            for (int i=0; i<vadded.size(); i++)
                if (((SymbolNode)vadded.get(i)).GetExpression().equals(GetExpression())) 
                {
                    AddedPos = i;
                    break;
                }
            
            int StepSize = 1 << vadded.size();
            if (AddedPos == -1) 
            {
                
                for (int i2=0; i2<Rows; i2++)
                    Values[i2] = ((i2 / StepSize) % 2 == 0);
                
                vadded.add(this);
                ((VariableNode)this).StepSize = StepSize; // Für die Sortierung der Tabelle
            }
            else
            {
                System.arraycopy(((SymbolNode)vadded.get(AddedPos)).Values, 0, Values, 0, Rows);
                ((VariableNode)this).StepSize = ((VariableNode)vadded.get(AddedPos)).StepSize;
            }
        }
    }
    
    /**
     * Berechnet alle möglichen Wahrheitswertkombinationen.
     * Der Baum muss komplett sein, d.h. alle Äste mussen mit
     * einer Variablen abgeschlossen sein, welcher schon
     * sämtlich Wahrheitwerte zugewiesen wurden.
     * Muss NACH SetVariableValues aufgerufen werden!
     */    
    private void Calculate(int Rows, boolean PutMessages)
    {
        if (IsJunctor())
        {
            Values = new boolean[Rows];
            if (GetNodeType() == NTBinJunctor)
            {
                BinJunctorNode bjn = (BinJunctorNode)this;
                bjn.GetLeftChild().Calculate(Rows, PutMessages);
                bjn.GetRightChild().Calculate(Rows, PutMessages);
                if (PutMessages)
                    AnalyzeMessager.AnalyzeMessage("calculating "+GetExpression()+" ...");
                for (int i=0; i<Rows; i++)       // Spalte berechnen
                    Values[i] = Symbols.CalculateBinJunctorValue(bjn.GetLeftChild().Values[i], bjn.GetRightChild().Values[i], bjn.GetJunctor());                
            }
            
            if (GetNodeType() == NTUnJunctor)
            {
                UnJunctorNode ujn = (UnJunctorNode)this;
                ujn.GetRightChild().Calculate(Rows, PutMessages);
                if (PutMessages)
                    AnalyzeMessager.AnalyzeMessage("calculating"+GetExpression()+" ...");
                for (int i=0; i<Rows; i++)       // Spalte berechnen                                    
                    Values[i] = Symbols.CalculateUnJunctorValue(ujn.GetRightChild().Values[i], ujn.GetJunctor());
                
            }
        }        
    }
    
    /**
     * Hilfsfunktion für die Tabellendatstellung der Knoten.
     * Fügt die Knoten in den Vector v ein, so dass die Operationsreihenfolge
     * korrekt wiedergegeben ist: An erster Position steht die zuerst 
     * auszuführende Operation. Redundante Knoten werden weggelassen.
     */
    public void ListNode(Vector v)
    {
        for (int i=0; i<v.size(); i++)  // Redundante Knoten weglassen
            if (((SymbolNode)v.get(i)).GetExpression().equals(GetExpression()))
                return;
        
        if (GetNodeType() == NTVariable)
            v.add(this);
        
        if (GetNodeType() == NTBinJunctor)
        { 
            ((BinJunctorNode)this).GetLeftChild().ListNode(v);
            ((BinJunctorNode)this).GetRightChild().ListNode(v);
            v.add(this);
        }
        
        if (GetNodeType() == NTUnJunctor)
        { 
            
            ((UnJunctorNode)this).GetRightChild().ListNode(v);
            v.add(this);
        }
    }    
}



class VariableNode extends SymbolNode
{
    private char Symbol = Symbols.UndefinedSymbol;
    public int StepSize = -1;      // Speichert die Intervalle zwischen Wechsel von wahr zu falsch in der Wahrheitstabelle
    
    public VariableNode(char Symbol)
    {
        this.Symbol = Symbol;
    }
    
    public char GetSymbol() 
    {
        return Symbol;
    }
    
    public int GetNodeType() 
    {
        return NTVariable;
    }
    
    public boolean IsJunctor() 
    {
        return false;
    }
    
    public boolean IsTautology() 
    {
        return false;
    }
    
    public boolean IsAntilogy() 
    {
        return false;
    }    
}

abstract class JunctorNode extends SymbolNode
{
    protected int Junctor = Symbols.UndefinedJunctor;
    
    public abstract char GetSymbol(); 
    public abstract int GetBinding();
    
    public int GetJunctor()
    {
        return Junctor;
    }   
    
    public boolean IsJunctor() 
    {
        return true;
    }
    
    public boolean IsTautology() 
    {            
        if (Values != null)
            for (int i=0; i<Values.length; i++)
                if (Values[i] == false)
                    return false;
        
        return true;    
    }
    
    public boolean IsAntilogy()
    {
        if (Values != null)
            for (int i=0; i<Values.length; i++)
                if (Values[i] == true)
                    return false;
        
        return true;
    }
}

class BinJunctorNode extends JunctorNode
{
    private SymbolNode LeftChild, RightChild;
    
    public BinJunctorNode(SymbolNode LeftChild, SymbolNode RightChild, int Junctor)
    {
        this.LeftChild = LeftChild;
        this.RightChild = RightChild;
        this.Junctor = Junctor;
    }
    
    public boolean BindsStrongerThan(JunctorNode jn, boolean ArgIsBefore) 
    {
        if (ArgIsBefore)
            return GetBinding() < jn.GetBinding();
        else
            return GetBinding() <= jn.GetBinding();
    }
    
    public char GetSymbol()
    {
        return Symbols.GetBinJunctorSymbol(Junctor);
    }
       
    public int GetBinding() 
    {
        return Symbols.GetBinJunctorBinding(Junctor);
    }
    
    public int GetNodeType() 
    {
        return NTBinJunctor;
    }
    
    public SymbolNode GetLeftChild()
    {
        return LeftChild;        
    }
    
    public SymbolNode GetRightChild()
    {
        return RightChild;        
    }
}

class UnJunctorNode extends JunctorNode
{
    private SymbolNode RightChild;
    
    public UnJunctorNode(SymbolNode RightChild, int Junctor)
    {
        this.RightChild = RightChild;
        this.Junctor = Junctor;
    }
    
    public boolean BindsStrongerThan(JunctorNode jn) 
    {
        return GetBinding() < jn.GetBinding();
    }
    
    public char GetSymbol()
    {
        return Symbols.GetUnJunctorSymbol(Junctor);
    }
        
    public int GetBinding() 
    {
        return Symbols.GetUnJunctorBinding(Junctor);
    }
    
    public int GetNodeType() 
    {
        return NTUnJunctor;
    }
   
    public SymbolNode GetRightChild()
    {
        return RightChild;        
    }        
}

class SymbolNodeComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        SymbolNode sn1 = (SymbolNode)o1;
        SymbolNode sn2 = (SymbolNode)o2;
        if (sn1.GetNodeType() == SymbolNode.NTVariable && sn2.GetNodeType() != SymbolNode.NTVariable)
            return -1;
        else if (sn2.GetNodeType() == SymbolNode.NTVariable && sn1.GetNodeType() != SymbolNode.NTVariable)
            return 1;
        else if (sn1.GetNodeType() == SymbolNode.NTVariable && sn2.GetNodeType() == SymbolNode.NTVariable)
            return ((VariableNode)sn1).StepSize > ((VariableNode)sn2).StepSize ? -1 : 1;
        else
            return 0;
    }
}
