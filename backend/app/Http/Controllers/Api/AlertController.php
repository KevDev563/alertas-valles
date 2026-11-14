<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Alert;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class AlertController extends Controller
{
    public function index(Request $request): JsonResponse
    {
        $alerts = Alert::query()
            ->with('zone')
            ->where('active', true)
            ->orderByDesc('created_at')
            ->limit(100)
            ->get();

        return response()->json(['data' => $alerts]);
    }

    public function store(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'zone_id' => ['nullable', 'exists:zones,id'],
            'title' => ['required', 'string', 'max:255'],
            'description' => ['nullable', 'string'],
            'level' => ['nullable', 'in:info,warning,danger'],
            'source' => ['nullable', 'string', 'max:50'],
            'active' => ['nullable', 'boolean'],
        ]);

        $alert = Alert::query()->create([
            'zone_id' => $validated['zone_id'] ?? 1,
            'title' => $validated['title'],
            'description' => $validated['description'] ?? null,
            'level' => $validated['level'] ?? 'info',
            'source' => $validated['source'] ?? 'manual',
            'active' => $validated['active'] ?? true,
            'starts_at' => now(),
        ]);

        return response()->json($alert->load('zone'), 201);
    }
}
