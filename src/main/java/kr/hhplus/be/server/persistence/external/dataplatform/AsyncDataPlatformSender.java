package kr.hhplus.be.server.persistence.external.dataplatform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class AsyncDataPlatformSender {

    private final HttpClient httpClient;
    private final String platformUrl;

    public AsyncDataPlatformSender(String platformUrl) {
        // HTTP/2를 사용하며, 여러 비동기 요청을 처리할 수 있는 클라이언트 생성
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.platformUrl = platformUrl;
    }

    /**
     * 데이터를 비동기적으로 전송합니다.
     * @param data 전송할 데이터 (JSON 문자열 형식)
     * @return 전송 성공 여부를 담는 CompletableFuture
     */
    public CompletableFuture<Boolean> sendDataAsync(String data) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(platformUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();

        // sendAsync()를 사용하여 비동기 요청을 시작하고 CompletableFuture를 즉시 반환
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    // thenApply는 HTTP 요청이 완료된 후 실행됩니다.
                    // 이 블록에서 응답 상태 코드를 확인하여 성공 여부를 반환합니다.
                    return response.statusCode() >= 200 && response.statusCode() < 300;
                })
                .exceptionally(e -> {
                    // exceptionally는 비동기 작업 중 예외가 발생했을 때 실행됩니다.
                    return false;
                });
    }
}