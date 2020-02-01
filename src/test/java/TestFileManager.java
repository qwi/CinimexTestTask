import com.cinimex.tasks.FileManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestFileManager {

    @Test
    public void testAddObjectInCache() {
        FileManager<String, String> cache = new FileManager<>(TimeUnit.SECONDS.toMillis(10));
        Assert.assertEquals(0, cache.size());
        cache.put("testKey", "testValue");
        Assert.assertEquals(1, cache.size());
    }

    @Test
    public void testAddObjectAndGetIt() {
        FileManager<String, String> cache = new FileManager<>(TimeUnit.SECONDS.toMillis(10));
        cache.put("testKey", "testValue");
        Assert.assertEquals("testValue", cache.get("testKey"));
    }

    @Test
    public void testAddObjectAndRemoveIt() {
        FileManager<String, String> cache = new FileManager<>(TimeUnit.SECONDS.toMillis(10));
        cache.put("testKey", "testValue");
        cache.remove("testKey");
        Assert.assertEquals(0, cache.size());
    }

    @Test
    public void testAddSeveralObjectsAndRemoveAll() {
        FileManager<String, String> cache = new FileManager<>(TimeUnit.SECONDS.toMillis(10));
        for (int i = 0; i < 5; i++) {
            cache.put("testKey" + i, "testValue" + i);
        }
        cache.removeAll();
        Assert.assertEquals(0, cache.size());
    }

    @Test
    public void testDoContainsItem() {
        FileManager<String, String> cache = new FileManager<>(TimeUnit.SECONDS.toMillis(10));
        cache.put("testKey", "testValue");
        Assert.assertTrue(cache.containsItem("testKey"));
    }

    @Test
    public void testDoContainsItemAfterDelete() {
        FileManager<String, String> cache = new FileManager<>(TimeUnit.SECONDS.toMillis(10));
        cache.put("testKey", "testValue");
        cache.remove("testKey");
        Assert.assertFalse(cache.containsItem("testKey"));
    }

    /**
     * Тест на автоматическую проверку элементов кэша и удаление "протухших"
     */
    @Test
    public void testRemoveExpiredElementsIfTimeLiveEnd() throws InterruptedException {
        FileManager<String, String> cache = new FileManager<>(TimeUnit.SECONDS.toMillis(1));
        cache.put("testKey", "testValue");
        System.out.println("testValue exist? " + cache.containsItem("testKey"));
        Assert.assertTrue(cache.containsItem("testKey"));
        Thread.sleep(1000);
        System.out.println("testValue exist? " + cache.containsItem("testKey"));
        Assert.assertFalse(cache.containsItem("testKey"));
    }

    @Test
    public void testSetTimeToLiveInRunTime() {
        FileManager<String, String> cache = new FileManager<>(TimeUnit.SECONDS.toMillis(1));
        Assert.assertEquals(1000, cache.getTimeToLive());

        cache.setDefaultTimeToLive(TimeUnit.HOURS.toMillis(1));
        Assert.assertEquals(TimeUnit.HOURS.toMillis(1), cache.getTimeToLive());
    }

    @Test
    public void testUpdatingObjectCreationDate() throws InterruptedException {
        FileManager<String, String> cache = new FileManager<>(TimeUnit.SECONDS.toMillis(5));
        cache.put("testKey", "testValue");
        long firstCreationDate = cache.getTimeToLiveForElement("testKey");
        Thread.sleep(10);
        Assert.assertTrue(firstCreationDate < System.currentTimeMillis());
        cache.refreshCreationDate("testKey", System.currentTimeMillis());
        long secondCreationDate = cache.getTimeToLiveForElement("testKey");
        Thread.sleep(10);
        Assert.assertTrue(secondCreationDate < System.currentTimeMillis());
    }
}
