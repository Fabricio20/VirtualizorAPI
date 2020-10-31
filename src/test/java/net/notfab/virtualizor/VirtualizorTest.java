package net.notfab.virtualizor;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VirtualizorTest {

    private Virtualizor api;

    @BeforeAll
    public void beforeAll() {
        this.api = new Virtualizor(new OkHttpClient.Builder().build(),
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

}
