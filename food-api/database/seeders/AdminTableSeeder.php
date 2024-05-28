<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Faker\Factory as Faker;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\DB;

class AdminTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $faker = Faker::create();

        $admins = [];
        for ($i = 0; $i < 10; $i++) {
            $admins[] = [
                'username' => $faker->userName,
                'password' => Hash::make('password'),
                'image_url' => $faker->imageUrl(),
                'status' => $faker->randomElement(['active', 'inactive']),
                'role' => $faker->randomElement(['admin', 'manager', 'staff']),
            ];
        }

        // Chèn dữ liệu vào bảng admin
        DB::table('admin')->insert($admins);
    }
}
