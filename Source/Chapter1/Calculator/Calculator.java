import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Java�ɂ��ȒP�Ȍv�Z�@
 *
 */
public class Calculator {
    static CalculatorFrame cf;    
    public static void main(String args[]){
	cf = new CalculatorFrame();
	cf.show();
    }
}

class CalculatorFrame extends Frame implements ActionListener {
    Label text;
    Button button[];
    Panel numberPanel;
    Button clear,plus,minus,equal;    
    Panel commandPanel;
    String buffer;
    int result;
    String operator;
    boolean append;
    
    CalculatorFrame(){
	setTitle("Calculator");
	
	initBuffer();
	initOperator();
	append = false;
	text = new Label(buffer,Label.RIGHT);
	text.setBackground(Color.white);
	showBuffer();
	result = Integer.parseInt(buffer);

	button = new Button[10];
	for(int i = 0  ; i < 10 ; i++){
	    button[i] = new Button((new Integer(i)).toString());
	    button[i].addActionListener(this);
	}

	clear = new Button("C");
	clear.addActionListener(this);	
	plus  = new Button("+");
	plus.addActionListener(this);	
	minus = new Button("-");
	minus.addActionListener(this);	
	equal = new Button("=");
	equal.addActionListener(this);	

	numberPanel = new Panel();
	numberPanel.setLayout(new GridLayout(4,3));
	for(int i = 1 ; i < 10 ; i++){
	    numberPanel.add(button[i]);
	}
	numberPanel.add(button[0]);

	commandPanel = new Panel();
	commandPanel.setLayout(new GridLayout(4,1));
	commandPanel.add(clear);
	commandPanel.add(plus);
	commandPanel.add(minus);
	commandPanel.add(equal);
	
	setLayout(new BorderLayout());
	add("North",text);
	add("Center",numberPanel);
	add("East",commandPanel);
	pack();
    }

    public void actionPerformed(ActionEvent event){
	Object source = event.getSource();
	if(source == clear){
	    // �N���A�̏ꍇ
	    initBuffer();
	    initOperator();
	    showBuffer();
	} else if (source == plus){
	    // �v���X�̏ꍇ
	    calculate();
	    setOperator("plus");
	    append = false;
	} else if (source == minus){
	    // �}�C�i�X�̏ꍇ
	    calculate();
	    setOperator("minus");
	    append = false;
	} else if (source == equal){
	    // �C�R�[���̏ꍇ
	    calculate();
	    append = false;
	    initOperator();
	} else {
	    // 0 �` 9�̐����̏ꍇ
	    if(append){
		buffer = buffer + event.getActionCommand();
	    } else {
		buffer = event.getActionCommand();
	    }
	    append = true;
	    showBuffer();
	}
    }

    void showBuffer(){
	// ������Ƃ���010�Ȃǂ̂悤��0���擪�ɂ��Ă��܂����C
	// �������xInteger�ɕϊ������10�ƔF�������D
	// ����ɂ���10�𕶎���ɕϊ����o�b�t�@�ɂ��܂��D
	buffer = Integer.toString(Integer.parseInt(buffer));
	text.setText(buffer);
    }

    void initBuffer(){
	buffer = null;
	buffer = new String("0");
    }

    void initOperator(){
	operator = null;
	operator = new String("none");
    }

    void setOperator(String theOperator){
	operator = theOperator;
    }

    void calculate(){
	if(operator.equals("plus")){
	    result = result + Integer.parseInt(buffer);
	    buffer = Integer.toString(result);
	    showBuffer();
	} else if(operator.equals("minus")){
	    result = result - Integer.parseInt(buffer);
	    buffer = Integer.toString(result);
	    showBuffer();
	} else {
	    result = Integer.parseInt(buffer);
	}
    }
}
