<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;
use App\Models\Customer;

class AccountTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $customers = Customer::all();
        foreach ($customers as $customer) {
            $accountData = [
                'customer_id' => $customer->id,
                'username' => Str::random(8),
                'password' => Hash::make('password'),
                'role' => 'customer',
                'status' => 'active',
                'verify_code' => null,
            ];
            DB::table('account')->insert($accountData);
        }
    }
}
