import com.ruoyi.wms.stock.vo.DryInBillTrayVo;
import io.netty.util.internal.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;


public class TestRedis {
    static class Parent{

        public  void call(){
            System.out.println("aaaaaaaaaaaaa");

        }
    }


    static class A extends Parent{

        @Override
        public void call() {
            System.out.println("A");
        }
    }

    static class B extends Parent{
        @Override
        public void call() {
            System.out.println("B");
        }
    }



    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException {


//        Parent a = new A();
//        Parent b = new B();
//        a.call();
//        b.call();
//        Test test = new Test();
//        Method add = test.getClass().getDeclaredMethod("add",String.class);
//        add.invoke(test,"aaaaaaainvoke");
//        DryInBillTrayVo o = (DryInBillTrayVo) Class.forName("com.ruoyi.wms.stock.vo.DryInBillTrayVo").newInstance();
//        DryInBillTrayVo test = DryInBillTrayVo.class.newInstance();
//        Field[] declaredFields = test.getClass().getDeclaredFields();
//        for (Field declaredField : declaredFields) {
//            System.out.println(declaredField);
//        }



    }

}
