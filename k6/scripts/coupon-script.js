import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

// 선착순 쿠폰 발급 부하 테스트 시나리오
export const options = {
  scenarios: {
    warmup: {
      executor: 'constant-arrival-rate',
      rate: 100,
      timeUnit: '1s',
      duration: '1m',
      preAllocatedVUs: 150,
      maxVUs: 300,
      startTime: '0s',
    },
    concurrent_coupon_issuance: {
        executor: 'constant-arrival-rate',    // 실행기(고정 도달율: RPS를 일정하게 유지)
        rate: 400,                            // 목표 도달률: 초당 400개의 요청
        timeUnit: '1s',                       // rate 단위: 1초당
        duration: '5m',                       // 총 실행 시간: 5분
        preAllocatedVUs: 400,                 // 미리 확보할 VU(가상 사용자) 수
        maxVUs: 1000,                         // 필요 시 늘어날 수 있는 VU 최대치
        startTime: '1m',                      // 워밍업 종료 후 본테스트 진행
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<3000'],        // 응답 시간 95%가 3초 미만이어야 통과
    http_req_failed: ['rate<0.01'],           // 실패율이 1% 미만이어야 통과
  },
};

export default function () {
  // K6 실행 시 URL주입하여 호출
  const baseUrl = __ENV.BASE_URL;

  const payload = JSON.stringify({
    userId: randomIntBetween(1, 100000),  // 랜덤한 userId
    couponId: 1,  // 발급할 쿠폰 ID (예: 1번 쿠폰)
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(baseUrl, payload, params);

  // 응답 상태 코드 체크
  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(Math.random() * 2 + 1);
}

export function handleSummary(data) {
  return {
    'summary.json': JSON.stringify(data, null, 2),
  };
}
