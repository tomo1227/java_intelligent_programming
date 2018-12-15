
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smackx.muc.*;
import org.xml.sax.SAXException;

import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.*;

/**
 * �ʐM���M���b�Z�[�W�̏����ȂǁC�`���b�g�{�b�g�Ŕėp�I�Ɏg���@�\�������������ۃN���X�D
 */
public abstract class AbstractChatbot implements MessageListener, Runnable {

    /**
     * �`�ԑf���API��URL
     */
    protected String maServURL;

    /**
     * �W��󂯉��API��URL
     */
    protected String daServURL;

    /**
     * XMPP�T�[�o��URL
     * (Chatbot�N���X��XMPP_SERVER�Ŏw��)
     */
    public String xmppServer;
    
    /**
     *  Yahoo Web API �̃A�v���P�[�V����ID 
     * (Chatbot�N���X��YAHOO_APP_ID�Ŏw��)
     */
    protected String yahooAppId;

    /**
     *  �`���b�g�{�b�g�̖��O 
     * (Chatbot�N���X��BOT_NAME�Ŏw��)
     */
    protected String botName;

    /**
     * �`���b�g�{�b�g�̃p�X���[�h 
     * (Chatbot�N���X��BOT_PASS�Ŏw��)
     */
    protected String botPass;
    
    /**
     * �`���b�g����
     */
    protected List<Message> history;
    
    /**
     * ����`���b�g�ōŌ�ɔ����������� (�~���b) ���L�^����Map
     */
    protected Map<Chat, Long> lastUttTimeMap;
    
    /**
     * �b���U��܂łɒ��ق�ς��鎞�� (�b)
     */
    protected int silenceLimit = 60;
    
    /**
     * Proxy�̃z�X�g��
     */
    public String proxyHost;

    /**
     * Proxy�̃|�[�g�ԍ�
     */
    public String proxyPort;

    protected SAXParser saxParser;
    protected SimpleDateFormat sdf;
    
    protected XMPPConnection conn;
    protected ChatManager chatMan;
    protected Thread botThread;
    
    protected Map<String,Chat> chatMap;
    protected Map<String,MultiUserChat> chatRoomMap;
    
   
    /**
     * �R���X�g���N�^ 
     */
    public AbstractChatbot(String yahooAppId, 
                           String botName, 
                           String botPass, 
                           String xmppServer,
                           String proxyHost,
                           String proxyPort) {
        this.yahooAppId = yahooAppId;
        this.botName = botName;
        this.botPass = botPass;
        this.xmppServer = xmppServer;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        maServURL = "http://jlp.yahooapis.jp/MAService/V1/parse?appid=" 
            + yahooAppId + "&response=surface,reading,pos,baseform,feature&sentence=";
        daServURL = "http://jlp.yahooapis.jp/DAService/V1/parse?appid="
            + yahooAppId + "&sentence=";
        if (proxyHost != null && ! proxyHost.isEmpty()) {
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", proxyPort);
        }
        conn = new XMPPConnection(new ConnectionConfiguration(xmppServer, 5222));
        chatMap = new HashMap<String, Chat>();
        chatRoomMap = new HashMap<String, MultiUserChat>();
        initSAXParser();
        sdf = new SimpleDateFormat("HH:mm:ss ");
        history = new ArrayList<Message>();
        lastUttTimeMap = new HashMap<Chat, Long>();
    }
    
    
    /**
     * ��M�������b�Z�[�W����������
     * @param chat 
     * @param message ��M�������b�Z�[�W
     */
    public void processMessage(Chat chat, Message message) {
        history.add(message);
        String senderName = message.getFrom();
        System.out.println(sdf.format(new Date()) + 
            senderName+"����̃��b�Z�[�W: "+message.getBody());
        recordUtteranceTime(chat);
        String resText = createResponse(senderName, message.getBody());
        try {
            // 0.5�b�҂�
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // do nothing
        }
        // �������b�Z�[�W�𑗂�
        sendMessage(chat, resText);

    }
    
    
    /**
     * sender���痈�����b�Z�[�W�ɑ΂���ԓ����𐶐����C�Ԃ�
     * @param sender ���b�Z�[�W�̑��M�� (�A�J�E���g��@XMPP�T�[�o��)
     * @param text ���b�Z�[�W
     * @return �ԓ����镶
     */
    public abstract String createResponse(String sender, String text);

