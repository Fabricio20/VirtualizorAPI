package net.notfab.virtualizor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.notfab.virtualizor.entities.VNCInfo;
import net.notfab.virtualizor.entities.VirtualServer;
import net.notfab.virtualizor.entities.VzBackup;
import net.notfab.virtualizor.entities.VzPlan;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class VirtualizorAPI {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .followSslRedirects(true)
            .followRedirects(true)
            .hostnameVerifier((s, sslSession) -> true)
            .build();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String URL;
    private String PASS;

    public VirtualizorAPI(String url, String keyPass) {
        this.URL = url;
        this.PASS = keyPass;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private String call(Map<String, String> params, boolean post) {
        String apiKey = this.getAPIK(this.generateRandStr(), this.PASS);

        StringBuilder url = new StringBuilder(URL + "/index.php")
                .append("&api=json")
                .append("&apikey=").append(apiKey)
                .append("&act=").append(params.get("act"));
        params.remove("act");
        Request request;
        if (post) {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            params.forEach(builder::addFormDataPart);
            request = new Request.Builder().url(url.toString().replaceFirst("&", "?"))
                    .addHeader("User-Agent", "GalaxyGate/1.0")
                    .post(builder.build())
                    .build();
        } else {
            params.forEach((k, v) -> url.append("&").append(k).append("=").append(v));
            request = new Request.Builder().url(url.toString().replaceFirst("&", "?"))
                    .addHeader("User-Agent", "GalaxyGate/1.0")
                    .build();
        }

        try (Response response = this.client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            return body.string();
        } catch (Exception ex) {
            logger.error("Error requesting to Virtualizor API", ex);
            return null;
        }
    }

    /**
     * Returns the status of a Virtual Servers.
     *
     * @param vpsId Ids of the Virtual Servers.
     * @return Virtual Server status (Online or Offline).
     */
    public Map<String, Boolean> getStatus(Long... vpsId) {
        Map<String, String> params = new HashMap<>();
        params.put("act", "vs");
        params.put("vs_status", Arrays.stream(vpsId).map(String::valueOf).collect(Collectors.joining(",")));

        String response = this.call(params, false);
        if (response == null) {
            return new HashMap<>();
        }
        try {
            JsonNode node = objectMapper.readTree(response);
            Map<String, Boolean> status = new HashMap<>();
            node.get("status").fieldNames().forEachRemaining(x -> {
                status.put(x, node.get("status").get(x).get("status").asInt() == 1);
            });
            return status;
        } catch (JsonProcessingException ex) {
            logger.error("Error parsing vps status", ex);
            return new HashMap<>();
        }
    }

    /**
     * Finds a VPS by Hostname.
     *
     * @param hostname - Hostname of the VPS.
     * @return VirtualServer or null.
     */
    public VirtualServer getServerByHostname(String hostname) {
        Map<String, String> params = new HashMap<>();
        params.put("act", "vs");
        params.put("vpshostname", hostname);

        String response = this.call(params, true);
        if (response == null) {
            return null;
        }
        try {
            AtomicReference<VirtualServer> server = new AtomicReference<>();
            objectMapper.readTree(response).get("vs").elements().forEachRemaining(node -> {
                try {
                    server.set(objectMapper.readValue(node.toString(), VirtualServer.class));
                } catch (JsonProcessingException ex) {
                    logger.error("Error parsing server", ex);
                }
            });
            return server.get();
        } catch (JsonProcessingException ex) {
            logger.error("Error parsing vps status", ex);
            return null;
        }
    }

    /**
     * Finds a VPS by id.
     *
     * @param id - Id of the VPS.
     * @return VirtualServer or null.
     */
    public VirtualServer getServerById(long id) {
        Map<String, String> params = new HashMap<>();
        params.put("act", "vs");
        params.put("vpsid", String.valueOf(id));

        String response = this.call(params, true);
        if (response == null) {
            return null;
        }
        try {
            AtomicReference<VirtualServer> server = new AtomicReference<>();
            objectMapper.readTree(response).get("vs").elements().forEachRemaining(node -> {
                try {
                    server.set(objectMapper.readValue(node.toString(), VirtualServer.class));
                } catch (JsonProcessingException ex) {
                    logger.error("Error parsing server", ex);
                }
            });
            return server.get();
        } catch (JsonProcessingException ex) {
            logger.error("Error parsing vps status", ex);
            return null;
        }
    }

    /**
     * Lists all VPSes which belong to a specific user.
     *
     * @param email - Email.
     * @return List of VirtualServers.
     */
    public List<VirtualServer> getServersForUser(String email) {
        Map<String, String> params = new HashMap<>();
        params.put("act", "vs");
        params.put("user", email);

        String response = this.call(params, true);
        if (response == null) {
            return new ArrayList<>();
        }
        try {
            List<VirtualServer> serverList = new ArrayList<>();
            objectMapper.readTree(response).get("vs").elements().forEachRemaining(node -> {
                try {
                    VirtualServer server = objectMapper.readValue(node.toString(), VirtualServer.class);
                    serverList.add(server);
                } catch (JsonProcessingException ex) {
                    logger.error("Error parsing server", ex);
                }
            });
            return serverList;
        } catch (JsonProcessingException ex) {
            logger.error("Error parsing vps status", ex);
            return new ArrayList<>();
        }
    }

    public boolean start(long vpsId) {
        Map<String, String> params = new HashMap<>();
        params.put("act", "vs");
        params.put("action", "start");
        params.put("vpsid", String.valueOf(vpsId));

        String response = this.call(params, false);
        if (response == null) {
            logger.warn("Unknown response from VPS start (vId = " + vpsId + ")");
            return false;
        }
        try {
            return objectMapper.readTree(response)
                    .get("done")
                    .asBoolean(false);
        } catch (JsonProcessingException ex) {
            logger.error("Error starting VPS " + vpsId, ex);
            return false;
        }
    }

    public boolean stop(long vpsId, boolean force) {
        Map<String, String> params = new HashMap<>();
        params.put("act", "vs");
        if (force) {
            params.put("action", "poweroff");
        } else {
            params.put("action", "stop");
        }
        params.put("vpsid", String.valueOf(vpsId));

        String response = this.call(params, false);
        if (response == null) {
            logger.warn("Unknown response from VPS stop (vId = " + vpsId + ")");
            return false;
        }
        try {
            return objectMapper.readTree(response)
                    .get("done")
                    .asBoolean(false);
        } catch (JsonProcessingException ex) {
            logger.error("Error stopping VPS " + vpsId, ex);
            return false;
        }
    }

    public boolean restart(long vpsId) {
        Map<String, String> params = new HashMap<>();
        params.put("act", "vs");
        params.put("action", "restart");
        params.put("vpsid", String.valueOf(vpsId));

        String response = this.call(params, false);
        if (response == null) {
            logger.warn("Unknown response from VPS restart (vId = " + vpsId + ")");
            return false;
        }
        try {
            return objectMapper.readTree(response)
                    .get("done")
                    .asBoolean(false);
        } catch (JsonProcessingException ex) {
            logger.error("Error restarting VPS " + vpsId, ex);
            return false;
        }
    }

    public VNCInfo getVNC(long vpsId) {
        Map<String, String> params = new HashMap<>();
        params.put("act", "vnc");
        params.put("novnc", String.valueOf(vpsId));

        String response = this.call(params, true);
        if (response == null) {
            logger.warn("Unknown response from VNC details (vId = " + vpsId + ")");
            return null;
        }
        try {
            return objectMapper.treeToValue(objectMapper.readTree(response)
                    .get("info"), VNCInfo.class);
        } catch (JsonProcessingException ex) {
            logger.error("Error fetching VNC information " + vpsId, ex);
            return null;
        }
    }

    public List<VzPlan> getPlans() {
        Map<String, String> params = new HashMap<>();
        params.put("act", "plans");
        try {
            List<VzPlan> planList = new ArrayList<>();
            int page = 1;
            do {
                params.put("page", String.valueOf(page));
                String response = this.call(params, false);
                if (response == null) {
                    return planList;
                }
                if (!objectMapper.readTree(response).has("plans")) {
                    break;
                }
                objectMapper.readTree(response).get("plans").elements().forEachRemaining(node -> {
                    try {
                        VzPlan plan = objectMapper.readValue(node.toString(), VzPlan.class);
                        planList.add(plan);
                    } catch (JsonProcessingException ex) {
                        logger.error("Error parsing Plan", ex);
                    }
                });
                page++;
            } while (true);
            return planList;
        } catch (JsonProcessingException ex) {
            logger.error("Error parsing plan", ex);
            return new ArrayList<>();
        }
    }

    public List<VzBackup> getBackups(long vpsId) {
        String responseBody;
        String apiKey = this.getAPIK(this.generateRandStr(), this.PASS);

        Request request;
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        builder.addFormDataPart("vpsid", String.valueOf(vpsId));
        String url = URL + "/index.php" +
                "?api=json" +
                "&apikey=" + apiKey +
                "&act=vpsrestore&op=get_vps";
        request = new Request.Builder().url(url)
                .addHeader("User-Agent", "GalaxyGate/1.0")
                .post(builder.build())
                .build();
        try (Response response = this.client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return new ArrayList<>();
            }
            ResponseBody body = response.body();
            if (body == null) {
                return new ArrayList<>();
            }
            responseBody = body.string();
        } catch (Exception ex) {
            logger.error("Error requesting to Virtualizor API", ex);
            return new ArrayList<>();
        }
        // --
        try {
            List<VzBackup> planList = new ArrayList<>();
            JsonNode node = objectMapper.readTree(responseBody);
            if (!node.has("backup_list") || !node.has("vps_backup_server")) {
                return planList;
            }
            long server = Long.parseLong(node.get("vps_backup_server").asText());
            node.get("backup_list").fields().forEachRemaining((x) -> {
                Date date;
                try {
                    date = new SimpleDateFormat("yyyyMMdd").parse(x.getKey());
                } catch (ParseException e) {
                    return;
                }
                JsonNode array = x.getValue();
                array.elements().forEachRemaining(y -> {
                    VzBackup backup;
                    try {
                        backup = objectMapper.readValue(y.toString(), VzBackup.class);
                        backup.setServer(server);
                        backup.setDate(date);
                        planList.add(backup);
                    } catch (JsonProcessingException ignored) {
                    }
                });
            });
            return planList;
        } catch (JsonProcessingException ex) {
            logger.error("Error parsing Backups", ex);
            return new ArrayList<>();
        }
    }

    public String getMetrics(long vpsId) {
        Map<String, String> params = new HashMap<>();
        params.put("act", "vps_stats");
        params.put("show", "1");
        params.put("vpsid", String.valueOf(vpsId));

        String response = this.call(params, true);
        if (response == null) {
            logger.warn("Unknown response from VPS Metrics (vId = " + vpsId + ")");
            return null;
        }
        try {
            return response;
        } catch (Exception ex) {
            logger.error("Error fetching VPS Metrics " + vpsId, ex);
        }
        return null;
    }

    public String getNodeMetrics(String yyyyMM) {
        Map<String, String> params = new HashMap<>();
        params.put("act", "server_stats");
        if (yyyyMM == null) {
            yyyyMM = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        }
        params.put("show", yyyyMM);

        String response = this.call(params, false);
        if (response == null) {
            logger.warn("Unknown response from Node Metrics");
            return null;
        }
        try {
            return response;
        } catch (Exception ex) {
            logger.error("Error fetching VPS Metrics", ex);
        }
        return null;
    }

    // https://www.virtualizor.com/admin-api/manage-vps
    public boolean update(long vpsId, Map<String, String> settings) {
        Map<String, String> params = new HashMap<>();
        params.put("act", "managevps");
        params.put("vpsid", String.valueOf(vpsId));
        params.put("editvps", "1");
        params.putAll(settings);

        String response = this.call(params, true);
        if (response == null) {
            logger.warn("Unknown response from Manage VPS (vId = " + vpsId + ")");
            return false;
        }
        try {
            JsonNode node = objectMapper.readTree(response);
            return node.has("done") && node.get("done").has("done")
                    && node.get("done").get("done").asBoolean()
                    // No errors
                    && (node.has("error") && node.get("error").isEmpty());
        } catch (Exception ex) {
            logger.error("Error fetching VPS Metrics " + vpsId, ex);
        }
        return false;
    }

    private String getAPIK(String key, String pass) {
        return key + md5(pass + key);
    }

    private String generateRandStr() {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int rand = new Random().nextInt(61);
            if (rand < 10) {
                token.append((char) (rand + 48));
            } else if (rand < 36) {
                token.append((char) (rand + 55));
            } else {
                token.append((char) (rand + 61));
            }
        }
        return token.toString().toLowerCase();
    }

    private String md5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Failed to generate MD5 Key for Virtualizor", ex);
        }
        return null;
    }

}
