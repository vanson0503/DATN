<?php

namespace Database\Seeders;

use App\Models\User;
// use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        $this->call([
            CategoryTableSeeder::class,
            ProductTableSeeder::class,
            ImageTableSeeder::class,
            ProductCategoryTableSeeder::class,
            CustomerTableSeeder::class,
            ReviewTableSeeder::class,
            FavoriteTableSeeder::class,
            CartTableSeeder::class,
            LocationTableSeeder::class,
            AdminTableSeeder::class,
        ]);
    }
}
