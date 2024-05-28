<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use App\Models\Customer;
use Faker\Factory as Faker;

class OrdersSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run()
    {
        $faker = Faker::create();
        
        for ($i = 0; $i < 50; $i++) {
            $customerId = Customer::inRandomOrder()->first()->id; // Giả sử bạn có bảng `customers`

            DB::table('orders')->insert([
                'customer_id' => $customerId,
                'name' => $faker->name,
                'phone_number' => $faker->phoneNumber,
                'address' => $faker->address,
                'note' => $faker->realText(200),
                'payment' => $faker->randomElement(['cash', 'card', 'online']),
                'payment_status' => $faker->randomElement(['initialization', 'pending', 'completed', 'failed']),
                'status' => $faker->randomElement(['initialization', 'pending', 'completed', 'cancelled']),
                'created_time' => $faker->dateTimeThisYear(),
                'updated_time' => $faker->dateTimeThisYear()
            ]);
        }
    }
}
