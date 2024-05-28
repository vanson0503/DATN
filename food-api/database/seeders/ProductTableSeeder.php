<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Product;
use Faker\Factory as Faker;

class ProductTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Xóa dữ liệu cũ trong bảng trước khi thêm dữ liệu mới
        // Product::truncate();

        // Tạo đối tượng Faker
        $faker = Faker::create();

        // Tạo dữ liệu giả mạo cho khoảng 100 sản phẩm
        $products = [];
        for ($i = 0; $i < 100; $i++) {
            $products[] = [
                'name' => $faker->sentence(3),
                'description' => $faker->text,
                'ingredient' => $faker->sentence(5),
                'calo' => $faker->numberBetween(50, 500),
                'quantity' => $faker->numberBetween(1, 50),
                'price' => $faker->randomFloat(2, 5, 100),
            ];
        }

        // Chèn dữ liệu vào bảng product
        Product::insert($products);
        
    }
}
