import java.util.*;
import java.io.*;

/**
 * RuleBaseSystem
 * 
 */
public class RuleBaseSystem {
    static RuleBase rb;
    public static void main(String args[]){
        rb = new RuleBase();
        rb.forwardChain();      
    }
}

/**
 * ���[�L���O��������\���N���X�D
 *
 * 
 */
class WorkingMemory {
    Vector assertions;    

    WorkingMemory(){
        assertions = new Vector();
    }

    /**
     * �}�b�`����A�T�[�V�����ɑ΂���o�C���f�B���O����Ԃ�
     * �i�ċA�I�j
     *
     * @param     �O�������� Vector
     * @return    �o�C���f�B���O��񂪓����Ă��� Vector
     */
    public Vector matchingAssertions(Vector theAntecedents){
        Vector bindings = new Vector();
        return matchable(theAntecedents,0,bindings);
    }

    private Vector matchable(Vector theAntecedents,int n,Vector bindings){
        if(n == theAntecedents.size()){
            return bindings;
        } else if (n == 0){
            boolean success = false;
            for(int i = 0 ; i < assertions.size() ; i++){
                Hashtable binding = new Hashtable();
                if((new Matcher()).matching(
                    (String)theAntecedents.elementAt(n),
                    (String)assertions.elementAt(i),
                    binding)){
                    bindings.addElement(binding);
                    success = true;
                }
            }
            if(success){
                return matchable(theAntecedents, n+1, bindings);
            } else {
                return null;
            }
        } else {
            boolean success = false;
            Vector newBindings = new Vector();
            for(int i = 0 ; i < bindings.size() ; i++){
                for(int j = 0 ; j < assertions.size() ; j++){
                    if((new Matcher()).matching(
                        (String)theAntecedents.elementAt(n),
                        (String)assertions.elementAt(j),
                        (Hashtable)bindings.elementAt(i))){
                        newBindings.addElement(bindings.elementAt(i));
                        success = true;
                    }
                }
            }
            if(success){
                return matchable(theAntecedents,n+1,newBindings);
            } else {
                return null;
            }
        }
    }
    
    /**
     * �A�T�[�V���������[�L���O�������ɉ�����D
     *
     * @param     �A�T�[�V������\�� String
     */
    public void addAssertion(String theAssertion){
        System.out.println("ADD:"+theAssertion);
        assertions.addElement(theAssertion);
    }

    /**
     * �w�肳�ꂽ�A�T�[�V���������łɊ܂܂�Ă��邩�ǂ����𒲂ׂ�D
     *
     * @param     �A�T�[�V������\�� String
     * @return    �܂܂�Ă���� true�C�܂܂�Ă��Ȃ���� false
     */
    public boolean contains(String theAssertion){
        return assertions.contains(theAssertion);
    }

    /**
     * ���[�L���O�������̏����X�g�����O�Ƃ��ĕԂ��D
     *
     * @return    ���[�L���O�������̏���\�� String
     */
    public String toString(){
        return assertions.toString();
    }
    
}

/**
 * ���[���x�[�X��\���N���X�D
 *
 * 
 */
class RuleBase {
    String fileName;
    FileReader f;
    StreamTokenizer st;
    WorkingMemory wm;
    Vector rules;
    
    RuleBase(){
        fileName = "CarShop.data";
        wm = new WorkingMemory();
        wm.addAssertion("my-car is inexpensive");
        wm.addAssertion("my-car has a VTEC engine");
        wm.addAssertion("my-car is stylish");
        wm.addAssertion("my-car has several color models");
        wm.addAssertion("my-car has several seats");
        wm.addAssertion("my-car is a wagon");
        rules = new Vector();
        loadRules(fileName);
    }

    /**
     * �O�������_���s�����߂̃��\�b�h
     *
     */
    public void forwardChain(){
        boolean newAssertionCreated;
        // �V�����A�T�[�V��������������Ȃ��Ȃ�܂ő�����D
        do {
            newAssertionCreated = false;
            for(int i = 0 ; i < rules.size(); i++){
                Rule aRule = (Rule)rules.elementAt(i);
                System.out.println("apply rule:"+aRule.getName());
                Vector antecedents = aRule.getAntecedents();
                String consequent  = aRule.getConsequent();
                //Hashtable bindings = wm.matchingAssertions(antecedents);
                Vector bindings = wm.matchingAssertions(antecedents);
                if(bindings != null){
                    for(int j = 0 ; j < bindings.size() ; j++){
                        //�㌏���C���X�^���V�G�[�V����
                        String newAssertion =
                            instantiate((String)consequent,
                                        (Hashtable)bindings.elementAt(j));
                        //���[�L���O�������[�ɂȂ���ΐ���
                        if(!wm.contains(newAssertion)){
                            System.out.println("Success: "+newAssertion);
                            wm.addAssertion(newAssertion);
                            newAssertionCreated = true;
                        }
                    }
                }
            }
            System.out.println("Working Memory"+wm);
        } while(newAssertionCreated);
        System.out.println("No rule produces a new assertion");
    }

