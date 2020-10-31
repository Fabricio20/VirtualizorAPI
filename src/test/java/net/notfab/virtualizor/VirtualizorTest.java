package net.notfab.virtualizor;

import okhttp3.OkHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VirtualizorTest {

    private Virtualizor api;

    @BeforeAll
    public void beforeAll() {
        this.api = new Virtualizor(new OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS).build(),
                System.getenv("API_URL"), System.getenv("API_KEY"));
    }

    @Test
    public void findByHostname() {
        assert this.api.findByHostname("gg-cache").isPresent();
    }

    @Test
    public void findById() {
        assert this.api.findById(1221).isPresent();
    }

    @Test
    public void findByIp() {
        assert this.api.findByIp("10.20.10.222").isPresent();
    }

    @Test
    public void findByEmail() {
        assert this.api.findByEmail("admin@test.com").isEmpty();
    }

    @Test
    void findIpPools() {
        assert !this.api.findIpPools(1).isEmpty();
    }

    @Test
    void findIps() {
        assert !this.api.findIps(22, 1).isEmpty();
    }

    @Test
    void update() {
        JSONArray array = new JSONArray();
        array.put("192.168.0.2");
        array.put("192.168.0.3");
        JSONObject object = new JSONObject();
        object.put("ips[]", array);
        object.put("editvps", "1");
        assert this.api.update(2624, object);
    }

}
