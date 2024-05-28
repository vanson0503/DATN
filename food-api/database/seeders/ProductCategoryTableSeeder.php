<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use App\Models\Product;
use App\Models\Category;


class ProductCategoryTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Xóa dữ liệu cũ trong bảng trước khi thêm dữ liệu mới
        DB::table('product_category')->truncate();

        // Lấy danh sách tất cả sản phẩm và danh mục
        $products = Product::all();
        $categories = Category::all();

        // Tạo một mảng chứa tất cả các cặp product_id và category_id độc đáo
        $data = [];
        foreach ($products as $product) {
            // Lấy một số lượng ngẫu nhiên của danh mục cho mỗi sản phẩm
            $randomCategories = $categories->random(mt_rand(1, $categories->count()));

            // Thêm mỗi cặp product_id và category_id vào mảng dữ liệu
            foreach ($randomCategories as $category) {
                $data[] = [
                    'product_id' => $product->id,
                    'category_id' => $category->id,
                ];
            }
        }

        // Chèn dữ liệu vào bảng product_category
        DB::table('product_category')->insert($data);
    }
}
