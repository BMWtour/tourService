package com.lion.BMWtour.config;

import com.lion.BMWtour.service.NcpGeolocationService;
import com.lion.BMWtour.service.NcpMapApiService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**naver cloud platform 관련 설정*/
@Configuration
public class NcpClientConfig {
    //web map 사용
    @Value("${ncp.api.client-id}")
    String clientId;
    @Value("${ncp.api.client-secret}")
    String ncpMapClientSecret;
    //geoLocation 사용
    @Value("${ncp.api.api-access}")
    private String accessKey;
    //api-secret
    @Value("${ncp.api.api-secret}")
    private String secretKey;

    //geoLocation 헤더 상수화
    private static final String X_TIMESTAMP_HEADER = "x-ncp-apigw-timestamp";
    private static final String X_IAM_ACCESS_KEY = "x-ncp-iam-access-key";
    private static final String X_APIGW_SIGNATURE = "x-ncp-apigw-signature-v2";
    
    @Bean
    public RestClient ncpMapClient() {
        return RestClient
                .builder()
                .baseUrl("https://naveropenapi.apigw.ntruss.com")
                .defaultHeader("X-NCP-APIGW-API-KEY-ID", clientId)
                .defaultHeader("X-NCP-APIGW-API-KEY", ncpMapClientSecret)
                .build();
    }

    @Bean
    public RestClient ncpGeoLocationClient(){
        return RestClient
                .builder()
                .baseUrl("https://geolocation.apigw.ntruss.com/geolocation/v2/geoLocation")
                //실제로 보내지기 전에, 헤더가 추가되는 등의 작업이 진행된다
                .requestInitializer(request -> {
                    //여러개의 헤더 담기
                    HttpHeaders requestHeaders = request.getHeaders();
                    //1. 요청을 보내는 Unix Time
                    long timestamp = System.currentTimeMillis();
                    requestHeaders.add(X_TIMESTAMP_HEADER, Long.toString(timestamp));
                    //2. Acess key
                    requestHeaders.add(X_IAM_ACCESS_KEY, accessKey);
                    //3. 현재시각 + 요청 URI + 요청 메서드 정보로 만드는 시그니처
                    //메서드로 만들어 넣기
                    //시그니처를 만드는데 필요한 정보는 request에 있다.
                    requestHeaders.add(X_APIGW_SIGNATURE, makeSignature(
                            request.getMethod(),
                            request.getURI().getPath() + "?" + request.getURI().getQuery(),
                            timestamp
                    ));
                })
                .build();
    }
    //딴데서 편하게 쓸라고, 위 RestClient 설정 박아서 빈 등록
    @Bean
    public NcpGeolocationService geolocationService(){
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(ncpGeoLocationClient()))
                .build()
                .createClient(NcpGeolocationService.class);
    }

    //딴데서 편하게 쓸라고, 위 RestClient 설정 박아서 빈 등록
    @Bean
    public NcpMapApiService mapApiService(){
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(ncpMapClient()))
                .build()
                .createClient(NcpMapApiService.class);
    }

    //시그니처를 만드는 메서드
    private String makeSignature(HttpMethod method, String url, long timestamp) {
        String space = " ";
        String newLine = "\n";

        String message = new StringBuilder()
                .append(method.name())
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        try {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

            return Base64.encodeBase64String(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
