# Implementasi API Key Security

## Overview
Implementasi keamanan berbasis API Key untuk melindungi endpoint tertentu di aplikasi Vehicle Rental.

## Komponen

### 1. ApiKeyFilter (`security/ApiKeyFilter.java`)
Filter yang mengecek validitas API Key untuk endpoint tertentu.

**Protected Endpoints:**
- `POST /api/loyalty/coupons/use`
- `POST /api/loyalty/points/add`

**Cara Kerja:**
1. Filter mengecek apakah request path cocok dengan protected endpoints
2. Jika cocok, filter akan memvalidasi header `API-KEY`
3. Jika header tidak ada atau tidak valid, return 401 Unauthorized
4. Jika valid, request dilanjutkan ke controller

### 2. SecurityConfig (`config/SecurityConfig.java`)
Konfigurasi yang mendaftarkan `ApiKeyFilter` ke dalam aplikasi Spring Boot.

## Konfigurasi

### application.yaml
```yaml
api:
  key: your-secret-api-key-here
```

Atau set via environment variable:
```bash
export API_KEY=your-secret-api-key-here
```

## Cara Menggunakan

### 1. Request TANPA API Key (akan ditolak)
```bash
curl -X POST http://localhost:8082/api/loyalty/points/add \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "123",
    "points": 100
  }'
```

**Response:**
```json
{
  "status": 401,
  "message": "API Key is required",
  "timestamp": "Thu Nov 28 10:00:00 WIB 2025",
  "data": null
}
```

### 2. Request DENGAN API Key yang SALAH (akan ditolak)
```bash
curl -X POST http://localhost:8082/api/loyalty/points/add \
  -H "Content-Type: application/json" \
  -H "API-KEY: wrong-key" \
  -d '{
    "customerId": "123",
    "points": 100
  }'
```

**Response:**
```json
{
  "status": 401,
  "message": "Invalid API Key",
  "timestamp": "Thu Nov 28 10:00:00 WIB 2025",
  "data": null
}
```

### 3. Request DENGAN API Key yang BENAR (akan diterima)
```bash
curl -X POST http://localhost:8082/api/loyalty/points/add \
  -H "Content-Type: application/json" \
  -H "API-KEY: your-secret-api-key-here" \
  -d '{
    "customerId": "123",
    "points": 100
  }'
```

**Response:**
```json
{
  "status": 200,
  "message": "Loyalty points added successfully",
  "timestamp": "Thu Nov 28 10:00:00 WIB 2025",
  "data": {
    "customerId": "123",
    "pointsAdded": 100,
    "totalPoints": 100
  }
}
```

## Testing dengan Postman

1. Buat request baru dengan method POST
2. URL: `http://localhost:8082/api/loyalty/points/add`
3. Headers:
   - `Content-Type`: `application/json`
   - `API-KEY`: `your-secret-api-key-here`
4. Body (raw JSON):
   ```json
   {
     "customerId": "123",
     "points": 100
   }
   ```

## Menambah Protected Endpoint Baru

Untuk menambah endpoint lain yang perlu API Key, edit file `ApiKeyFilter.java`:

```java
private static final List<String> PROTECTED_PATHS = Arrays.asList(
    "/api/loyalty/coupons/use",
    "/api/loyalty/points/add",
    "/api/new/protected/endpoint"  // Tambahkan endpoint baru di sini
);
```

## Catatan Keamanan

1. **Jangan commit API Key ke Git**: Pastikan API Key disimpan di environment variable atau secret management
2. **Gunakan HTTPS di Production**: API Key harus dikirim melalui HTTPS untuk mencegah intersepsi
3. **Rotate API Key secara berkala**: Ganti API Key secara periodik untuk keamanan
4. **Logging**: Filter mencatat semua upaya akses ke protected endpoints untuk audit

## Troubleshooting

### Error: "API Key is required"
- Pastikan header `API-KEY` dikirim dalam request
- Perhatikan case sensitivity: harus `API-KEY`, bukan `api-key` atau `Api-Key`

### Error: "Invalid API Key"
- Periksa nilai API Key di `application.yaml` atau environment variable
- Pastikan tidak ada spasi atau karakter tersembunyi di API Key

### Endpoint tidak terproteksi
- Periksa apakah path endpoint sudah terdaftar di `PROTECTED_PATHS`
- Pastikan method HTTP sesuai (harus POST)
- Cek log aplikasi untuk melihat apakah filter dieksekusi
