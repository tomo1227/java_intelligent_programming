import java.util.*;
import java.io.*;

public class RuleBaseSystem {
    static RuleBase rb;
    static FileManager fm;
    public static void main(String args[]){
	if(args.length != 1){
	    System.out.println("Usage: %java RuleBaseSystem [query strings]");
	    System.out.println("Example:");
	    System.out.println(" \"?x is b\" and \"?x is c\" are queries");
	    System.out.println("  %java RuleBaseSystem \"?x is b,?x is c\"");
	} else {
	    fm = new FileManager();
	    Vector rules = fm.loadRules("CarShop.data");
	    //Vector rules = fm.loadRules("AnimalWorld.data");
	    Vector wm    = fm.loadWm("CarShopWm.data");
	    //Vector wm    = fm.loadWm("AnimalWorldWm.data");
	    rb = new RuleBase(rules,wm);
	    StringTokenizer st = new StringTokenizer(args[0],",");
	    Vector queries = new Vector();
	    for(int i = 0 ; i < st.countTokens();){
		queries.addElement(st.nextToken());
	    }
	    rb.backwardChain(queries);
	}
    }
}
    
class RuleBase implements Serializable{
    String fileName;
    Vector wm;
    Vector rules;
    
    RuleBase(Vector theRules,Vector theWm){
	wm = theWm;
	rules = theRules;
    }

    public void setWm(Vector theWm){
	wm = theWm;
    }

    public void setRules(Vector theRules){
	rules = theRules;
    }

    public void backwardChain(Vector hypothesis){
	System.out.println("Hypothesis:"+hypothesis);
	Vector orgQueries = (Vector)hypothesis.clone();
	Hashtable binding = new Hashtable();
	if(matchingPatterns(hypothesis,binding)){
	    System.out.println("Yes");
	    System.out.println(binding);
	    // �ŏI�I�Ȍ��ʂ���̃N�F���[�ɑ�����ĕ\������
	    for(int i = 0 ; i < orgQueries.size() ; i++){
		String aQuery = (String)orgQueries.elementAt(i);
		String anAnswer = instantiate(aQuery,binding);
		System.out.println("Query: "+aQuery);
		System.out.println("Answer:"+anAnswer);
	    }
	} else {
	    System.out.println("No");
	}
    }

    /**
     * �}�b�`���郏�[�L���O�������̃A�T�[�V�����ƃ��[���̌㌏
     * �ɑ΂���o�C���f�B���O����Ԃ�
     */
    private boolean matchingPatterns(Vector thePatterns,Hashtable theBinding){
        String firstPattern;
	if(thePatterns.size() == 1){
	    firstPattern = (String)thePatterns.elementAt(0);
	    if(matchingPatternOne(firstPattern,theBinding,0) != -1){
	      return true;
	    } else {
	      return false;
	    }
	} else {
	    firstPattern = (String)thePatterns.elementAt(0);
	    thePatterns.removeElementAt(0);

	    int cPoint = 0;
	    while(cPoint < wm.size() + rules.size()){
	      // ���̃o�C���f�B���O������Ă���
	      Hashtable orgBinding = new Hashtable();
	      for(Enumeration e = theBinding.keys() ; e.hasMoreElements();){
		String key = (String)e.nextElement();
		String value = (String)theBinding.get(key);
		orgBinding.put(key,value);
	      }
	      int tmpPoint = matchingPatternOne(firstPattern,theBinding,cPoint);
	      if(tmpPoint != -1){
		System.out.println("Success:"+firstPattern);
		if(matchingPatterns(thePatterns,theBinding)){
		  //����  
		  return true;
		} else {
                  //���s
		  //choice�|�C���g��i�߂�
		  cPoint = tmpPoint;
		  // ���s�����̂Ńo�C���f�B���O��߂�
		  theBinding.clear();
		  for(Enumeration e=orgBinding.keys();e.hasMoreElements();){
		    String key = (String)e.nextElement();
		    String value = (String)orgBinding.get(key);
		    theBinding.put(key,value);
		  }
		}
	      } else {
		// ���s�����̂Ńo�C���f�B���O��߂�
		theBinding.clear();
		for(Enumeration e=orgBinding.keys();e.hasMoreElements();){
		  String key = (String)e.nextElement();
		  String value = (String)orgBinding.get(key);
		  theBinding.put(key,value);
		}
		return false;
	      }
	    }
	    return false;
	    /*
	    if(matchingPatternOne(firstPattern,theBinding)){
	      return matchingPatterns(thePatterns,theBinding);
	    } else {
	      return false;
	    }
	    */
	}
    }

