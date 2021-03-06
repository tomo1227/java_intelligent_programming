
import java.util.*;

/**
 * 文全体の文節のリスト．文節間の係り受け構造を保持するクラス．
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
     * 主辞の文節を返す
     * @return 主辞の文節
     */
    public Chunk getHeadChunk() {
        return head;
    }
    
    /**
     * 動作主格の文節を返す
     * @return 動作主格の文節
     */
    public Chunk getAgentCaseChunk() {
        // 主辞に係る文節の中からガ格の文節を探す
        Chunk cand = findChunkByHead(head.getDependents(), "助詞", "が");
        if (cand != null) return cand;
        // 主辞に係る文節の中からハ格の文節を探す
        cand = findChunkByHead(head.getDependents(), "助詞", "は");
        if (cand != null) return cand;
        // 全ての文節の中からガ格の文節を探す
        cand = findChunkByHead(this, "助詞", "が");
        if (cand != null) return cand;
        // 全ての文節の中からハ格の文節を探す
        cand = findChunkByHead(this, "助詞", "は");
        return cand;
    }
    
    /**
     * 指定した品詞・原型の形態素を主辞に持つ文節を文節リストから探して返す
     * @param chunks 文節リスト
     * @param pos 探しててる文節の主辞形態素の品詞
     * @param baseform 探している文節の主辞形態素の原型
     * @return 見つかった文節
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
     * この文に含まれる文節間の係り受け構造を表すXML風の文字列を返す
     * @return XML風の文字列
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
