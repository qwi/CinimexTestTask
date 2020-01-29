import com.cinimex.tasks.FileManager;
import org.junit.Assert;
import org.junit.Test;

public class testFileManager {

    @Test
    public void testAddObjectInCacheAndGetItAndDelete() {
        FileManager<String, String> cache = new FileManager<>(10000);
        String testKey = "testKey";
        String testValue = "testValue";

        Assert.assertEquals(0, cache.size());
        cache.put(testKey, testValue);
        Assert.assertEquals(testValue, cache.get(testKey));
        Assert.assertEquals(1, cache.size());
        cache.remove(testKey);
        Assert.assertEquals(0, cache.size());
    }
}
