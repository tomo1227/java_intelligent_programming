/*
 AIFrame.java

*/

import java.util.*;

abstract
class AIFrame {
 
private boolean mIsInstance;
private String mName;
private Dictionary mSlots = new Hashtable();
private AIWhenConstructedProc mWhenConstructedProc = null;

/*
 * AIFrame
 *  �R���X�g���N�^
 */
AIFrame( AIFrameSystem inFrameSystem,
 AIClassFrame inSuperFrame,
 String inName,
 boolean inIsInstance )
{
 mName = inName;
 mIsInstance = inIsInstance;
 if ( inSuperFrame != null )
  setSlotValue( getSuperSlotName(), inSuperFrame );
 evalWhenConstructedProc( inFrameSystem, this );
}


/*
 * AIFrame
 *  �R���X�g���N�^
 */
AIFrame( AIFrameSystem inFrameSystem,
 Enumeration inSuperFrames,
 String inName,
 boolean inIsInstance )
{
 mName = inName;
 mIsInstance = inIsInstance;
 while ( inSuperFrames.hasMoreElements() == true ) {
  AIFrame frame = (AIFrame) inSuperFrames.nextElement();
  addSlotValue( getSuperSlotName(), frame );
 }
 evalWhenConstructedProc( inFrameSystem, this );
}


/*
 * setWhenConstructedProc
 *  when-constructed proc ��o�^
 */
public
void setWhenConstructedProc( AIWhenConstructedProc inProc ) {
 mWhenConstructedProc = inProc;
}


/*
 * getWhenConstructedProc
 *  when-constructed proc ��Ԃ�
 */
public
AIWhenConstructedProc getWhenConstructedProc() {
 return mWhenConstructedProc;
}


/*
 * evalWhenConstructedProc
 *  when-constructed proc ��]��
 */
void evalWhenConstructedProc(
 AIFrameSystem inFrameSystem,
 AIFrame inFrame )
{
 Enumeration supers = getSupers();
 if ( supers != null ) {
  while ( supers.hasMoreElements() == true ) {
   AIClassFrame frame = (AIClassFrame) supers.nextElement();
   frame.evalWhenConstructedProc( inFrameSystem, inFrame );
  }
 }
 if ( mWhenConstructedProc != null )
  mWhenConstructedProc.eval( inFrameSystem, inFrame );
}


/*
 * isInstance
 *  ���̃t���[�����C���X�^���X�t���[���Ȃ� true ��Ԃ�
 */
public boolean isInstance() { return mIsInstance; }


/*
 * getSupers
 *  ���̃t���[���̃X�[�p�[�t���[����Ԃ�
 */
public
Enumeration getSupers() {
 return getSlotValues( getSuperSlotName() );
}


/**
 * readSlotValue
 *  �X���b�g inSlotName �Ɋi�[����Ă���X���b�g�l��Ԃ��D
 *  �����̃X���b�g�l���i�[����Ă���Ƃ��́C�ŏ��̃I�u�W�F�N�g��Ԃ��D
 *
 *  �X���b�g�l�̗D��x
 *   1. ������ when-requested procedure
 *   2. �X�[�p�[�N���X�� when-requested procedure
 *   3. ������ when-read procedure
 *   4. �X�[�p�[�N���X�� when-read procedure
 *   5. �����̃X���b�g�l
 *   6. �X�[�p�[�N���X�̃X���b�g�l
 */
public
Object
readSlotValue(
 AIFrameSystem inFrameSystem,
 String inSlotName,
 boolean inDefault )
{
 return getFirst(
         readSlotValues( inFrameSystem, inSlotName, inDefault ) );
}


/**
 * readSlotValues
 *  �X���b�g inSlotName �Ɋi�[����Ă���X���b�g�l��Ԃ��D
 */
public
Enumeration
readSlotValues(
 AIFrameSystem inFrameSystem,
 String inSlotName,
 boolean inDefault )
{
 Enumeration obj = null;
 
 if ( inDefault == false ) {
  AISlot slot = getSlot( inSlotName );
  if ( slot != null )
   obj = slot.getSlotValues();
 }
  
 if ( obj == null )
  obj = readSlotValuesWithWhenRequestedProc(
         inFrameSystem, inSlotName );

 if ( obj == null ) {
  Enumeration supers = getSupers();
  while ( supers.hasMoreElements() == true ) {
   AIClassFrame frame = (AIClassFrame) supers.nextElement();
   obj = frame.getSlotValues( inSlotName );
   if ( obj != null )
    break;
  }
 }

 return readSlotValuesWithWhenReadProc(
         inFrameSystem, inSlotName, obj );
}


/**
 * readSlotValuesWithWhenRequestedProc
 *  �X���b�g inSlotName �Ɋi�[����Ă���X���b�g�l��Ԃ��D
 */
Enumeration
readSlotValuesWithWhenRequestedProc(
 AIFrameSystem inFrameSystem,
 String inSlotName )
{
 return readSlotValuesWithWhenRequestedProc(
         inFrameSystem, this, inSlotName );
}

protected
Enumeration
readSlotValuesWithWhenRequestedProc(
 AIFrameSystem inFrameSystem,
 AIFrame inFrame,
 String inSlotName )
{
 Enumeration obj = null;
 AISlot slot = getSlot( inSlotName );
  
  obj = evalWhenRequestedProc(
         inFrameSystem, inFrame, slot, inSlotName );
 if ( obj != null )
  return obj;

 Enumeration supers = getSupers();
 if ( supers != null ) {
  while ( supers.hasMoreElements() == true ) {
   AIClassFrame frame = (AIClassFrame) supers.nextElement();
   slot = frame.getSlot( inSlotName );
   obj = frame.evalWhenRequestedProc(
          inFrameSystem, inFrame, slot, inSlotName );
   if ( obj != null )
    return obj;
  }
 }
 
 return null;
}

protected
Enumeration
evalWhenRequestedProc(
 AIFrameSystem inFrameSystem,
 AIFrame inFrame,
 AISlot inSlot,
 String inSlotName )
{
  if ( inSlot != null && inSlot.getWhenRequestedProc() != null ) {
  AIDemonProc demon = inSlot.getWhenRequestedProc();
  if ( demon != null )
   return (Enumeration) demon.eval(
           inFrameSystem, inFrame, inSlotName, null );
  }
 return null;
}


/**
 * readSlotValuesWithWhenReadProc
 *  �X���b�g inSlotName �Ɋi�[����Ă���X���b�g�l��Ԃ��D
 */
Enumeration
readSlotValuesWithWhenReadProc(
 AIFrameSystem inFrameSystem,
 String inSlotName,
 Enumeration inSlotValue )
{
 return readSlotValuesWithWhenReadProc(
         inFrameSystem, this, inSlotName, inSlotValue );
}

protected
Enumeration
readSlotValuesWithWhenReadProc(
 AIFrameSystem inFrameSystem,
 AIFrame inFrame,
 String inSlotName,
 Enumeration inSlotValue )
{
 AISlot slot;
  
 Enumeration supers = getSupers();
 if ( supers != null ) {
  while ( supers.hasMoreElements() == true ) {
   AIClassFrame frame = (AIClassFrame) supers.nextElement();
   slot = frame.getSlot( inSlotName );
   inSlotValue =
    frame.evalWhenReadProc(
     inFrameSystem, inFrame, slot, inSlotName, inSlotValue );
  }
 }

 slot = getSlot( inSlotName );
  return evalWhenReadProc( inFrameSystem, inFrame,
          slot, inSlotName, inSlotValue );
}

protected
Enumeration
evalWhenReadProc(
 AIFrameSystem inFrameSystem,
 AIFrame inFrame,
 AISlot inSlot,
 String inSlotName,
 Enumeration inSlotValue )
{
  if ( inSlot != null && inSlot.getWhenReadProc() != null ) {
  AIDemonProc demon = inSlot.getWhenReadProc();
  if ( demon != null )
   inSlotValue =
    (Enumeration) demon.eval( inFrameSystem, inFrame,
                   inSlotName, inSlotValue );
  }
 
 return inSlotValue; 
}

/**
 * writeSlotValue
 *  �X���b�g inSlotName �ɃX���b�g�l inSlotValue ��ݒ肷��D
 */
public
void
writeSlotValue(
 AIFrameSystem inFrameSystem,
 String inSlotName,
 Object inSlotValue )
{
 AISlot slot = getSlot( inSlotName );
 if ( slot == null ) {
  slot = new AISlot();
  mSlots.put( inSlotName, slot );
 }

 slot.setSlotValue( inSlotValue );

 writeSlotValueWithWhenWrittenProc(
  inFrameSystem, inSlotName, inSlotValue );
}


void writeSlotValueWithWhenWrittenProc(
 AIFrameSystem inFrameSystem,
 String inSlotName,
 Object inSlotValue )
{
 Enumeration supers = getSupers();
 if ( supers != null ) {
  while ( supers.hasMoreElements() == true ) {
   AIClassFrame frame = (AIClassFrame) supers.nextElement();
   frame.writeSlotValueWithWhenWrittenProc(
    inFrameSystem, inSlotName, inSlotValue );
  }
 }

 AISlot slot = getSlot( inSlotName );
 if ( slot != null ) {
  AIDemonProc demon = slot.getWhenWrittenProc();
  if ( demon != null )
   demon.eval( inFrameSystem, this,
    inSlotName, makeEnum( inSlotValue ) );  
 }
}


// ----------------------------------------------------------------------
public
Object getSlotValue( String inSlotName ) {
 Enumeration iter = getSlotValues( inSlotName );
 if ( iter != null && iter.hasMoreElements() == true )
  return iter.nextElement();
 return null;
}

public
Enumeration getSlotValues( String inSlotName ) {
 AISlot slot = getSlot( inSlotName );
 if ( slot == null )
  return null;
 return slot.getSlotValues();
}

public
void setSlotValue( String inSlotName, Object inSlotValue ) {
 AISlot slot = getSlot( inSlotName );
 if ( slot == null ) {
  slot = new AISlot();
  mSlots.put( inSlotName, slot );
 }
 slot.setSlotValue( inSlotValue );
}

public
void addSlotValue( String inSlotName, Object inSlotValue ) {
 AISlot slot = getSlot( inSlotName );
 if ( slot == null ) {
  slot = new AISlot();
  mSlots.put( inSlotName, slot );
 }
 slot.addSlotValue( inSlotValue );
}

public
void removeSlotValue( String inSlotName, Object inSlotValue ) {
 AISlot slot = getSlot( inSlotName );
 if ( slot != null )
  slot.removeSlotValue( inSlotValue );
}

public
void setDemonProc(
 int inType,
 String inSlotName,
 AIDemonProc inDemonProc )
{
 AISlot slot = getSlot( inSlotName );
 if ( slot == null ) {
  slot = new AISlot();
  mSlots.put( inSlotName, slot );
 }
 slot.setDemonProc( inType, inDemonProc );
}


// ------------------------------------------------------------------
// utils
// ------------------------------------------------------------------

/*
 * getSuperSlotName
 *  �X�[�p�[�t���[�����i�[���Ă���X���b�g�̖��O��Ԃ��D
 */
String getSuperSlotName() {
 if ( isInstance() == true )
  return "is-a";
 return "ako";
}

/*
 * getSlot
 *  �X���b�g���� inSlotName �ł���X���b�g��Ԃ��D
 */
AISlot getSlot( String inSlotName ) {
 return (AISlot) mSlots.get( inSlotName );
}


/*
 * getFirst
 *  inEnum ���̍ŏ��̃I�u�W�F�N�g��Ԃ�
 */
public static
Object getFirst( Enumeration inEnum ) {
 if ( inEnum != null && inEnum.hasMoreElements() == true )
  return inEnum.nextElement();
 return null;
}


/*
 * makeEnum
 *
 */
public static
Enumeration makeEnum( Object inObj ) {
 Vector vec = new Vector();
 vec.addElement( inObj );
 return vec.elements();
}

} // end of class definition