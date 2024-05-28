<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Faker\Factory as Faker;

class CustomerTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Xóa dữ liệu cũ trong bảng trước khi thêm dữ liệu mới
        // DB::table('customer')->truncate();

        // Tạo đối tượng Faker
        $faker = Faker::create();

        // Tạo dữ liệu giả mạo cho khoảng 100 khách hàng
        $customers = [];
        for ($i = 0; $i < 100; $i++) {
            $customers[] = [
                'full_name' => $faker->name,
                'phone_number' => $faker->phoneNumber,
                'email' => $faker->unique()->safeEmail,
            ];
        }

        // Chèn dữ liệu vào bảng customer
        DB::table('customer')->insert($customers);
    }
}
