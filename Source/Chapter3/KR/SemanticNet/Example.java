import java.util.*;

/***
 * Semantic Net �̎g�p��
 */
public class Example {
    public static void main(String args[]){
	SemanticNet sn = new SemanticNet();

	// �싅�̓X�|�[�c�ł���D
	sn.addLink(new Link("is-a","baseball","sports",sn));

	// ���Y�͖��É��H�Ƒ�w�̊w���ł���D
	sn.addLink(new Link("is-a","Taro","NIT-student",sn));

	// ���Y�̐��͐l�H�m�\�ł���D
	sn.addLink(new Link("speciality","Taro","AI",sn));
	
	// �t�F���[���͎Ԃł���D
	sn.addLink(new Link("is-a","Ferrari","car",sn));

	// �Ԃ̓G���W�������D
	sn.addLink(new Link("has-a","car","engine",sn));
	
	// ���Y�̎�͖싅�ł���D
	sn.addLink(new Link("hobby","Taro","baseball",sn));
	
	// ���Y�̓t�F���[�������L����D
	sn.addLink(new Link("own","Taro","Ferrari",sn));

	// ���É��H�Ƒ�w�̊w���́C�w���ł���D
	sn.addLink(new Link("is-a","NIT-student","student",sn));

	// �w���͕׋����Ȃ��D
	sn.addLink(new Link("donot","student","study",sn));

	sn.printLinks();
	sn.printNodes();

	Vector query = new Vector();
	query.addElement(new Link("own","?y","Ferrari"));
	query.addElement(new Link("is-a","?y","student"));
	query.addElement(new Link("hobby","?y","baseball"));
	sn.query(query);
    }    
}

