<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use App\Models\Product;
use Faker\Factory as Faker;

class OrderDetailSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $faker = Faker::create();

        $orders = DB::table('orders')->get();

        foreach ($orders as $order) {
            $numProducts = rand(1, 5); // Số lượng sản phẩm trong mỗi đơn hàng từ 1 đến 5
            
            for ($j = 0; $j < $numProducts; $j++) {
                $productId = Product::inRandomOrder()->first()->id; // Giả sử bạn có bảng `products`
                
                DB::table('order_detail')->insert([
                    'product_id' => $productId,
                    'order_id' => $order->id,
                    'quantity' => $faker->numberBetween(1, 10),
                    'price' => $faker->randomFloat(2, 20, 200)
                ]);
            }
        }
    }
}
