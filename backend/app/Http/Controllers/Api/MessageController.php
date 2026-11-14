<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Message;
use Illuminate\Http\JsonResponse;

class MessageController extends Controller
{
    public function index(): JsonResponse
    {
        $messages = Message::query()
            ->with('zone')
            ->orderByDesc('published_at')
            ->orderByDesc('created_at')
            ->limit(100)
            ->get();

        return response()->json(['data' => $messages]);
    }
}
