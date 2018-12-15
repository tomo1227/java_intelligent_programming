/*
 AIDemonProcReadTest.java
  ���ׂĂ̎�ނ̃f�����葱���̃X�[�p�[�N���X

  when-read procedure �́C�X���b�g�l�� Enumeration �Ƃ���
  �Ԃ��Ȃ���΂Ȃ�Ȃ�
*/

import java.util.*;

class AIDemonProcReadTest extends AIDemonProc {

public
Object eval(
 AIFrameSystem inFrameSystem,
 AIFrame inFrame,
 String inSlotName,
 Enumeration inSlotValues,
 Object inOpts )
{
 Object height = inFrame.readSlotValue( inFrameSystem, "height", false );
 if ( height instanceof Integer ) {
  int h = ((Integer) height).intValue();
  return AIFrame.makeEnum( new Integer( (int) (0.9 * (h - 100))) );
 }
 return null;
}

}