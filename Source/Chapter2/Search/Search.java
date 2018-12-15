import java.util.*;

public class Search {
	Node node[];
	Node goal;
	Node start;

	Search() {
		makeStateSpace();
	}

	private void makeStateSpace() {
		node = new Node[10];
		// ��ԋ�Ԃ̐���
		node[0] = new Node("L.A.Airport", 0);
		start = node[0];
		node[1] = new Node("UCLA", 7);
		node[2] = new Node("Hollywood", 4);
		node[3] = new Node("Anaheim", 6);
		node[4] = new Node("Grand Canyon", 1);
		node[5] = new Node("SanDiego", 2);
		node[6] = new Node("Downtown", 3);
		node[7] = new Node("Pasadena", 4);
		node[8] = new Node("DisneyLand", 2);
		node[9] = new Node("Las Vegas", 0);
		goal = node[9];

		node[0].addChild(node[1], 1);
		node[0].addChild(node[2], 3);
		node[1].addChild(node[2], 1);
		node[1].addChild(node[6], 6);
		node[2].addChild(node[3], 6);
		node[2].addChild(node[6], 6);
		node[2].addChild(node[7], 3);
		node[3].addChild(node[4], 5);
		node[3].addChild(node[7], 2);
		node[3].addChild(node[8], 4);
		node[4].addChild(node[8], 2);
		node[4].addChild(node[9], 1);
		node[5].addChild(node[1], 1);
		node[6].addChild(node[5], 7);
		node[6].addChild(node[7], 2);
		node[7].addChild(node[8], 1);
		node[7].addChild(node[9], 7);
		node[8].addChild(node[9], 5);
	}

