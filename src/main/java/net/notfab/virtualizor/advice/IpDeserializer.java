package net.notfab.virtualizor.advice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import net.notfab.virtualizor.models.InetAddr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class  IpDeserializer extends JsonDeserializer<List<InetAddr>> {

    @Override
    public List<InetAddr> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        List<InetAddr> addrList = new ArrayList<>();
        node.fields().forEachRemaining(pair -> {
            String pool = pair.getKey();
            String ip = pair.getValue().asText();
            InetAddr addr = new InetAddr();
            addr.setAddress(ip);
            addr.setPoolId(Long.parseLong(pool));
            addrList.add(addr);
        });
        return addrList;
    }

}