    private int matchingPatternOne(String thePattern,Hashtable theBinding,int cPoint){
      if(cPoint < wm.size() ){
	// WME(Working Memory Elements) �� Unify ���Ă݂�D
	for(int i = cPoint ; i < wm.size() ; i++){
	    if((new Unifier()).unify(thePattern,
				     (String)wm.elementAt(i),
				     theBinding)){
		System.out.println("Success WM");
		System.out.println((String)wm.elementAt(i)+" <=> "+thePattern);
		return i+1;
	    }
	}
      }
      if(cPoint < wm.size() + rules.size()){
	// Rule�� Unify ���Ă݂�D
 	for(int i = cPoint ; i < rules.size() ; i++){
 	    Rule aRule = rename((Rule)rules.elementAt(i));
	    // ���̃o�C���f�B���O������Ă����D
	    Hashtable orgBinding = new Hashtable();
	    for(Enumeration e = theBinding.keys() ; e.hasMoreElements();){
		String key = (String)e.nextElement();
		String value = (String)theBinding.get(key);
		orgBinding.put(key,value);
	    }
 	    if((new Unifier()).unify(thePattern,
 				     (String)aRule.getConsequent(),
 				     theBinding)){
		System.out.println("Success RULE");
		System.out.println("Rule:"+aRule+" <=> "+thePattern);
 		// �����backwardChaining
 		Vector newPatterns = (Vector)aRule.getAntecedents();
		if(matchingPatterns(newPatterns,theBinding)){
		    return wm.size()+i+1;
		} else {
		    // ���s�����猳�ɖ߂��D
		    theBinding.clear();
		    for(Enumeration e=orgBinding.keys();e.hasMoreElements();){
			String key = (String)e.nextElement();
			String value = (String)orgBinding.get(key);
			theBinding.put(key,value);
		    }
		}
	    }
 	}
      }
      return -1;
    }

    /**
     * �^����ꂽ���[���̕ϐ������l�[���������[���̃R�s�[��Ԃ��D
     * @param   �ϐ������l�[�����������[��
     * @return  �ϐ������l�[�����ꂽ���[���̃R�s�[��Ԃ��D
     */
    int uniqueNum = 0;
    private Rule rename(Rule theRule){
	Rule newRule = theRule.getRenamedRule(uniqueNum);
	uniqueNum = uniqueNum + 1;
	return newRule;
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
}

class FileManager {
    FileReader f;
    StreamTokenizer st;
    public Vector loadRules(String theFileName){
	Vector rules = new Vector();
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
			}
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
	return rules;
    }

    public Vector loadWm(String theFileName){
	Vector wm = new Vector();
	String line;
	try{
	    int token;
	    f = new FileReader(theFileName);
	    st = new StreamTokenizer(f);
	    st.eolIsSignificant(true);
	    st.wordChars('\'','\'');
	    while((token = st.nextToken())!= StreamTokenizer.TT_EOF){
		line = new String();
		while( token != StreamTokenizer.TT_EOL){
		    line = line + st.sval + " ";
		    token = st.nextToken();
		}
		wm.addElement(line.trim());
	    }
	} catch(Exception e){
	    System.out.println(e);
	}
	return wm;
    }
}


/**
 * ���[����\���N���X�D
 */
class Rule implements Serializable{
    String name;
    Vector antecedents;
    String consequent;

    Rule(String theName,Vector theAntecedents,String theConsequent){
	this.name = theName;
	this.antecedents = theAntecedents;
	this.consequent = theConsequent;
    }

    public Rule getRenamedRule(int uniqueNum){
	Vector vars = new Vector();
	for(int i = 0 ; i < antecedents.size() ; i++){
	    String antecedent = (String)this.antecedents.elementAt(i);
	    vars = getVars(antecedent,vars);
	}
	vars = getVars(this.consequent,vars);
	Hashtable renamedVarsTable = makeRenamedVarsTable(vars,uniqueNum);

	Vector newAntecedents = new Vector();
	for(int i = 0 ; i < antecedents.size() ; i++){
	    String newAntecedent =
		renameVars((String)antecedents.elementAt(i),
			   renamedVarsTable);
	    newAntecedents.addElement(newAntecedent);
	}
	String newConsequent = renameVars(consequent,
					  renamedVarsTable);

	Rule newRule = new Rule(name,newAntecedents,newConsequent);
	return newRule;
    }

    private Vector getVars(String thePattern,Vector vars){
	StringTokenizer st = new StringTokenizer(thePattern);
	for(int i = 0 ; i < st.countTokens();){
	    String tmp = st.nextToken();
	    if(var(tmp)){
		vars.addElement(tmp);
	    }
	}
	return vars;
    }

