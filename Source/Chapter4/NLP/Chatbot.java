import java.util.*;
import java.util.regex.*;




/**
 * �`���b�g�{�b�g�̃N���X
 */
public class Chatbot extends AbstractChatbot {
    
    // Yahoo Web API �̃A�v���P�[�V����ID 
    // (�����Ŏ擾�����A�v���P�[�V����ID�ɕύX����)
    private static String YAHOO_APP_ID = "Y.6xN_K......";
    
    // �`���b�g�T�[�o��URL 
    // (XMPP�T�[�o�������Ă���z�X�g���ɕύX����)
    public static String XMPP_SERVER = "localhost";
    
    // �`���b�g�{�b�g�̃��O�C����
    // (�D���Ȗ��O�ɕύX����)
    private static String BOT_NAME = "your_bot_name";
    
    // �`���b�g�{�b�g�̃��O�C���p�X���[�h
    // (�ݒ肵���p�X���[�h�ɕύX����)
    private static String BOT_PASS = "apple";
    
    // �`���b�g�{�b�g�Ƙb�����[�U�̃��O�C����
    // (�D���Ȗ��O�ɕύX����)
    private static String USER_NAME = "your_name";

    // �v���L�V�T�[�o��URL�@(�K�v�Ȃ�ݒ肷��)
    public static String PROXY_HOST = "";

    // �v���L�V�T�[�o�̃|�[�g�ԍ��@(�K�v�Ȃ�ݒ肷��)
    public static String PROXY_PORT = "";
    
    // 
    /**
     * �ŏ��ɌĂяo�����main���\�b�h
     */
    public static void main(String[] args) throws Exception {
        // Chatbot�N���X�̃I�u�W�F�N�g�����C�X���b�h���J�n
        Chatbot chatbot = new Chatbot();
        chatbot.start();
        
        // ���ق�ς���b�����Z�b�g
        chatbot.setSilenceLimit(30);
        
        // �`���b�g�T�[�o�փ��O�C��
        chatbot.login();
        
        //chatbot.createChatWith(USER_NAME + "@" + XMPP_SERVER); 
        // �N������createMessage�Ő����������b�Z�[�W�𑗂�
        chatbot.sendMessage(USER_NAME + "@" + XMPP_SERVER);  
    }
    
    /**
     * sender���痈�����b�Z�[�W�ɑ΂���ԓ����𐶐����C�Ԃ�
     * @param sender ���b�Z�[�W�̑��M�� (�A�J�E���g��@�`���b�g�T�[�o��)
     * @param text ���b�Z�[�W
     * @return �ԓ����镶
     */
    public String createResponse(String sender, String text) {
        // �`�ԑf���
        List<Morpheme> morphs = analyzeMorpheme(text);
        System.out.println("�`�ԑf��͂̌���: "+ morphs);
        
        // �W��󂯉��
        Sentence sentence = analyzeDependency(text);
        System.out.println("�W��󂯉�͂̌���:\n"+ sentence);
 
        // �ł��P���Ȏ�����: �I�E���Ԃ�
        String senderName = sender.substring(0, sender.indexOf("@"));
        return senderName + "���񂪁u" + text + "�v�ƌ�����";
    }

    /**
     * target �Ɍ����Ĕ��M���郁�b�Z�[�W�̕��𐶐����C�Ԃ�
     * @param target ����@(�A�J�E���g��@�`���b�g�T�[�o��)
     * @return ���M���镶
     */ 
    public String createMessage(String target) {
        // �ł��P���Ȏ�����: �Ăт����邾��
        String targetName = target.substring(0, target.indexOf("@"));
        return targetName + "����A����ɂ���";
    }	

    /**
     * �R���X�g���N�^
     */
    public Chatbot() {
        super(YAHOO_APP_ID, BOT_NAME, BOT_PASS, XMPP_SERVER, PROXY_HOST, PROXY_PORT);
    }


}
