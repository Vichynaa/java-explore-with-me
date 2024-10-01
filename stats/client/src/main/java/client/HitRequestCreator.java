package client;

import dto.HitRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HitRequestCreator {
    public static HitRequest hitRequestCreator(String ip, String uri, String app, String timestamp) {
        HitRequest hitRequest = new HitRequest();
        hitRequest.setIp(ip);
        hitRequest.setUri(uri);
        hitRequest.setApp(app);
        hitRequest.setTimestamp(timestamp);
        return hitRequest;
    }
}