    private Hashtable makeRenamedVarsTable(Vector vars,int uniqueNum){
	Hashtable result = new Hashtable();
	for(int i = 0 ; i < vars.size() ; i++){
	    String newVar =
		(String)vars.elementAt(i) + uniqueNum;
	    result.put((String)vars.elementAt(i),newVar);
	}
	return result;
    }
    
    private String renameVars(String thePattern,
			      Hashtable renamedVarsTable){
	String result = new String();
	StringTokenizer st = new StringTokenizer(thePattern);
	for(int i = 0 ; i < st.countTokens();){
	    String tmp = st.nextToken();
	    if(var(tmp)){
		result = result + " " +
		    (String)renamedVarsTable.get(tmp);
	    } else {
		result = result + " " + tmp;
	    }
	}
	return result.trim();
    }

    private boolean var(String str){
	// �擪�� ? �Ȃ�ϐ�
	return str.startsWith("?");
    }

    public String getName(){
	return name;
    }

    public String toString(){
	return name+" "+antecedents.toString()+"->"+consequent;
    }

    public Vector getAntecedents(){
	return antecedents;
    }

    public String getConsequent(){
	return consequent;
    }
}

class Unifier {
    StringTokenizer st1;
    String buffer1[];    
    StringTokenizer st2;
    String buffer2[];
    Hashtable vars;
    
    Unifier(){
	//vars = new Hashtable();
    }

    public boolean unify(String string1,String string2,Hashtable theBindings){
	Hashtable orgBindings = new Hashtable();
	for(Enumeration e = theBindings.keys() ; e.hasMoreElements();){
	    String key = (String)e.nextElement();
	    String value = (String)theBindings.get(key);
	    orgBindings.put(key,value);
	}
	this.vars = theBindings;
	if(unify(string1,string2)){
	    return true;
	} else {
	    // ���s�����猳�ɖ߂��D
	    theBindings.clear();
	    for(Enumeration e = orgBindings.keys() ; e.hasMoreElements();){
		String key = (String)e.nextElement();
		String value = (String)orgBindings.get(key);
		theBindings.put(key,value);
	    }
	    return false;
	}
    }

    public boolean unify(String string1,String string2){
	// �����Ȃ琬��
	if(string1.equals(string2)) return true;
	
	// �e�X�g�[�N���ɕ�����
	st1 = new StringTokenizer(string1);
	st2 = new StringTokenizer(string2);
	
	// �����قȂ����玸�s
	if(st1.countTokens() != st2.countTokens()) return false;
	
	// �萔���m
	int length = st1.countTokens();
	buffer1 = new String[length];
	buffer2 = new String[length];
	for(int i = 0 ; i < length; i++){
	    buffer1[i] = st1.nextToken();
	    buffer2[i] = st2.nextToken();
	}

	// �����l�Ƃ��ăo�C���f�B���O���^�����Ă�����
	if(this.vars.size() != 0){
	    for(Enumeration keys = vars.keys(); keys.hasMoreElements();){
		String key = (String)keys.nextElement();
		String value = (String)vars.get(key);
		replaceBuffer(key,value);
	    }
	}

	for(int i = 0 ; i < length ; i++){
	    if(!tokenMatching(buffer1[i],buffer2[i])){
		return false;
	    }
	}

	return true;
    }

    boolean tokenMatching(String token1,String token2){
	if(token1.equals(token2)) return true;
	if( var(token1) && !var(token2)) return varMatching(token1,token2);
	if(!var(token1) &&  var(token2)) return varMatching(token2,token1);
	if( var(token1) &&  var(token2)) return varMatching(token1,token2);
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
	    replaceBuffer(vartoken,token);
	    if(vars.contains(vartoken)){
		replaceBindings(vartoken,token);
	    }
	    vars.put(vartoken,token);
	}
	return true;
    }

    void replaceBuffer(String preString,String postString){
	for(int i = 0 ; i < buffer1.length ; i++){
	    if(preString.equals(buffer1[i])){
		buffer1[i] = postString;
	    }
	    if(preString.equals(buffer2[i])){
		buffer2[i] = postString;
	    }
	}
    }
    
    void replaceBindings(String preString,String postString){
	Enumeration keys;
	for(keys = vars.keys(); keys.hasMoreElements();){
	    String key = (String)keys.nextElement();
	    if(preString.equals(vars.get(key))){
		vars.put(key,postString);
	    }
	}
    }
    
    boolean var(String str1){
	// �擪�� ? �Ȃ�ϐ�
	return str1.startsWith("?");
    }
}
