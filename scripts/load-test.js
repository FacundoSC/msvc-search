import http from 'k6/http';
import { check } from 'k6';

const baseUrl = __ENV.BASE_URL || 'http://localhost:8081';
const seedCount = Number(__ENV.SEED_COUNT || 1000000);

export default function () {
    const n = (((__VU - 1) * 10000 + __ITER) % seedCount) + 1;
    const searchId = `load-${String(n).padStart(7, '0')}`;
    const response = http.get(`${baseUrl}/count?searchId=${searchId}`, {
        tags: { name: 'GET /count' },
    });

    check(response, {
        'respuesta HTTP 200': (r) => r.status === 200,
    });
}