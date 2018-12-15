import java.util.*;

public class Link {
    String label;
    Node tail;
    Node head;
    boolean inheritance;

    Link(String theLabel,String theTail,
	 String theHead, SemanticNet sn){
	label = theLabel;
	Hashtable nodesNameTable = sn.getNodesNameTable();
	Vector nodes = sn.getNodes();

    	tail = (Node)nodesNameTable.get(theTail);
	if(tail == null){
	    tail = new Node(theTail);
	    nodes.addElement(tail);
	    nodesNameTable.put(theTail,tail);
	}

	head = (Node)nodesNameTable.get(theHead);
	if(head == null){
	    head = new Node(theHead);
	    nodes.addElement(head);
	    nodesNameTable.put(theHead,head);
	}
	inheritance = false;
    }

    // For constructing query.
    Link(String theLabel,String theTail,String theHead){
	label = theLabel;
	tail = new Node(theTail);
	head = new Node(theHead);
	inheritance = false;
    }

    public void setInheritance(boolean value){
	inheritance = value;
    }

    public Node getTail(){
	return tail;
    }

    public Node getHead(){
	return head;
    }

    public String getLabel(){
	return label;
    }

    public String getFullName(){
	return tail.getName() + " " + label + " " + head.getName();
    }
    
    public String toString(){
	String result =
	    tail.getName() + "  =" + label +"=>  " + head.getName();
	if(!inheritance){
	    return result;
	} else {
	    return "( "+result+" )";
	}
    }
}
