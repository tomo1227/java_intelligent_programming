import java.util.*;

/***
 * �Ӗ��l�b�g���[�N (Semantic Net)
 *
 */
public class SemanticNet {
    Vector links;
    Vector nodes;
    Hashtable nodesNameTable;
    
    SemanticNet(){
	links = new Vector();
	nodes = new Vector();
	nodesNameTable = new Hashtable();
    }

    public void query(Vector theQueries){
	System.out.println("*** Query ***");
	for(int i = 0 ; i < theQueries.size() ; i++){
	    System.out.println(((Link)theQueries.elementAt(i)).toString());
	}
	System.out.println((doQuery(theQueries)).toString());
    }

    public Vector doQuery(Vector theQueries){
	Vector bindingsList = new Vector();
	for(int i = 0 ; i < theQueries.size() ; i++){
	    Link theQuery = (Link)theQueries.elementAt(i);
	    Vector bindings = queryLink(theQuery);
	    if(bindings.size() != 0){
		bindingsList.addElement(bindings);
	    } else {
		//���s�����Ƃ�
		return (new Vector());
	    }
	}
	return join(bindingsList);
    }

    public Vector queryLink(Link theQuery){
	Vector bindings = new Vector();
	for(int i = 0 ; i < links.size() ; i++){
	    Link theLink = (Link)links.elementAt(i);
	    Hashtable binding = new Hashtable();
	    String theQueryString = theQuery.getFullName();
	    String theLinkString  = theLink.getFullName();
	    if((new Matcher()).
	       matching(theQueryString,theLinkString,binding)){
		bindings.addElement(binding);
	    }
	}
	return bindings;
    }

    public Vector join(Vector theBindingsList){
	int size = theBindingsList.size();
	switch(size){
	    case 0:
		// ���s���Ă��鎞�H
		break;
	    case 1:
		return (Vector)theBindingsList.elementAt(0);
	    case 2:
		Vector bindings1 = (Vector)theBindingsList.elementAt(0);
		Vector bindings2 = (Vector)theBindingsList.elementAt(1);
		return joinBindings(bindings1,bindings2);
	    default:
		bindings1 = (Vector)theBindingsList.elementAt(0);
		theBindingsList.removeElement(bindings1);
		bindings2 = join(theBindingsList);
		return joinBindings(bindings1,bindings2);
	}
	// �_�~�[
	return (Vector)null;
    }

    public Vector joinBindings(Vector theBindings1,Vector theBindings2){
	Vector resultBindings = new Vector();
	for(int i = 0 ; i < theBindings1.size() ; i++){
	    Hashtable theBinding1 = (Hashtable)theBindings1.elementAt(i);
	    for(int j = 0 ; j < theBindings2.size() ; j++){
		Hashtable theBinding2 = (Hashtable)theBindings2.elementAt(j);
		Hashtable resultBinding =
		    joinBinding(theBinding1,theBinding2);
		if(resultBinding.size()!=0){
		    resultBindings.addElement(resultBinding);
		}
	    }
	}
	return resultBindings;
    }

    public Hashtable joinBinding(Hashtable theBinding1,Hashtable theBinding2){
	Hashtable resultBinding = new Hashtable();
	//System.out.println(theBinding1.toString() + "<->" + theBinding2.toString());
	// theBinding1 �� key & value �����ׂăR�s�[
	for(Enumeration e = theBinding1.keys() ; e.hasMoreElements();){
	    String key = (String)e.nextElement();
	    String value = (String)theBinding1.get(key);
	    resultBinding.put(key,value);
	}
	// theBinding2 �� key & value �����čs���C�������������玸�s
	for(Enumeration e = theBinding2.keys() ; e.hasMoreElements();){
	    String key = (String)e.nextElement();
	    String value2 = (String)theBinding2.get(key);
	    if(resultBinding.containsKey(key)){
		String value1 = (String)resultBinding.get(key);
		//System.out.println("=>"+value1 + "<->" + value2);
		if(!value2.equals(value1)){
		    resultBinding.clear();
		    break;
		}
	    }
	    resultBinding.put(key,value2);
	}
	return resultBinding;
    }

    /***
     * ��: Ito  =is-a=>  NIT-student:
     *     tail : Ito,
     *     head : NIT-student,
     *     label: is-a.
     */
    public void addLink(Link theLink){
	Node tail = theLink.getTail();
	Node head = theLink.getHead();
	links.addElement(theLink);

	// �����̌p��
 	if("is-a".equals(theLink.getLabel())){
 	    // head �̂��ׂẴ����N�� is-a �����ǂ��Ă��ׂẴm�[�h�Ɍp���D
 	    Vector tmp = new Vector();
 	    tmp.addElement(tail);
 	    recursiveInheritance(head.getDepartFromMeLinks(),tmp);
 	}
	// theLink �� is-a �����ǂ��Ă��ׂẴm�[�h�Ɍp��������
	Vector tmp = new Vector();
	tmp.addElement(theLink);
	recursiveInheritance(tmp,tail.getISATails());

	
	// �֌W�� head �� tail �ɓo�^�D
	head.addArriveAtMeLinks(theLink);
	tail.addDepartFromMeLinks(theLink);
    }

    /***
     * theInheritLinks : �p�����ׂ������N
     * theInheritNodes : �p�����ׂ������N���p������m�[�h
     */
    public void recursiveInheritance(Vector theInheritLinks,
				     Vector theInheritNodes){
	for(int i = 0 ; i < theInheritNodes.size() ; i++){
	    Node theNode = (Node)theInheritNodes.elementAt(i);
	    // theNode ���̂Ƀ����N���p���D
	    for(int j = 0 ; j < theInheritLinks.size() ; j++){
		// theNode �� tail �ɂ��������N�𐶐�
		Link theLink = (Link)theInheritLinks.elementAt(j);
		Link newLink = new Link(theLink.getLabel(),
					theNode.getName(),
					(theLink.getHead()).getName(),
					 this);
		newLink.setInheritance(true);
		links.addElement(newLink);
		theNode.addDepartFromMeLinks(newLink);
	    }
	    // theNode ���� is-a �ł��ǂ��m�[�h�Ƀ����N���p��
	    Vector isaTails = theNode.getISATails();
	    if(isaTails.size() != 0){
		recursiveInheritance(theInheritLinks,isaTails);
	    }
	}
    }


    public Vector getNodes(){
	return nodes;
    }
    
    public Hashtable getNodesNameTable(){
	return nodesNameTable;
    }

    public void printLinks(){
	System.out.println("*** Links ***");
	for(int i = 0 ; i < links.size() ; i++){
	    System.out.println(((Link)links.elementAt(i)).toString());
	}
    }

    public void printNodes(){
	System.out.println("*** Nodes ***");
	for(int i = 0 ; i < nodes.size() ; i++){
	    System.out.println(((Node)nodes.elementAt(i)).toString());
	}
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
	if(matching(string1,string2)){
	    return true;
	} else {
	    return false;
	}
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
