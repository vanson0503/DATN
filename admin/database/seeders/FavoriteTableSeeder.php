<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Customer;
use App\Models\Product;
use App\Models\Favorite;

class FavoriteTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $faker = \Faker\Factory::create();

        // Lặp để tạo dữ liệu giả
        for ($i = 0; $i < 200; $i++) {
            // Chọn ngẫu nhiên một khách hàng và một sản phẩm
            $customerId = Customer::inRandomOrder()->first()->id;
            $productId = Product::inRandomOrder()->first()->id;

            // Kiểm tra xem sản phẩm đã được yêu thích bởi khách hàng này chưa
            $existingFavorite = Favorite::where('customer_id', $customerId)
                ->where('product_id', $productId)
                ->exists();

            // Nếu sản phẩm chưa được yêu thích, thêm vào bảng favorite
            if (!$existingFavorite) {
                Favorite::create([
                    'customer_id' => $customerId,
                    'product_id' => $productId,
                ]);
            }
        }
    }
}
