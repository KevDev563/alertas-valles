<?php

use App\Http\Controllers\Api\AlertController;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\ChatController;
use App\Http\Controllers\Api\MessageController;
use App\Http\Controllers\Api\SensorReadingController;
use Illuminate\Support\Facades\Route;

Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);

Route::get('/readings/latest', [SensorReadingController::class, 'latest']);
Route::get('/readings', [SensorReadingController::class, 'index']);
Route::get('/alerts', [AlertController::class, 'index']);
Route::get('/messages', [MessageController::class, 'index']);
Route::post('/chat', ChatController::class);

Route::middleware('auth:sanctum')->group(function () {
    Route::get('/me', [AuthController::class, 'me']);
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::post('/alerts', [AlertController::class, 'store']);
});
