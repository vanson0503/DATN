<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Faker\Factory as Faker;
use Illuminate\Support\Facades\Hash;

class CustomerTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $faker = Faker::create();

        $customers = [];
        for ($i = 0; $i < 100; $i++) {
            $phone_number = $faker->numerify('##########'); // Tạo số điện thoại bắt đầu bằng 0 và có 9 hoặc 10 số đằng sau
            if (strlen($phone_number) == 10) {
                $phone_number = '0' . $phone_number; // Nếu số điện thoại có 10 số, thêm số 0 vào đầu
            }
            $customers[] = [
                'full_name' => $faker->name,
                'phone_number' => $phone_number,
                'email' => $faker->unique()->safeEmail,
                'image_url' => $faker->imageUrl(),
                'password' => Hash::make('password'), // Thay 'password' bằng giá trị mong muốn
            ];
        }

        DB::table('customer')->insert($customers);


    }
}
