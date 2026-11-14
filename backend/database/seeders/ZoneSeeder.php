<?php

namespace Database\Seeders;

use App\Models\Zone;
use Illuminate\Database\Seeder;

class ZoneSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        Zone::query()->updateOrCreate(
            ['slug' => 'region-valles'],
            ['name' => 'Región Valles']
        );
    }
}
