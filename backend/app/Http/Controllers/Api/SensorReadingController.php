<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\SensorReading;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class SensorReadingController extends Controller
{
    public function latest(Request $request): JsonResponse
    {
        $estacion = $request->query('estacion', 'Estacion_02');

        $lastFecha = SensorReading::query()
            ->where('estacion', $estacion)
            ->max('fecha');

        if (! $lastFecha) {
            return response()->json(['estacion' => $estacion, 'fecha' => null, 'readings' => []]);
        }

        $readings = SensorReading::query()
            ->where('estacion', $estacion)
            ->where('fecha', $lastFecha)
            ->get(['sensor_name', 'sensor_dato', 'fecha']);

        return response()->json([
            'estacion' => $estacion,
            'fecha' => $lastFecha,
            'readings' => $readings,
        ]);
    }

    public function index(Request $request): JsonResponse
    {
        $estacion = $request->query('estacion', 'Estacion_02');
        $sensor = $request->query('sensor_name');
        $limit = min((int) $request->query('limit', 100), 500);

        $query = SensorReading::query()->where('estacion', $estacion);
        if ($sensor) {
            $query->where('sensor_name', $sensor);
        }

        $readings = $query->orderByDesc('fecha')->limit($limit)->get();

        return response()->json(['data' => $readings]);
    }
}
