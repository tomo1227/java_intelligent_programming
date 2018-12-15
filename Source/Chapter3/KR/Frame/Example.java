/*
 Example.java

*/

public class Example {

 public static void main(String args[]) {
  System.out.println( "Frame" );

  // �t���[���V�X�e���̏�����
  AIFrameSystem fs = new AIFrameSystem();
  
  // �N���X�t���[�� human �̐���
  fs.createClassFrame( "human" );
  // height �X���b�g��ݒ�
  fs.writeSlotValue( "human", "height", new Integer( 160 ) );
  // height ���� weight ���v�Z���邽�߂̎� weight = 0.9*(height-100) ��
  // when-requested demon �Ƃ��� weight �X���b�g�Ɋ��蓖�Ă�  
  fs.setWhenRequestedProc( "human", "weight", new AIDemonProcReadTest() );

  // �C���X�^���X�t���[�� tora ��̐���
  fs.createInstanceFrame( "human", "tora" );

  // height �� weight �̓f�t�H���g�l
  System.out.println( fs.readSlotValue( "tora", "height", false ) );
  System.out.println( fs.readSlotValue( "tora", "weight", false ) );

  // weight �̓f�t�H���g�l
  fs.writeSlotValue( "tora", "height", new Integer( 165 ) );
  System.out.println( fs.readSlotValue( "tora", "height", false ) );
  System.out.println( fs.readSlotValue( "tora", "weight", false ) );

  // �Ăуf�t�H���g�l��\��
  fs.writeSlotValue( "tora", "weight", new Integer( 50 ) );
  System.out.println( fs.readSlotValue( "tora", "height", true ) );
  System.out.println( fs.readSlotValue( "tora", "weight", true ) );
 }
 
}