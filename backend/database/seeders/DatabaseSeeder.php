<?php

namespace Database\Seeders;

use App\Models\Alert;
use App\Models\Message;
use App\Models\User;
use App\Models\Zone;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        $this->call(ZoneSeeder::class);

        $zone = Zone::query()->where('slug', 'region-valles')->first();

        $user = User::query()->updateOrCreate(
            ['email' => 'demo@alertas-valles.test'],
            [
                'name' => 'Usuario Demo',
                'password' => 'password',
            ]
        );

        if ($zone) {
            $user->zones()->syncWithoutDetaching([$zone->id]);

            Alert::query()->updateOrCreate(
                ['title' => 'Calidad del aire: precaución'],
                [
                    'zone_id' => $zone->id,
                    'description' => 'Se registraron niveles moderados de partículas. Evita actividades intensas al aire libre si eres sensible.',
                    'level' => 'warning',
                    'source' => 'system',
                    'active' => true,
                    'starts_at' => now(),
                ]
            );

            Alert::query()->updateOrCreate(
                ['title' => 'Condiciones estables'],
                [
                    'zone_id' => $zone->id,
                    'description' => 'Sin eventos meteorológicos severos reportados en la Región Valles por el momento.',
                    'level' => 'info',
                    'source' => 'manual',
                    'active' => true,
                    'starts_at' => now(),
                ]
            );

            Message::query()->updateOrCreate(
                ['title' => 'Bienvenido a Alertas Valles'],
                [
                    'zone_id' => $zone->id,
                    'body' => 'Este es un aviso de ejemplo. Aquí se publicarán comunicados y recomendaciones para la Región Valles.',
                    'source' => 'manual',
                    'published_at' => now(),
                ]
            );
        }
    }
}
