import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  vus: 10,           // 가상 유저 수 (동시 사용자)
  duration: '30s',   // 테스트 지속 시간
};

export default function () {
  http.get('http://spring-app:8080/coupons/issuing');
  sleep(1); // 요청 간격
}