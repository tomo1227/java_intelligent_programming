/*
 AIFrameSystem.java
  �t���[���V�X�e��
*/

import java.util.*;

public
class AIFrameSystem {

final static String sTopFrameName = "top_level_frame"; 

// ���ׂẴt���[�����i�[���邽�߂̎����D
// �t���[�������C���f�b�N�X�Ƃ��ė��p�D
private Dictionary mFrames = new Hashtable();


/**
 * AIFrameSystem
 *  �R���X�g���N�^
 */
public
AIFrameSystem() {
 mFrames.put( sTopFrameName,
  new AIClassFrame( this, null, sTopFrameName ) );
}


/**
 * createClassFrame
 *  �N���X�t���[�� inName ���쐬����D
 */
public
void createClassFrame( String inName ) {
 createFrame( sTopFrameName, inName, false );
}


/**
 * createClassFrame
 *  �X�[�p�[�t���[���Ƃ��� inSuperName �����N���X�t���[��
 *  inName ���쐬����D
 *
 *  @param inSuperName �X�[�p�[�t���[���̃t���[����
 *  @param inName �t���[����
 */
public
void createClassFrame( String inSuperName, String inName ) {
 createFrame( inSuperName, inName, false );
}


/**
 * createInstanceFrame
 *  �X�[�p�[�t���[���Ƃ��� inSuperName �����C���X�^���X�t���[��
 *  inName ���쐬����D
 *
 *  @param inSuperName �X�[�p�[�t���[���̃t���[����
 *  @param inName �t���[����
 */
public
void createInstanceFrame( String inSuperName, String inName ) {
 createFrame( inSuperName, inName, true ); 
}


/*
 * createFrame 
 *  �t���[�����쐬����
 *
 *  @param inSuperName �X�[�p�[�t���[���̃t���[����
 *  @param inName �t���[����
 *  @param inIsInstance �C���X�^���X�t���[���Ȃ� true
 */
void createFrame(
 String inSuperName,
 String inName,
 boolean inIsInstance )
{
 AIClassFrame frame;
 try {
  frame = (AIClassFrame) mFrames.get( inSuperName );
  createFrame( frame, inName, inIsInstance );
 } catch ( Throwable err ) {
 }
}


/*
 * createFrame 
 *  �t���[�����쐬����
 *
 *  @param inSuperName �X�[�p�[�t���[��
 *  @param inName �t���[����
 *  @param inIsInstance �C���X�^���X�t���[���Ȃ� true
 */

void createFrame(
 AIClassFrame inSuperFrame,
 String inName,
 boolean inIsInstance )
{
 AIFrame frame;
 if ( inIsInstance == true ) {
  frame = new AIInstanceFrame( this, inSuperFrame, inName );
 } else {
  frame = new AIClassFrame( this, inSuperFrame, inName );
 }
 mFrames.put( inName, frame );
}


/**
 * readSlotValue 
 *  �X���b�g�l��Ԃ�
 *
 *  @param inFrameName �t���[����
 *  @param inSlotName �X���b�g��
 *  @param inDefault �f�t�H���g�l��D�悵�����Ȃ� true
 */
public
Object readSlotValue(
 String inFrameName,
 String inSlotName,
 boolean inDefault )
{
 AIFrame frame = (AIFrame) mFrames.get( inFrameName );
 return frame.readSlotValue( this, inSlotName, inDefault );
}


/**
 * readSlotValue 
 *  �X���b�g�l��Ԃ�
 *
 *  @param inFrameName �t���[����
 *  @param inSlotName �X���b�g��
 */
public
Object readSlotValue(
 String inFrameName,
 String inSlotName )
{
 AIFrame frame = (AIFrame) mFrames.get( inFrameName );
 return frame.readSlotValue( this, inSlotName, false );
}


/**
 * readSlotValue 
 *  �X���b�g�l��Ԃ�
 *
 *  @param inFrameName �t���[����
 *  @param inSlotName �X���b�g��
 *  @param inFacetName �t�@�Z�b�g��
 */
public
Object readSlotValue(
 String inFrameName,
 String inSlotName,
 String inFacetName )
{
 AIFrame frame = (AIFrame) mFrames.get( inFrameName );
 return frame.readSlotValue( this, inSlotName, false );
}


/**
 * writeSlotValue 
 *  �X���b�g�l��ݒ肷��D
 *
 *  @param inFrameName �t���[����
 *  @param inSlotName �X���b�g��
 *  @param inSlotValue �X���b�g�l
 */
public
void writeSlotValue(
 String inFrameName,
 String inSlotName,
 Object inSlotValue )
{
 AIFrame frame = (AIFrame) mFrames.get( inFrameName );
 frame.writeSlotValue( this, inSlotName, inSlotValue );
}


// demon procedure �̐ݒ�

/**
 * setWhenConstructedProc
 *  when-constructed procedure ��ݒ肷��D
 */
public
void setWhenConstructedProc(
 String inFrameName,
 String inSlotName,
 AIWhenConstructedProc inDemonProc )
{
 AIFrame frame = (AIFrame) mFrames.get( inFrameName );
 if ( frame != null )
  frame.setWhenConstructedProc( inDemonProc );
}

public
void setWhenConstructedProc(
 String inFrameName,
 String inSlotName,
 String inClassName )
{
 try {
  AIWhenConstructedProc demonProc =
    (AIWhenConstructedProc) Class.forName( inClassName ).newInstance();
  AIFrame frame = (AIFrame) mFrames.get( inFrameName );
  if ( frame != null )
   frame.setWhenConstructedProc( demonProc );
 } catch ( Exception err ) {
  System.out.println( err );
 }
}


/**
 * setWhenRequestedProc
 *  when-requested procedure ��ݒ肷��D
 */
public
void setWhenRequestedProc(
 String inFrameName,
 String inSlotName,
 AIDemonProc inDemonProc )
{
 setDemonProc( AISlot.WHEN_REQUESTED, inFrameName,
  inSlotName, inDemonProc );
}

public
void setWhenRequestedProcClass(
 String inFrameName,
 String inSlotName,
 String inClassName )
{
 setDemonProcClass( AISlot.WHEN_REQUESTED,
  inFrameName, inSlotName, inClassName );
}


/**
 * setWhenReadProc
 *  when-read procedure ��ݒ肷��D
 */
public
void setWhenReadProc(
 String inFrameName,
 String inSlotName,
 AIDemonProc inDemonProc )
{
 setDemonProc( AISlot.WHEN_READ,
  inFrameName, inSlotName, inDemonProc );
}

public
void setWhenReadProcClass(
 String inFrameName,
 String inSlotName,
 String inClassName )
{
 setDemonProcClass( AISlot.WHEN_READ,
  inFrameName, inSlotName, inClassName );
}


/**
 * setWhenWrittenProc
 *  when-written procedure ��ݒ肷��D
 */
public
void setWhenWrittenProc(
 String inFrameName,
 String inSlotName,
 AIDemonProc inDemonProc )
{
 setDemonProc( AISlot.WHEN_WRITTEN,
  inFrameName, inSlotName, inDemonProc );
}

public
void setWhenWrittenProcClass(
 String inFrameName,
 String inSlotName,
 String inClassName )
{
 setDemonProcClass( AISlot.WHEN_WRITTEN,
  inFrameName, inSlotName, inClassName );
}


/*
 * setDemonProc
 *  demon procedure ��ݒ肷��D
 */
void setDemonProc(
 int inType,
 String inFrameName,
 String inSlotName,
 AIDemonProc inDemonProc )
{
 AIFrame frame = (AIFrame) mFrames.get( inFrameName );
 if ( frame != null )
  frame.setDemonProc( inType, inSlotName, inDemonProc );
}


/*
 * setDemonClass
 *  demon procedure ��ݒ肷��D
 */
void setDemonProcClass(
 int inType,
 String inFrameName,
 String inSlotName,
 String inClassName )
{
 try {
  AIDemonProc demon =
   (AIDemonProc) Class.forName( inClassName ).newInstance();
  setDemonProc( inType, inFrameName, inSlotName, demon );
 } catch ( Exception err ) {
  System.out.println( err );
 }
}

} // end of class definition