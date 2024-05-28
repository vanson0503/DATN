<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Faker\Factory as Faker;


class ReviewTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Tạo đối tượng Faker
        $faker = Faker::create();

        // Lấy danh sách ID của tất cả các khách hàng và sản phẩm
        $customerIds = DB::table('customer')->pluck('id');
        $productIds = DB::table('product')->pluck('id');

        // Tạo 10 bản ghi đánh giá ngẫu nhiên
        for ($i = 0; $i < 1000; $i++) {
            DB::table('review')->insert([
                'customer_id' => $faker->randomElement($customerIds),
                'product_id' => $faker->randomElement($productIds),
                'rate' => $faker->numberBetween(1, 5),
                'content' => $faker->text
            ]);
        }
    }
}
