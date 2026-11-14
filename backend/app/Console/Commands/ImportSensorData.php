<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use Illuminate\Support\Facades\DB;

class ImportSensorData extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'sensor:import {path? : Ruta del archivo CSV} {--truncate : Vaciar lecturas antes de importar}';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Importa lecturas de sensores desde un archivo CSV';

    /**
     * Execute the console command.
     */
    public function handle(): int
    {
        $path = $this->argument('path') ?: database_path('data/datos.csv');

        if (! is_file($path)) {
            $this->error("No se encontró el archivo: {$path}");

            return self::FAILURE;
        }

        if ($this->option('truncate')) {
            DB::table('sensor_readings')->truncate();
            $this->info('Tabla sensor_readings vaciada.');
        }

        $handle = fopen($path, 'r');
        if ($handle === false) {
            $this->error('No se pudo abrir el archivo.');

            return self::FAILURE;
        }

        $header = fgetcsv($handle);
        if ($header === false) {
            fclose($handle);
            $this->error('El archivo CSV está vacío.');

            return self::FAILURE;
        }

        $batch = [];
        $count = 0;
        $now = now()->toDateTimeString();

        while (($row = fgetcsv($handle)) !== false) {
            if (count($row) < 5) {
                continue;
            }

            [$id, $estacion, $sensorName, $sensorDato, $fecha] = $row;

            $batch[] = [
                'estacion' => $estacion,
                'sensor_name' => $sensorName,
                'sensor_dato' => $sensorDato,
                'fecha' => $fecha,
                'created_at' => $now,
                'updated_at' => $now,
            ];

            if (count($batch) >= 1000) {
                DB::table('sensor_readings')->insert($batch);
                $count += count($batch);
                $batch = [];
            }
        }

        if ($batch !== []) {
            DB::table('sensor_readings')->insert($batch);
            $count += count($batch);
        }

        fclose($handle);

        $this->info("Importación completada: {$count} lecturas.");

        return self::SUCCESS;
    }
}
