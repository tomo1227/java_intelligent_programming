/**
 * �ȒP�ȃ����[�V���i���f�[�^�x�[�X
 */

import java.util.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class RelationalDatabase {
	public static void main(String args[]) {
		// "�\1"
		Relation relation1 = new Relation("�\1", "���O", "����", "����", "�E��", "�N��");
		relation1.insert("���", "���{", "�j", "�w��", "23");
		relation1.insert("�N���X", "�C�M���X", "�j", "�w��", "26");
		relation1.insert("���[�V�[", "�A�����J", "��", "������", "34");
		relation1.insert("�W����", "�A�����J", "�j", "��Ј�", "43");
		relation1.insert("���n��", "�h�C�c", "�j", "�w��", "23");
		relation1.insert("�z�q", "���{", "��", "��w", "31");
		relation1.show("�\1", 20, 20);
		// "�\3"
		Relation relation3 = new Relation("�\3", "���O", "�ꍑ��");
		relation3.insert("�N���X", "�p��");
		relation3.insert("�W����", "�p��");
		relation3.insert("���n��", "�h�C�c��");
		relation3.show("�\3", 40, 40);
		// "�\2 SELECT �E��=�w�� and ����=�j"
		Set<Constraint> constraints = new HashSet<Constraint>();
		constraints.add(new EqualityConstraint("�E��", "�w��"));
		constraints.add(new EqualityConstraint("����", "�j"));
		Relation relation2 = relation1.select(constraints);
		relation2.name = "�\2";
		relation2.show("�\2 SELECT �E��=�w�� and ����=�j", 60, 60);
		// "�\4 JOIN �\�Qand �\�R with ���O�����O"
		Relation relation4 = relation2.join(relation3, "���O");
		relation4.name = "�\4";
		relation4.show("�\4 JOIN �\�Qand �\�R with ���O�����O", 80, 80);
		// "�\5 PROJECT over ���� and �ꍑ��"
		Relation relation5 = relation4.project("�\2.����", "�\3.�ꍑ��");
		relation5.name = "�\5";
		relation5.show("�\5 PROJECT over ���� and �ꍑ��", 100, 100);
	}
}

// �����[�V�����i�֌W�j
class Relation {
	String name; // �����[�V������
	String fields[]; // �t�B�[���h���ꗗ
	List<String[]> records; // ���R�[�h
	Map<String, Integer> index; // ���Ԗڂ̃t�B�[���h�����Ǘ�

	// �R���X�g���N�^
	public Relation(String name, String... fields) {
		this.name = name;
		this.fields = fields;
		records = new ArrayList<String[]>();
		index = new HashMap<String, Integer>();
		int i = 0;
		for (String fieled: fields)
			index.put(fieled, i++);
	}

	// �R���X�g���N�^(�������p)
	Relation(Relation relation, List<String[]> records, boolean deepCopy) {
		name = relation.name;
		this.fields = relation.fields.clone();
		this.index = new HashMap<String, Integer>(relation.index);
		this.records = deepCopy ? deepCopy(records) : records; 
	}
	
	// select���Z: �w�肵�������𖞂������R�[�h��Ԃ�
	public Relation select(Set<Constraint> constraints) {
		// �w�肵�������𖞂������R�[�h���烊���[�V�����𐶐�����
		List<String[]> feasibleRecords = solve(records, constraints);
		return new Relation(this, feasibleRecords, false);
	}

	// project���Z: �w�肵���t�B�[���h�����o��
	public Relation project(String... fields) {
		// �V���������[�V�������쐬����
		Relation relation = new Relation(name, fields);
		for (String[] record: records) {
			// �w�肵���t�B�[���h�����̃��R�[�h���쐬
			String[] values = new String[fields.length];
			int i = 0;
			for (String field: fields)
				values[i++] = record[index.get(field)];
			relation.insert(values);
		}
		return relation;
	}