	/***
	 * ���D��T��
	 */
	public void breadthFirst() {
		Vector open = new Vector();
		open.addElement(node[0]);
		Vector closed = new Vector();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// open�͋󂩁H
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// open�̐擪�����o�� node �Ƃ���D
				Node node = (Node) open.elementAt(0);
				open.removeElementAt(0);
				// node �� �S�[�����H
				if (node == goal) {
					success = true;
					break;
				} else {
					// node ��W�J���Ďq�ߓ_�����ׂċ��߂�D
					Vector children = node.getChildren();
					// node �� closed �ɓ����D
					closed.addElement(node);
					// �q�ߓ_ m �� open �ɂ� closed �ɂ��܂܂�Ă��Ȃ���΁C
					for (int i = 0; i < children.size(); i++) {
						Node m = (Node) children.elementAt(i);
						if (!open.contains(m) && !closed.contains(m)) {
							// m ���� node �ւ̃|�C���^��t����D
							m.setPointer(node);
							if (m == goal) {
								open.insertElementAt(m, 0);
							} else {
								open.addElement(m);
							}
						}
					}
				}
			}
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * �[���D��T��
	 */
	public void depthFirst() {
		Vector open = new Vector();
		open.addElement(node[0]);
		Vector closed = new Vector();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// open�͋󂩁H
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// open�̐擪�����o�� node �Ƃ���D
				Node node = (Node) open.elementAt(0);
				open.removeElementAt(0);
				// node �� �S�[�����H
				if (node == goal) {
					success = true;
					break;
				} else {
					// node ��W�J���Ďq�ߓ_�����ׂċ��߂�D
					Vector children = node.getChildren();
					// node �� closed �ɓ����D
					closed.addElement(node);
					// �q�ߓ_ m �� open �ɂ� closed �ɂ��܂܂�Ă��Ȃ���΁C
					// �ȉ������s�D���D��T���ƈقȂ�̂͂��̕����ł���D
					// j �͕����̎q�ߓ_��K�؂�open�̐擪�ɒu�����߂Ɉʒu
					// �𒲐�����ϐ��ł���C��ʂɂ͓W�J�����Ƃ��̎q�ߓ_
					// �̈ʒu�͔C�ӂł��܂�Ȃ��D
					int j = 0;
					for (int i = 0; i < children.size(); i++) {
						Node m = (Node) children.elementAt(i);
						if (!open.contains(m) && !closed.contains(m)) {
							// m ���� node �ւ̃|�C���^��t����
							m.setPointer(node);
							if (m == goal) {
								open.insertElementAt(m, 0);
							} else {
								open.insertElementAt(m, j);
							}
							j++;
						}
					}
				}
			}
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * �������@
	 */
	public void branchAndBound() {
		Vector open = new Vector();
		open.addElement(start);
		start.setGValue(0);
		Vector closed = new Vector();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// open�͋󂩁H
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// open�̐擪�����o�� node �Ƃ���D
				Node node = (Node) open.elementAt(0);
				open.removeElementAt(0);
				// node �� �S�[�����H
				if (node == goal) {
					success = true;
					break;
				} else {
					// node ��W�J���Ďq�ߓ_�����ׂċ��߂�D
					Vector children = node.getChildren();
					// node �� closed �ɓ����D
					closed.addElement(node);
					for (int i = 0; i < children.size(); i++) {
						Node m = (Node) children.elementAt(i);
						// �q�ߓ_m��open�ɂ�closed�ɂ��܂܂�Ă��Ȃ���΁C
						if (!open.contains(m) && !closed.contains(m)) {
							// m ���� node �ւ̃|�C���^��t����D
							m.setPointer(node);
							// node�܂ł̕]���l��node->m�̃R�X�g��
							// ���������̂�m�̕]���l�Ƃ���
							int gmn = node.getGValue() + node.getCost(m);
							m.setGValue(gmn);
							open.addElement(m);
						}
						// �q�ߓ_m��open�Ɋ܂܂�Ă���Ȃ�΁C
						if (open.contains(m)) {
							int gmn = node.getGValue() + node.getCost(m);
							if (gmn < m.getGValue()) {
								m.setGValue(gmn);
								m.setPointer(node);
							}
						}
					}
				}
			}
			open = sortUpperByGValue(open);
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * �R�o��@
	 */
	public void hillClimbing() {
		Vector open = new Vector();
		open.addElement(start);
		start.setGValue(0);
		Vector closed = new Vector();
		boolean success = false;

		// Start �� node �Ƃ���D
		Node node = start;
		for (;;) {
			// node �� �S�[�����H
			if (node == goal) {
				success = true;
				break;
			} else {
				// node ��W�J���Ďq�ߓ_�����ׂċ��߂�D
				Vector children = node.getChildren();
				System.out.println(children.toString());
				for (int i = 0; i < children.size(); i++) {
					Node m = (Node) children.elementAt(i);
					// m ���� node �ւ̃|�C���^��t����D
					m.setPointer(node);
				}
				// �q�ߓ_�̒��� goal ������� goal �� node �Ƃ���D
				// �Ȃ���΁C�ŏ��� hValue �����q�ߓ_ m �� node
				// �Ƃ���D
				boolean goalp = false;
				Node min = (Node) children.elementAt(0);
				for (int i = 0; i < children.size(); i++) {
					Node a = (Node) children.elementAt(i);
					if (a == goal) {
						goalp = true;
					} else if (min.getHValue() > a.getHValue()) {
						min = a;
					}
				}
				if (goalp) {
					node = goal;
				} else {
					node = min;
				}
			}
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * �ŗǗD��T��
	 */
	public void bestFirst() {
		Vector open = new Vector();
		open.addElement(start);
		start.setGValue(0);
		Vector closed = new Vector();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// open�͋󂩁H
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// open�̐擪�����o�� node �Ƃ���D
				Node node = (Node) open.elementAt(0);
				open.removeElementAt(0);
				// node �� �S�[�����H
				if (node == goal) {
					success = true;
					break;
				} else {
					// node ��W�J���Ďq�ߓ_�����ׂċ��߂�D
					Vector children = node.getChildren();
					// node �� closed �ɓ����D
					closed.addElement(node);
					for (int i = 0; i < children.size(); i++) {
						Node m = (Node) children.elementAt(i);
						// �q�ߓ_m��open�ɂ�closed�ɂ��܂܂�Ă��Ȃ���΁C
						if (!open.contains(m) && !closed.contains(m)) {
							// m ���� node �ւ̃|�C���^��t����D
							m.setPointer(node);
							open.addElement(m);
						}
					}
				}
			}
			open = sortUpperByHValue(open);
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * A* �A���S���Y��
	 */
	public void aStar() {
		Vector open = new Vector();
		open.addElement(start);
		start.setGValue(0);
		start.setFValue(0);
		Vector closed = new Vector();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// open�͋󂩁H
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// open�̐擪�����o�� node �Ƃ���D
				Node node = (Node) open.elementAt(0);
				open.removeElementAt(0);
				// node �� �S�[�����H
				if (node == goal) {
					success = true;
					break;
				} else {
					// node ��W�J���Ďq�ߓ_�����ׂċ��߂�D
					Vector children = node.getChildren();
					// node �� closed �ɓ����D
					closed.addElement(node);
					for (int i = 0; i < children.size(); i++) {
						Node m = (Node) children.elementAt(i);
						int gmn = node.getGValue() + node.getCost(m);
						int fmn = gmn + m.getHValue();

						// �e�q�ߓ_m�̕]���l�ƃ|�C���^��ݒ肷��
						if (!open.contains(m) && !closed.contains(m)) {
							// �q�ߓ_m��open�ɂ�closed�ɂ��܂܂�Ă��Ȃ��ꍇ
							// m ���� node �ւ̃|�C���^��t����D
							m.setGValue(gmn);
							m.setFValue(fmn);
							m.setPointer(node);
							// m��open�ɒǉ�
							open.addElement(m);
						} else if (open.contains(m)) {
							// �q�ߓ_m��open�Ɋ܂܂�Ă���ꍇ
							if (gmn < m.getGValue()) {
								// �]���l���X�V���Cm ���� node �ւ̃|�C���^��t���ւ���
								m.setGValue(gmn);
								m.setFValue(fmn);
								m.setPointer(node);
							}
						} else if (closed.contains(m)) {
							if (gmn < m.getGValue()) {
								// �q�ߓ_m��closed�Ɋ܂܂�Ă��� fmn < fm �ƂȂ�ꍇ
								// �]���l���X�V���Cm����node�ւ̃|�C���^��t���ւ���
								m.setGValue(gmn);
								m.setFValue(fmn);
								m.setPointer(node);
								// �q�ߓ_m��closed����open�Ɉړ�
								closed.removeElement(m);
								open.addElement(m);
							}
						}
					}
				}
			}
			open = sortUpperByFValue(open);
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	
	
	/***
	 * ���̕\��
	 */
	public void printSolution(Node theNode) {
		if (theNode == start) {
			System.out.println(theNode.toString());
		} else {
			System.out.print(theNode.toString() + " <- ");
			printSolution(theNode.getPointer());
		}
	}

	/***
	 * Vector �� Node �� fValue �̏����i���������j�ɗ�׊�����D
	 */
	public Vector sortUpperByFValue(Vector theOpen) {
		Vector newOpen = new Vector();
		Node min, tmp = null;
		while (theOpen.size() > 0) {
			min = (Node) theOpen.elementAt(0);
			for (int i = 1; i < theOpen.size(); i++) {
				tmp = (Node) theOpen.elementAt(i);
				if (min.getFValue() > tmp.getFValue()) {
					min = tmp;
				}
			}
			newOpen.addElement(min);
			theOpen.removeElement(min);
		}
		return newOpen;
	}

	
	/***
	 * Vector �� Node �� gValue �̏����i���������j�ɗ�׊�����D
	 */
	public Vector sortUpperByGValue(Vector theOpen) {
		Vector newOpen = new Vector();
		Node min, tmp = null;
		while (theOpen.size() > 0) {
			min = (Node) theOpen.elementAt(0);
			for (int i = 1; i < theOpen.size(); i++) {
				tmp = (Node) theOpen.elementAt(i);
				if (min.getGValue() > tmp.getGValue()) {
					min = tmp;
				}
			}
			newOpen.addElement(min);
			theOpen.removeElement(min);
		}
		return newOpen;
	}

	/***
	 * Vector �� Node �� hValue �̏����i���������j�ɗ�׊�����D
	 */
	public Vector sortUpperByHValue(Vector theOpen) {
		Vector newOpen = new Vector();
		Node min, tmp = null;
		while (theOpen.size() > 0) {
			min = (Node) theOpen.elementAt(0);
			for (int i = 1; i < theOpen.size(); i++) {
				tmp = (Node) theOpen.elementAt(i);
				if (min.getHValue() > tmp.getHValue()) {
					min = tmp;
				}
			}
			newOpen.addElement(min);
			theOpen.removeElement(min);
		}
		return newOpen;
	}

	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("USAGE:");
			System.out.println("java Search [Number]");
			System.out.println("[Number] = 1 : Bredth First Search");
			System.out.println("[Number] = 2 : Depth  First Search");
			System.out.println("[Number] = 3 : Branch and Bound Search");
			System.out.println("[Number] = 4 : Hill Climbing Search");
			System.out.println("[Number] = 5 : Best First Search");
			System.out.println("[Number] = 6 : A star Algorithm");
		} else {
			int which = Integer.parseInt(args[0]);
			switch (which) {
			case 1:
				// ���D��T��
				System.out.println("\nBreadth First Search");
				(new Search()).breadthFirst();
				break;
			case 2:
				// �[���D��T��
				System.out.println("\nDepth First Search");
				(new Search()).depthFirst();
				break;
			case 3:
				// �������@
				System.out.println("\nBranch and Bound Search");
				(new Search()).branchAndBound();
				break;
			case 4:
				// �R�o��@
				System.out.println("\nHill Climbing Search");
				(new Search()).hillClimbing();
				break;
			case 5:
				// �ŗǗD��T��
				System.out.println("\nBest First Search");
				(new Search()).bestFirst();
				break;
			case 6:
				// A*�A���S���Y��
				System.out.println("\nA star Algorithm");
				(new Search()).aStar();
				break;
			default:
				System.out.println("Please input numbers 1 to 6");
			}
		}
	}
}

class Node {
	String name;
	Vector children;
	Hashtable childrenCosts;
	Node pointer; // ��\���̂��߂̃|�C���^
	int gValue; // �R�X�g
	int hValue; // �q���[���X�e�B�b�N�l
	int fValue; // �]���l
	boolean hasGValue = false;
	boolean hasFValue = false;

