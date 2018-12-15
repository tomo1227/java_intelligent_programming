
/**
 * �`�ԑf�N���X
 */
public class Morpheme {

    /**
     * �\�w�I������
     */
    String surface;
    
    /**
     * �ǂ�
     */
    String reading;
    
    /**
     * �i��
     */
    String pos;
    
    /**
     * ���^
     */
    String baseform;
    
    /**
     * ����
     */
    String feature;

    /**
     * �R���X�g���N�^
     */
    public Morpheme() {
        super();
    }

    /**
     * �R���X�g���N�^
     */
    public Morpheme(String surface, String reading, String pos, String baseform) {
        this.surface = surface;
        this.reading = reading;
        this.pos = pos;
        this.baseform = baseform;
        feature = "";
    }
    
    /**
     * �R�s�[�R���X�g���N�^
     */
    public Morpheme(Morpheme morph) {
        this.surface = morph.surface;
        this.reading = morph.reading;
        this.pos = morph.pos;
        this.baseform = morph.baseform;
        this.feature = morph.feature;
    }


    
    /**
     * �����Ȃ�true, �����łȂ����false��Ԃ�
     * @return �������ۂ�
     */
    public boolean isVerb() {
        return pos.equals("����");
    }


    public void setSurface(String surface) {
        this.surface = surface;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public void setBaseform(String baseform) {
        this.baseform = baseform;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    /**
     * ���̌`�ԑf�̕\�w�I�������Ԃ�
     * @return �\�w�I������
     */
    public String getSurface() {
        return surface;
    }

    /**
     * ���̌`�ԑf�̓ǂ݂�Ԃ�
     * @return �ǂ�
     */
    public String getReading() {
        return reading;
    }

    /**
     * ���̌`�ԑf�̕i����Ԃ�
     * @return �i��
     */
    public String getPos() {
        return pos;
    }

    /**
     * ���̌`�ԑf�̌��^��Ԃ�
     * @return ���^
     */
    public String getBaseform() {
        return baseform;
    }

    /**
     * ���̌`�ԑf�̓��������킹���������Ԃ�
     * @return ���������킹��������
     */
    public String getFeature() {
        return feature;
    }

    /**
     * ���̌`�ԑf�̏���S�č��킹��XML�I�ɕ\�������������Ԃ�
     * @return �`�ԑf����S�č��킹��XML�I�ɕ\������������
     */
    public String toString() {
        return "<Morpheme surface=\""+surface+"\" reading=\""+reading+"\" pos=\""+pos+"\" baseform=\""+baseform+"\" feature=\""+feature+"\" />";
    }
}