    /**
     * target �Ɍ����Ĕ��M���郁�b�Z�[�W�̕��𐶐����C�Ԃ�
     * @param target ����@(�A�J�E���g��@XMPP�T�[�o��)
     * @return ���M���镶
     */
    public abstract String createMessage(String target);
    
    
    /**
     * SAXParser ��������
     */
    private void initSAXParser() {
        try {
            saxParser = SAXParserFactory.newInstance().newSAXParser();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * ���O�C��
     */
    public void login() {
        try {
            conn.connect();
            SASLAuthentication.supportSASLMechanism("PLAIN", 0);
            conn.login(botName, botPass);
            chatMan = conn.getChatManager();
            System.out.println(xmppServer + "��"+botName+"�Ƃ��ă��O�C�����܂��� ");
        } catch (XMPPException e) {
            System.err.println("[AbstractChatbot#login] " +
                "XMPP�T�[�o�ւ̃��O�C���Ɏ��s���܂���");
            e.printStackTrace();
        }
    }
    
    public Chat createChatWith(String targetName) {
        Chat chat = chatMan.createChat(targetName, this);
        chatMap.put(targetName, chat);
        return chat;
    }
    
    public Chat getChatWith(String targetName) {
        Chat chat = chatMap.get(targetName);
        if (chat != null) {
            return chat;
        } else {
            chat = createChatWith(targetName);
            return chat;
        }
    }
    
    /**
     * createMessage�����������������b�Z�[�W�Ƃ��đ���
     * @param targetName ���b�Z�[�W�̑��M��
     */
    public void sendMessage(String targetName) {
        sendMessage(targetName, createMessage(targetName));
    }
    
    /**
     * ���b�Z�[�W�𑗂�
     * @param targetName ���b�Z�[�W�̑��M��
     * @param text ���b�Z�[�W
     */
    public void sendMessage(String targetName, String text) {
        Chat chat = getChatWith(targetName);
        Message message = new Message(targetName);
        message.setBody(text);
        try {
            chat.sendMessage(message);
            recordUtteranceTime(chat);
            System.out.println(sdf.format(new Date()) +
                targetName + "�փ��b�Z�[�W�𑗐M: " + text);
        } catch (XMPPException e) {
            System.err.println("[AbstractChatbot#sendMessage] " +
                targetName + "�ւ̃��b�Z�[�W���M�Ɏ��s���܂���: " + text);
        }
    }

    /**
     * ���b�Z�[�W�𑗂�
     * @param chat ���M����`���b�g
     * @param text ���b�Z�[�W
     */
    public void sendMessage(Chat chat, String text) {
        String targetName = chat.getParticipant();
        Message message = new Message(targetName);
        message.setBody(text);
        try {
            chat.sendMessage(message);
            recordUtteranceTime(chat);
            System.out.println(sdf.format(new Date()) +
                targetName + "�փ��b�Z�[�W�𑗐M: " + text);
        } catch (XMPPException e) {
            System.err.println("[AbstractChatbot#sendMessage] " +
                targetName + "�ւ̃��b�Z�[�W���M�Ɏ��s���܂���: " + text);
        }
    }
    

    
    /**
     * Yahoo�`�ԑf��͂̌��ʂ�Morpheme�I�u�W�F�N�g�̃��X�g�ɂ��ĕԂ�
     * @param text ��͂��������{��̕�
     * @return �`�ԑf��͂̌���
     */
    public List<Morpheme> analyzeMorpheme(String text) {
        try {
            String encoded = URLEncoder.encode(text, "UTF-8");
            URL url = new URL(maServURL + encoded);
            URLConnection conn = url.openConnection();
            MorphSAXHandler maHandler = new MorphSAXHandler();
            saxParser.parse(conn.getInputStream(), maHandler);
            return maHandler.getMorphemes();
        } catch (UnsupportedEncodingException e) {
            // nothing to do
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // nothing to do
            e.printStackTrace();
        } catch (SAXException e) {
            System.err.println("[AbstractChatBot#analyzeMorpheme] " +
            "Yahoo �`�ԑf��� Web API ���Ԃ�����͌���XML�̉��߂Ɏ��s���܂���");
        } catch (IOException e) {
            System.err.println("[AbstractChatBot#analyzeMorpheme] " +
            "Yahoo �`�ԑf��� Web API �ւ̐ڑ��Ɏ��s���܂���");
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Yahoo�`�ԑf��͂̌��ʂ�XML�����̂܂ܕԂ�
     * @param text
     * @return �`�ԑf��͌��ʂ�XML���̂܂�
     */
    public String getYahooMorphemeAnalysisXML(String text) {
        StringBuffer sb = new StringBuffer();
        try {
            String encoded = URLEncoder.encode(text, "UTF-8");
            URL url = new URL(maServURL + encoded);
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    
   
    /**
     * Yahoo�W��󂯉�͂̌��ʂ�XML�����̂܂ܕԂ�
     * @param text
     * @return �W��󂯉�͌��ʂ�XML���̂܂�
     */
    public String getYahooDependencyAnalysisXML(String text) {
        StringBuffer sb = new StringBuffer();
        try {
            String encoded = URLEncoder.encode(text, "UTF-8");
            URL url = new URL(daServURL + encoded);
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Yahoo�W��󂯉�͂̌��ʂ�ChunkDependencyTree�I�u�W�F�N�g�ɂ��ĕԂ�
     * @param text ��͂��������{��̕�
     * @return �W��󂯉�͂̌���
     */
    public Sentence analyzeDependency(String text) {
        try {
            String encoded = URLEncoder.encode(text, "UTF-8");
            URL url = new URL(daServURL + encoded);
            URLConnection conn = url.openConnection();
            DepSAXHandler depHandler = new DepSAXHandler();
            saxParser.parse(conn.getInputStream(), depHandler);
            return depHandler.getDependencyTree();
        } catch (UnsupportedEncodingException e) {
            // nothing to do
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // nothing to do
            e.printStackTrace();
        } catch (SAXException e) {
            System.err.println("[AbstractChatBot#analyzeDependency] " +
            "Yahoo �W��󂯉�� Web API ���Ԃ�����͌���XML�̉��߂Ɏ��s���܂���");
        } catch (IOException e) {
            System.err.println("[AbstractChatBot#analyzeDependency] " +
            "Yahoo �W��󂯉�� Web API �ւ̐ڑ��Ɏ��s���܂���");
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * ���b�������L�^
     * @param name ������
     */
    protected void recordUtteranceTime(Chat chat) {
        lastUttTimeMap.put(chat, System.currentTimeMillis());
    }
    
    /**
     * �b���U��܂łɒ��ق�ς��鎞�Ԃ�ݒ�
     * @param sec �b��
     */
    public void setSilenceLimit(int sec) {
        silenceLimit = sec;
    }
    
    /**
     * Web����R���e���c���擾���C�������Ԃ��D
     * @param url 
     * @return HTML���̃R���e���c�̕�����
     */
    public String getWebContent(String url) {
        StringBuffer sb = new StringBuffer();
        try {
            URLConnection conn = new URL(url).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Web����R���e���c���擾���C�������Ԃ��D�R���e���c�̕����R�[�h���w�肷��D
     * @param url URL
     * @param enc �����R�[�h
     * @return HTML���̃R���e���c�̕�����
     */
    public String getWebContent(String url, String enc) {
        StringBuffer sb = new StringBuffer();
        try {
            URLConnection conn = new URL(url).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), enc));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    

    
    /**
     * 
     */
    public void start() {
        if (botThread == null) { 
            botThread = new Thread(this);
            botThread.start();
        }
    }
    
    /**
     * 
     */
    public void run() {
        Thread current = Thread.currentThread();
        while (botThread == current){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e){
                // do nothing
            } finally {
                Iterator<Chat> i = chatMap.values().iterator();
                while (i.hasNext()) {
                    Chat chat = i.next();
                    if (isWhenToSay(chat)) {
                        sendMessage(chat.getParticipant());
                    }
                }
            }
        }
    }
    
    /**
     * ���̃��\�b�h���Ăяo���ꂽ�u�Ԃ����b�Z�[�W�𑗐M���ׂ��^�C�~���O�Ȃ�true, 
     * �����łȂ����false��Ԃ�
     * @param chat
     * @return ���b�Z�[�W�𑗐M���ׂ��^�C�~���O���ۂ�
     */
    protected boolean isWhenToSay(Chat chat) {
        Long lastUttTime = lastUttTimeMap.get(chat);
        if (lastUttTime == null) {
            recordUtteranceTime(chat);
            lastUttTime = lastUttTimeMap.get(chat);
        }
        // �Ō�̔�������̌o�ߎ��Ԃ��`�F�b�N
        if (System.currentTimeMillis() - lastUttTime > silenceLimit * 1000) {
            return true;
        } else {
            return false;
        }
        
    }
    
}
