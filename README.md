# Alertas Valles

Proyecto MVP: **backend (Laravel API)** + **android** (app cliente).

## Estructura
- `backend/` – API en Laravel (SQLite) con lecturas de sensores, alertas, mensajes, auth y chat con Gemini.
- `android/` – carpeta reservada para la app Android.

## Backend (Laravel)

### Requisitos
- PHP y Composer (detectados vía Herd)
- SQLite (incluido, sin instalar MySQL)

### Configuración
1. Entra a `backend/` y revisa `.env`.
2. Agrega tu clave de Gemini (Google AI Studio) para habilitar el chat:
   ```env
   GEMINI_API_KEY=tu_clave_aqui
   GEMINI_MODEL=gemini-1.5-flash
   ```
   > No compartas ni subas tu clave al repositorio.

### Ejecutar
```bash
cd backend
php artisan serve
```
API disponible en `http://localhost:8000`.

### Datos incluidos
- Zona: **Región Valles** (`region-valles`), única por ahora.
- Usuario demo: `demo@alertas-valles.test` / `password` (suscrito a Región Valles).
- Lecturas importadas desde `database/data/datos.csv`.
- Alertas y mensajes de ejemplo.

Para reimportar el CSV:
```bash
php artisan sensor:import --truncate
```

### Endpoints principales (prefijo `/api`)
- `POST /register` – registro (`name`, `email`, `password`) y suscripción a Región Valles.
- `POST /login` – inicio de sesión, devuelve token.
- `GET /me` – usuario autenticado (Bearer token).
- `POST /logout` – cerrar sesión (Bearer token).
- `GET /readings/latest?estacion=Estacion_02` – última lectura por sensor.
- `GET /readings?estacion=Estacion_02&sensor_name=Temperatura&limit=100` – historial.
- `GET /alerts` – alertas activas.
- `POST /alerts` – crear alerta (Bearer token).
- `GET /messages` – avisos/mensajes.
- `POST /chat` – chat con IA (`message`, opcional `history[]` con `role`/`content`).

## Próximos pasos sugeridos
- **Android:** crear proyecto en `android/` que consuma estos endpoints (login, lecturas, alertas, avisos, chat).
- **Telegram (complemento):** bot/n8n que consulte `/api/alerts` y `/api/messages` para enviar resúmenes/novedades.
- **n8n + Gemini:** flujos para generar/registrar alertas vía `POST /api/alerts` o enriquecer avisos.
- **ngrok (solo desarrollo):** exponer `php artisan serve` con una URL pública temporal para webhooks de Telegram/n8n.
- **Servidor CUVALLES:** cuando haya acceso, agregar un comando/job que obtenga lecturas nuevas e inserte en `sensor_readings`.

## Notas
- Por ahora no se incluye versión web: el foco es **API + Android** (y Telegram como complemento).
- Credenciales de servidores compartidas previamente no deben hardcodearse; usar `.env`.
