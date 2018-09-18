import cn.simplifydb.sequence.IQuietSequence;
import cn.simplifydb.sequence.SequenceConfig;
import cn.simplifydb.sequence.impl.IdSequence;

/**
 * Created by jiangzeyin on 2017/12/6.
 */
public class test {

    public static void main(String[] args) {
        // SequenceConfig.config(1, 2);
        //ISequence iSequence = new DateSequence(-1, -2);
        //ISequence iSequence1 = new IdSequence(-1, -2);


//        for (int i = 0; i <= 10; i++) {
//            String id = IdSequence.instance().nextId();
//            String id2 = DateSequence.instance().nextId();
//            System.out.println(id + "  " + IdSequence.instance().parseInfo(id));
//            System.out.print(id2);
//            System.out.println("  " + DateSequence.instance().parseInfo(id2));
//        }
//        System.out.println(DateFormat.getTimeMessage(14754770456L));
//
//        System.out.println(0x7fffffffffffffffL);
        String id = ((IQuietSequence) SequenceConfig.parseSequence(IdSequence.class)).nextId();
        System.out.println(id);
        System.out.println(((IQuietSequence) SequenceConfig.parseSequence(IdSequence.class)).parseInfo(id));
    }


}
