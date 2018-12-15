
import java.util.*;

/**
 * ���߂�\���N���X
 */
public class Chunk extends ArrayList<Morpheme> {

    /**
     * ���ߔԍ�
     */
    int id;
    
    /**
     * �W���̕���
     */
    Chunk dependencyChunk;
    
    /**
     * �W���̕��ߔԍ�
     */
    int dependency;
    
    /**
     * ���̕��߂ɌW�镶�߂̃��X�g
     */
    List<Chunk> dependents;

    
    public Chunk() {
        super();
        dependents = new LinkedList<Chunk>();
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setDependency(int dependency) {
        this.dependency = dependency;
    }

    public void setMorphemes(List<Morpheme> morphs) {
        for (Iterator<Morpheme> i = iterator(); i.hasNext(); ) {
            remove(i.next());
        }
        addAll(morphs);
    }
    
    public void addMorpheme(Morpheme morph) {
        add(morph);
    }
    
    public void addDependentChunk(Chunk chunk) {
        dependents.add(chunk);
    }

    public void setDependencyChunk(Chunk chunk) {
        dependencyChunk = chunk;
    }

    /**
     * ���̕��߂̎厫�̌`�ԑf�����ւ���
     * @param morph �Z�b�g����`�ԑf
     */
    public void replaceHeadMorpheme(Morpheme morph) {
        set(size()-1, morph);
    }
    
    /**
     * ���̕��߂̕��ߔԍ���Ԃ�
     * @return ���ߔԍ�
     */
    public int getId() {
        return id;
    }
    
    /**
     * ���̕��߂��W�镶�߂�id��Ԃ�
     * @return ���̕��߂��W�镶�߂�id
     */
    public int getDependency() {
        return dependency;
    }

    /**
     * ���̕��߂��W�镶�߂�Ԃ�
     * @return ���̕��߂��W�镶��
     */
    public Chunk getDependencyChunk() {
        return dependencyChunk;
    }
    
    /**
     * ���̕��߂ɌW�镶�߂̃��X�g��Ԃ�
     * @return ���̕��߂ɌW�镶�߂̃��X�g
     */
    public List<Chunk> getDependents() {
        return dependents;
    }

    /**
     * �厫�ł���Ō�̌`�ԑf��Ԃ�
     * @return �厫�ł���Ō�̌`�ԑf
     */
    public Morpheme getHeadMorpheme() {
        return get(size()-1);
    }
     
    /**
     * ���̕��߂̕\�w�������Ԃ�
     * @return ���̕��߂̕\�w������
     */
    public String getSurface() {
        StringBuffer sb = new StringBuffer();
        for (Iterator<Morpheme> i = iterator(); i.hasNext(); ) {
            sb.append(i.next().getSurface());
        }
        return sb.toString();
    }
    
    /**
     * ���̕��߂Ɋ܂܂��`�ԑf�̃��X�g��\��XML���̕������Ԃ�
     * @return XML���̕�����
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<Chunk id=\"" + id);
        sb.append("\" dependency=\"" + dependency);
        if (! dependents.isEmpty()) {
            sb.append("\" dependents=\"");
            for (Iterator<Chunk> i = dependents.iterator(); i.hasNext(); ) {
                sb.append(i.next().getId());
                if (i.hasNext()) {
                    sb.append(",");
                }
            }
        }
        sb.append("\">\n");
        for (Iterator<Morpheme> i = iterator(); i.hasNext(); ) {
            sb.append(" ");
            sb.append(i.next().toString());
            sb.append("\n");
        }
        sb.append("</Chunk>");
        return sb.toString();
    }
}
