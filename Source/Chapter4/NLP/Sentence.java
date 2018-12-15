
import java.util.*;

/**
 * ���S�̂̕��߂̃��X�g�D���ߊԂ̌W��󂯍\����ێ�����N���X�D
 */
public class Sentence extends ArrayList<Chunk> {

    Chunk head;
    
    public Sentence(List<Chunk> chunks) {
        super(chunks);
        Iterator<Chunk> i = chunks.iterator();
        while (i.hasNext()) {
            Chunk chunk = i.next();  
            int dependency = chunk.getDependency();
            if (dependency == -1) {
                head = chunk;
            } else {
                Chunk depChunk = chunks.get(dependency);
                depChunk.addDependentChunk(chunk);
                chunk.setDependencyChunk(depChunk);
            }
        }
    }
    
    /**
     * �厫�̕��߂�Ԃ�
     * @return �厫�̕���
     */
    public Chunk getHeadChunk() {
        return head;
    }
    
    /**
     * �����i�̕��߂�Ԃ�
     * @return �����i�̕���
     */
    public Chunk getAgentCaseChunk() {
        // �厫�ɌW�镶�߂̒�����K�i�̕��߂�T��
        Chunk cand = findChunkByHead(head.getDependents(), "����", "��");
        if (cand != null) return cand;
        // �厫�ɌW�镶�߂̒�����n�i�̕��߂�T��
        cand = findChunkByHead(head.getDependents(), "����", "��");
        if (cand != null) return cand;
        // �S�Ă̕��߂̒�����K�i�̕��߂�T��
        cand = findChunkByHead(this, "����", "��");
        if (cand != null) return cand;
        // �S�Ă̕��߂̒�����n�i�̕��߂�T��
        cand = findChunkByHead(this, "����", "��");
        return cand;
    }
    
    /**
     * �w�肵���i���E���^�̌`�ԑf���厫�Ɏ����߂𕶐߃��X�g����T���ĕԂ�
     * @param chunks ���߃��X�g
     * @param pos �T���ĂĂ镶�߂̎厫�`�ԑf�̕i��
     * @param baseform �T���Ă��镶�߂̎厫�`�ԑf�̌��^
     * @return ������������
     */
    private Chunk findChunkByHead(List<Chunk> chunks, String pos, String baseform) {
        for (Iterator<Chunk> i = chunks.iterator(); i.hasNext(); ) {
            Chunk chunk = i.next();
            Morpheme head = chunk.getHeadMorpheme();
            if (pos.equals(head.getPos()) && baseform.equals(head.getBaseform())) {
                return chunk;
            }
        }
        return null;
    }
    
    /**
     * ���̕��Ɋ܂܂�镶�ߊԂ̌W��󂯍\����\��XML���̕������Ԃ�
     * @return XML���̕�����
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<Sentence head=\""+ head.getId() +"\">\n");
        for (Iterator<Chunk> i = iterator(); i.hasNext(); ) {
            sb.append(i.next().toString());
            sb.append("\n");
        }
        sb.append("</Sentence>");
        return sb.toString();
    }
    
}
