<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use App\Models\Customer;
use App\Models\Admin;
use Faker\Factory as Faker;

class MessagesTableSeeder extends Seeder
{
    public function run()
    {
        $faker = Faker::create();  // Tạo một instance của Faker
        $customers = Customer::all();
        $admins = Admin::all();

        for ($i = 0; $i < 2000; $i++) {
            $senderIsCustomer = rand(0, 1) == 1;

            if ($senderIsCustomer) {
                // Khách hàng là người gửi
                DB::table('messages')->insert([
                    'sender_id' => $customers->random()->id,
                    'receiver_id' => null, // Không có người nhận
                    'content' => $faker->realText(200),  // Sinh nội dung ngẫu nhiên
                    'sender_type' => 'customer',
                ]);
            } else {
                // Admin là người gửi
                $admin = $admins->random();
                DB::table('messages')->insert([
                    'sender_id' => $admin->id,
                    'receiver_id' => $customers->random()->id,
                    'content' => $faker->realText(200),  // Sinh nội dung ngẫu nhiên
                    'sender_type' => 'admin',
                ]);
            }
        }
    }
}