    private String instantiate(String thePattern, Hashtable theBindings){
        String result = new String();
        StringTokenizer st = new StringTokenizer(thePattern);
        for(int i = 0 ; i < st.countTokens();){
            String tmp = st.nextToken();
            if(var(tmp)){
                result = result + " " + (String)theBindings.get(tmp);
            } else {
                result = result + " " + tmp;
            }
        }
        return result.trim();
    }

    private boolean var(String str1){
        // �擪�� ? �Ȃ�ϐ�
        return str1.startsWith("?");
    }

    private void loadRules(String theFileName){
        String line;
        try{
            int token;
            f = new FileReader(theFileName);
            st = new StreamTokenizer(f);
            while((token = st.nextToken())!= StreamTokenizer.TT_EOF){
                switch(token){
                    case StreamTokenizer.TT_WORD:
                        String name = null;
                        Vector antecedents = null;
                        String consequent = null;
                        if("rule".equals(st.sval)){
			    st.nextToken();
//                            if(st.nextToken() == '"'){
                                name = st.sval;
                                st.nextToken();
                                if("if".equals(st.sval)){
                                    antecedents = new Vector();
                                    st.nextToken();
                                    while(!"then".equals(st.sval)){
                                        antecedents.addElement(st.sval);
                                        st.nextToken();
                                    }
                                    if("then".equals(st.sval)){
                                        st.nextToken();
                                        consequent = st.sval;
                                    }
                                }
//                            } 
                        }
			// ���[���̐���
                        rules.addElement(
                            new Rule(name,antecedents,consequent));
                        break;
                    default:
                        System.out.println(token);
                        break;
                }
            }
        } catch(Exception e){
            System.out.println(e);
        }
        for(int i = 0 ; i < rules.size() ; i++){
            System.out.println(((Rule)rules.elementAt(i)).toString());
        }
    }
}

/**
 * ���[����\���N���X�D
 *
 * 
 */
class Rule {
    String name;
    Vector antecedents;
    String consequent;

    Rule(String theName,Vector theAntecedents,String theConsequent){
        this.name = theName;
        this.antecedents = theAntecedents;
        this.consequent = theConsequent;
    }

    /**
     * ���[���̖��O��Ԃ��D
     *
     * @return    ���O��\�� String
     */
    public String getName(){
        return name;
    }

    /**
     * ���[����String�`���ŕԂ�
     *
     * @return    ���[���𐮌`����String
     */
    public String toString(){
        return name+" "+antecedents.toString()+"->"+consequent;
    }

    /**
     * ���[���̑O����Ԃ��D
     *
     * @return    �O����\�� Vector
     */
    public Vector getAntecedents(){
        return antecedents;
    }

    /**
     * ���[���̌㌏��Ԃ��D
     *
     * @return    �㌏��\�� String
     */
    public String getConsequent(){
        return consequent;
    }
    
}

class Matcher {
    StringTokenizer st1;
    StringTokenizer st2;
    Hashtable vars;
    
    Matcher(){
        vars = new Hashtable();
    }

    public boolean matching(String string1,String string2,Hashtable bindings){
        this.vars = bindings;
        return matching(string1,string2);
    }
    
    public boolean matching(String string1,String string2){
        //System.out.println(string1);
        //System.out.println(string2);
        
        // �����Ȃ琬��
        if(string1.equals(string2)) return true;
        
        // �e�X�g�[�N���ɕ�����
        st1 = new StringTokenizer(string1);
        st2 = new StringTokenizer(string2);

        // �����قȂ����玸�s
        if(st1.countTokens() != st2.countTokens()) return false;
                
        // �萔���m
        for(int i = 0 ; i < st1.countTokens();){
            if(!tokenMatching(st1.nextToken(),st2.nextToken())){
                // �g�[�N������ł��}�b�`���O�Ɏ��s�����玸�s
                return false;
            }
        }
        
        // �Ō�܂� O.K. �Ȃ琬��
        return true;
    }

    boolean tokenMatching(String token1,String token2){
        //System.out.println(token1+"<->"+token2);
        if(token1.equals(token2)) return true;
        if( var(token1) && !var(token2)) return varMatching(token1,token2);
        if(!var(token1) &&  var(token2)) return varMatching(token2,token1);
        return false;
    }

    boolean varMatching(String vartoken,String token){
        if(vars.containsKey(vartoken)){
            if(token.equals(vars.get(vartoken))){
                return true;
            } else {
                return false;
            }
        } else {
            vars.put(vartoken,token);
        }
        return true;
    }

    boolean var(String str1){
        // �擪�� ? �Ȃ�ϐ�
        return str1.startsWith("?");
    }

}
