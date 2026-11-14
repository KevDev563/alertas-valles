<?php

namespace App\Services;

use Illuminate\Support\Facades\Http;

class GeminiService
{
    public function chat(string $prompt, array $history = []): string
    {
        $apiKey = config('services.gemini.key');

        if (! $apiKey) {
            return 'La API de Gemini no está configurada. Agrega GEMINI_API_KEY en el archivo .env.';
        }

        $contents = [];
        foreach ($history as $item) {
            $role = ($item['role'] ?? 'user') === 'assistant' ? 'model' : 'user';
            $contents[] = [
                'role' => $role,
                'parts' => [['text' => (string) ($item['content'] ?? '')]],
            ];
        }
        $contents[] = [
            'role' => 'user',
            'parts' => [['text' => $prompt]],
        ];

        $model = config('services.gemini.model', 'gemini-3.5-flash');
        $url = "https://generativelanguage.googleapis.com/v1beta/models/{$model}:generateContent?key={$apiKey}";

        $response = Http::timeout(30)->post($url, [
            'systemInstruction' => [
                'parts' => [[
                    'text' => 'Eres un asistente de alertas ambientales y desastres meteorológicos para la Región Valles. '
                        .'Da recomendaciones claras, prácticas y breves sobre qué hacer ante condiciones ambientales o desastres. '
                        .'Si detectas una situación de emergencia, recomienda seguir indicaciones de protección civil y buscar ayuda local. '
                        .'Responde en español.',
                ]],
            ],
            'contents' => $contents,
        ]);

        if (! $response->successful()) {
            return 'No se pudo obtener respuesta de la IA en este momento. Intenta de nuevo más tarde.';
        }

        return data_get($response->json(), 'candidates.0.content.parts.0.text', 'Sin respuesta.');
    }
}
