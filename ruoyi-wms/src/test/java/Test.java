import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        System.out.println(1.0-0.32);
        BigDecimal bigDecimal = new BigDecimal(Double.toString(1.0));
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(0.32));
        BigDecimal result = bigDecimal.subtract(bigDecimal1);
        System.out.println(result.doubleValue());
    }

    public  void add(String a){

        System.out.println(a);
    }
}