	Node(String theName, int theHValue) {
		name = theName;
		children = new Vector();
		childrenCosts = new Hashtable();
		hValue = theHValue;
	}

	public String getName() {
		return name;
	}

	public void setPointer(Node theNode) {
		this.pointer = theNode;
	}

	public Node getPointer() {
		return this.pointer;
	}

	public int getGValue() {
		return gValue;
	}

	public void setGValue(int theGValue) {
		hasGValue = true;
		this.gValue = theGValue;
	}

	public int getHValue() {
		return hValue;
	}

	public int getFValue() {
		return fValue;
	}

	public void setFValue(int theFValue) {
		hasFValue = true;
		this.fValue = theFValue;
	}

	
	/***
	 * theChild ���̐ߓ_�̎q�ߓ_ theCost ���̎q�ߓ_�܂ł̃R�X�g
	 */
	public void addChild(Node theChild, int theCost) {
		children.addElement(theChild);
		childrenCosts.put(theChild, new Integer(theCost));
	}

	public Vector getChildren() {
		return children;
	}

	public int getCost(Node theChild) {
		return ((Integer) childrenCosts.get(theChild)).intValue();
	}

	public String toString() {
		String result = name + "(h:" + hValue + ")";
		if (hasGValue) {
			result = result + "(g:" + gValue + ")";
		}
		if (hasFValue) {
			result = result + "(f:" + fValue + ")";
		}
		return result;
	}
}
