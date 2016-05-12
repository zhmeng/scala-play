import com.google.common.cache.*;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangmeng on 16-5-11.
 */


public class GuavaTest{
    @Test
    public void test() throws Exception{
        LoadingCache<String, String> cacheBuilder = CacheBuilder
                .newBuilder()
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        String strProValue = "hello " + key + "!";
                        return strProValue;
                    }
                });
        System.out.println("Jerry: " + cacheBuilder.apply("jerry"));
        System.out.println("Jerry: " + cacheBuilder.get("jerry"));

        System.out.println("Peida: " + cacheBuilder.get("peida"));
        System.out.println("Peida: " + cacheBuilder.apply("peida"));

        System.out.println("lisa value: " + cacheBuilder.apply("lisa"));

        cacheBuilder.put("harry", "ssdded");

        System.out.println("harry value: " + cacheBuilder.get("harry"));
    }

    @Test
    public void testcallableCache() throws Exception {
        Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(1000).build();
        String resultVal = cache.get("jerry", new Callable<String>() {
            @Override
            public String call() throws Exception {
                String strProValue = "hello " + "jerry" + " !";
                return strProValue;
            }
        });
        System.out.println("jerry value: " + resultVal);

        resultVal = cache.get("peida", new Callable<String>() {
            @Override
            public String call() throws Exception {
                String strProValue = "hello " + "peida" + " !";
                return strProValue;
            }
        });
        System.out.println("peida value: " + resultVal);
    }

    public <K, V>LoadingCache<K, V> cached(CacheLoader<K, V> cacheLoader){
        LoadingCache<K, V> cache = CacheBuilder
                .newBuilder()
                .maximumSize(2)
                .weakKeys()
                .softValues()
                .refreshAfterWrite(120, TimeUnit.SECONDS)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener(new RemovalListener<K, V>() {
                    @Override
                    public void onRemoval(RemovalNotification<K, V> removalNotification) {
                        System.out.println(removalNotification.getKey() + " 被移除");
                    }
                }).build(cacheLoader);
        return cache;
    }

    public LoadingCache<String, String> commonCache(final String key) throws Exception {
        LoadingCache<String, String> commonCache = cached(new CacheLoader<String, String>() {
            @Override
            public String load(String s) throws Exception {
                return "hello " + s + "!";
            }
        });
        return commonCache;
    }

    @Test
    public void testCache() throws Exception {
        LoadingCache<String, String> commonCache = commonCache("peida");
        System.out.println("peida: " + commonCache.get("peida"));
        commonCache.apply("harry");
        System.out.println("harry:" + commonCache.get("harry"));
        commonCache.apply("lisa");
        System.out.println("lisa:" + commonCache.get("lisa"));
        TimeUnit.SECONDS.sleep(2);
    }
}