	// join���Z: �����[�V��������������(inner join)
	public Relation join(Relation relation, String field) {
		// �t�B�[���h�̌���
		int fieldSize = fields.length + relation.fields.length;
		String[] joinedFields = new String[fieldSize];
		int i = 0;
		for (String field1: fields)
			joinedFields[i++] = name + "." + field1;
		for (String field1: relation.fields)
			joinedFields[i++] = relation.name + "." + field1;

		// ���R�[�h�̌���
		int index1 = index.get(field);
		int index2 = relation.index.get(field);
		List<String[]> joinedRecords = new ArrayList<String[]>();
		for (String[] record1: records) {
			String value1 = record1[index1];
			for (String[] record2: relation.records) {
				String value2 = record2[index2];
				if (value1.equals(value2)) {
					// ���R�[�h����������
					String[] record = new String[fieldSize];
					int j = 0;
					for (String value: record1)
						record[j++] = value;
					for (String value: record2)
						record[j++] = value;
					joinedRecords.add(record);
				}
			}
		}
		// �V���������[�V�����̐���
		String joinedName = name + "+" + relation.name;
		relation = new Relation(joinedName, joinedFields);
		relation.records = joinedRecords;

		return relation;
	}

	// ���R�[�h��ǉ�����
	public void insert(String... values) {
		// ���R�[�h���R�s�[����
		String record[] = new String[values.length];
		int i = 0;
		for (String s: values)
			record[i++] = s;
		// �����[�V�����ɒǉ�����
		records.add(record);
	}

	// ���R�[�h���X�V����
	public void update(String field, String value, Set<Constraint> constraints) {
		// �����𖞂������R�[�h�𓾂�
		List<String[]> records1 = solve(records, constraints);
		// �����𖞂������R�[�h���X�V����
		int i = index.get(field);
		for (String[] record: records1)
			record[i] = value;
	}
	
	// ���R�[�h���폜����
	public void delete(Set<Constraint> constraints) {
		// �����𖞂������R�[�h�𓾂�
		List<String[]> records1 = solve(records, constraints);
		// �����𖞂������R�[�h������
		Iterator<String[]> i = records.iterator();
		while (i.hasNext()) {
			String[] record = i.next();
			if (records1.contains(record))
				i.remove();
		}
	}

	// ���R�[�h�̃��X�g���f�B�[�v�R�s�[����
	List<String[]> deepCopy(List<String[]> src) {
		List<String[]> copy = new ArrayList<String[]>();
		for (String[] record: src) {
			record = record.clone();
			copy.add(record);
		}
		return copy;
	}
	
	// �����𖞂������R�[�h��I��
	List<String[]> solve(List<String[]> allRecords, Set<Constraint> constraints) {
		List<String[]> feasibleRecords = new ArrayList<String[]>(allRecords);
		// �����𖞂����Ȃ����R�[�h���폜����
		for (Constraint constraint: constraints) {
			Iterator<String[]> j = feasibleRecords.iterator();
			while (j.hasNext()) {
				if (!constraint.isSatisfied(this, j.next()))
					j.remove();
			}
		}
		return feasibleRecords;
	}

	// �����[�V������\������
	public void show(String title, int x, int y) {
		int w = 100 * fields.length;
		int h = 16 * records.size() + 20;
		String[][] tableData = new String[records.size()][fields.length];
		for (int i = 0; i < records.size(); i++)
			for (int j = 0; j < fields.length; j++)
				tableData[i][j] = records.get(i)[j];
		JFrame frame = new JFrame();
		frame.setTitle(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTable table = new JTable(tableData, fields);
		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(w, h));
		JPanel p = new JPanel();
		p.add(sp);
		frame.getContentPane().add(p, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.setBounds(x, y, table.getWidth() + 32, h + 40);
	}
}

// select�ȂǂŎg���������
abstract class Constraint {
	abstract boolean isSatisfied(Relation relation, String[] record);
}

// ���������ǂ������`�F�b�N���鐧�����
class EqualityConstraint extends Constraint {	
	String field;
	String value;
	
	EqualityConstraint(String field, String value) {
		this.field = field;
		this.value = value;
	}

	boolean isSatisfied(Relation relation, String[] record) {
		int index = relation.index.get(field);
		return value.equals(record[index]);
	}
}
