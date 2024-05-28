<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use App\Models\Image;
use App\Models\Product;


class ImageTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Xóa dữ liệu cũ trong bảng trước khi thêm dữ liệu mới
        Image::truncate();

        // Lấy tất cả sản phẩm từ cơ sở dữ liệu
        $products = Product::all();

        // Tạo dữ liệu cho tất cả sản phẩm có 3 ảnh
        foreach ($products as $product) {
            for ($i = 1; $i <= 3; $i++) {
                $imageData = [
                    'product_id' => $product->id,
                    'imgurl' => 'product.png', // Tên file ảnh là product.png
                ];

                Image::create($imageData);
            }
        }
    }
}
