<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class CategoryTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $category = [
            ["name" => "Cơm","image_url"=>"com.png"],
            ["name" => "Phở","image_url"=>"pho.png"],
            ["name" => "Bún","image_url"=>"bun.png"],
            ["name" => "Coca","image_url"=>"coca.png"],
            ["name" => "pepsi","image_url"=>"pepsi.png"],
        ];

        foreach($category as $ct){
            DB::table("category")->insert($ct);
        }
    }
}
