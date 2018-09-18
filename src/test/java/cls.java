import cn.simplifydb.sequence.ISequence;
import cn.simplifydb.sequence.impl.IdSequence;

/**
 * Created by jiangzeyin on 2018/6/23.
 */
public class cls {
    public static void main(String[] args) {
        OrderedProperties orderedProperties = new OrderedProperties();

        System.out.println(ISequence.class.isInterface());
        System.out.println(cls.class.isAssignableFrom(cls.class));
        Integer a = 1;
        System.out.println(a instanceof Integer);
        System.out.println(IdSequence.class.isInterface());
        System.out.println(ISequence.class.isAssignableFrom(ISequence.class));
    }
}
