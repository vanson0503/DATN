<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Faker\Factory as Faker;
use App\Models\Customer;
use App\Models\Product;
use App\Models\Cart;

class CartTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $faker = Faker::create();

        // Lấy danh sách tất cả khách hàng
        $customers = Customer::all();

        // Lấy danh sách tất cả sản phẩm
        $products = Product::all();

        // Duyệt qua mỗi khách hàng
        foreach ($customers as $customer) {
            // Tạo số lượng sản phẩm ngẫu nhiên trong giỏ hàng (từ 1 đến 5)
            $numProductsInCart = $faker->numberBetween(1, 5);

            // Lấy ngẫu nhiên $numProductsInCart sản phẩm từ danh sách sản phẩm
            $selectedProducts = $faker->randomElements($products->toArray(), $numProductsInCart);

            // Duyệt qua mỗi sản phẩm trong giỏ hàng của khách hàng
            foreach ($selectedProducts as $selectedProduct) {
                // Tạo bản ghi giỏ hàng
                Cart::create([
                    'customer_id' => $customer->id,
                    'product_id' => $selectedProduct['id'],
                    'quantity' => $faker->numberBetween(1, 10), // Số lượng ngẫu nhiên từ 1 đến 10
                ]);
            }
        }
    }
}
