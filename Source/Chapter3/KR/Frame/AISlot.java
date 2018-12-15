/*
 AISlot.java

  �t���[���ɂ�����X���b�g����������N���X�D
*/

import java.util.*;


class AISlot {

public final static int WHEN_REQUESTED = 0;
public final static int WHEN_READ = WHEN_REQUESTED + 1;
public final static int WHEN_WRITTEN = WHEN_READ + 1;
public final static int WITH_RESPECT_TO = WHEN_WRITTEN + 1;
public final static int DEMON_PROCS = WITH_RESPECT_TO + 1;

private Vector mVals = new Vector();
private AIDemonProc mDemons[];

AISlot() {
 mDemons = new AIDemonProc[ DEMON_PROCS ];
}

//
Enumeration getSlotValues() {
 return mVals.elements();
}

Object getSlotValue() {
 Enumeration iter = getSlotValues();
 if ( iter != null && iter.hasMoreElements() == true )
  return iter.nextElement();
 return null;
}

void setSlotValue( Object inSlotValue ) {
 mVals = new Vector();
 addSlotValue( inSlotValue );
}

void addSlotValue( Object inSlotValue ) {
 mVals.addElement( inSlotValue );
}

void removeSlotValue( Object inSlotValue ) {
 mVals.removeElement( inSlotValue );
}

//
AIDemonProc getWhenRequestedProc() { return mDemons[ WHEN_REQUESTED ]; }
AIDemonProc getWhenReadProc() { return mDemons[ WHEN_READ ]; }
AIDemonProc getWhenWrittenProc() { return mDemons[ WHEN_WRITTEN ]; }

AIDemonProc getDemonProc( int inType ) { return mDemons[ inType ]; }

void setDemonProc(
 int inType,
 AIDemonProc inDemonProc )
{
 mDemons[ inType ] = inDemonProc;
}
 
} // end of class definition