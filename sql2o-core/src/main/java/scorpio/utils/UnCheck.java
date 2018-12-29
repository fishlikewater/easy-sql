package scorpio.utils;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class UnCheck {

    public static <T> T wrap(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void wrap(T t, Consumer<T> consumer) {
        try {
            consumer.accept(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
