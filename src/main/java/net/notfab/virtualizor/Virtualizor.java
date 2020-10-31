package net.notfab.virtualizor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.notfab.virtualizor.models.InetAddr;
import net.notfab.virtualizor.models.IpPool;
import net.notfab.virtualizor.models.VPS;
import okhttp3.OkHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Virtualizor {

    private final UnderlyingAPI api;
    private final ObjectMapper objectMapper;

    public Virtualizor(OkHttpClient okHttpClient, String url, String password) {
        this.api = new UnderlyingAPI(okHttpClient, url, password);
        this.objectMapper = new ObjectMapper();
    }

    public Optional<VPS> findByHostname(String hostname) {
        JSONObject object = new JSONObject();
        object.put("vpshostname", hostname);
        try (InputStream stream = this.api.post("vs", object)) {
            JsonNode node = this.objectMapper.readTree(stream);
            JsonNode vps = node.get("vs").elements().next();
            VPS server = this.objectMapper.treeToValue(vps, VPS.class);
            return Optional.of(server);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<VPS> findById(long id) {
        JSONObject object = new JSONObject();
        object.put("vpsid", String.valueOf(id));
        try (InputStream stream = this.api.post("vs", object)) {
            JsonNode node = this.objectMapper.readTree(stream);
            JsonNode vps = node.get("vs").elements().next();
            VPS server = this.objectMapper.treeToValue(vps, VPS.class);
            return Optional.of(server);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<VPS> findByIp(String ip) {
        JSONObject object = new JSONObject();
        object.put("vpsip", ip);
        try (InputStream stream = this.api.post("vs", object)) {
            JsonNode node = this.objectMapper.readTree(stream);
            JsonNode vps = node.get("vs").elements().next();
            VPS server = this.objectMapper.treeToValue(vps, VPS.class);
            return Optional.of(server);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public List<VPS> findByEmail(String email) {
        JSONObject object = new JSONObject();
        object.put("user", email);
        List<VPS> vpsList = new ArrayList<>();
        try (InputStream stream = this.api.post("vs", object)) {
            JsonNode node = this.objectMapper.readTree(stream);
            node.get("vs").elements().forEachRemaining(vps -> {
                try {
                    VPS server = this.objectMapper.treeToValue(vps, VPS.class);
                    vpsList.add(server);
                } catch (JsonProcessingException e) {
                    log.error("Failed to parse vps", e);
                }
            });
            return vpsList;
        } catch (IOException e) {
            return vpsList;
        }
    }

    public boolean start(long id) {
        JSONObject object = new JSONObject();
        object.put("action", "start");
        object.put("vpsid", String.valueOf(id));
        try (InputStream stream = this.api.get("vs", object)) {
            JsonNode node = this.objectMapper.readTree(stream);
            return node.get("done").asBoolean(false);
        } catch (IOException e) {
            return false;
        }
    }

    public boolean stop(long id, boolean force) {
        JSONObject object = new JSONObject();
        if (force) {
            object.put("action", "poweroff");
        } else {
            object.put("action", "stop");
        }
        object.put("vpsid", String.valueOf(id));
        try (InputStream stream = this.api.get("vs", object)) {
            JsonNode node = this.objectMapper.readTree(stream);
            return node.get("done").asBoolean(false);
        } catch (IOException e) {
            return false;
        }
    }

    public boolean restart(long id) {
        JSONObject object = new JSONObject();
        object.put("action", "restart");
        object.put("vpsid", String.valueOf(id));
        try (InputStream stream = this.api.get("vs", object)) {
            JsonNode node = this.objectMapper.readTree(stream);
            return node.get("done").asBoolean(false);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Updates a VPS, check API docs for the fields
     * https://www.virtualizor.com/admin-api/manage-vps
     */
    public boolean update(long id, JSONObject params) {
        JSONObject object = new JSONObject();
        object.put("vpsid", String.valueOf(id));
        try (InputStream stream = this.api.post("managevps", object)) {
            JsonNode node = this.objectMapper.readTree(stream);
            return node.get("done").asBoolean(false);
        } catch (IOException e) {
            return false;
        }
    }

    public List<IpPool> findIpPools(long page) {
        JSONObject object = new JSONObject();
        object.put("reslen", "50");
        object.put("page", String.valueOf(page));
        List<IpPool> poolList = new ArrayList<>();
        try (InputStream stream = this.api.get("ippool", object)) {
            JsonNode node = this.objectMapper.readTree(stream);
            node.get("ippools").elements().forEachRemaining(pool -> {
                try {
                    IpPool ipPool = this.objectMapper.treeToValue(pool, IpPool.class);
                    poolList.add(ipPool);
                } catch (JsonProcessingException e) {
                    log.error("Failed to parse vps", e);
                }
            });
            return poolList;
        } catch (IOException e) {
            return poolList;
        }
    }

    public List<InetAddr> findIps(long pool, long page) {
        JSONObject object = new JSONObject();
        object.put("reslen", "50");
        object.put("page", String.valueOf(page));
        object.put("ippid", String.valueOf(pool));
        List<InetAddr> poolList = new ArrayList<>();
        try (InputStream stream = this.api.get("ips", object)) {
            JsonNode node = this.objectMapper.readTree(stream);
            node.get("ips").elements().forEachRemaining(ip -> {
                try {
                    InetAddr addr = this.objectMapper.treeToValue(ip, InetAddr.class);
                    poolList.add(addr);
                } catch (JsonProcessingException e) {
                    log.error("Failed to parse ip", e);
                }
            });
            return poolList;
        } catch (IOException e) {
            return poolList;
        }
    }

}
