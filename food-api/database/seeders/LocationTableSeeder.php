<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Customer;
use App\Models\Location;
use Faker\Factory as Faker;

class LocationTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $faker = Faker::create();

        // Lấy danh sách tất cả khách hàng
        $customers = Customer::all();

        // Duyệt qua mỗi khách hàng
        foreach ($customers as $customer) {
            // Tạo số lượng địa chỉ ngẫu nhiên cho mỗi khách hàng (từ 1 đến 5)
            $numLocations = $faker->numberBetween(1, 5);

            // Tạo mỗi địa chỉ cho khách hàng
            for ($i = 0; $i < $numLocations; $i++) {
                Location::create([
                    'customer_id' => $customer->id,
                    'name' => $faker->name,
                    'phone_number' => $faker->phoneNumber,
                    'address' => $faker->address,
                    'is_default' => $i === 0 ? true : false, // Đánh dấu địa chỉ đầu tiên là mặc định
                ]);
            }
        }
 
    }
}
