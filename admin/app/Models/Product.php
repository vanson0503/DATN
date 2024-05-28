<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Product extends Model
{
    use HasFactory;
    protected $table = "product";
    public $timestamps = false;

    protected $fillable = [
        'name', // Thêm trường 'name' vào mảng fillable
        'description',
        'ingredient',
        'calo',
        'quantity',
        'price',
        'discount',
    ];

    public function category()
    {
        return $this->belongsToMany(Category::class, 'product_category', 'product_id', 'category_id');
    }

    public function images()
    {
        return $this->hasMany(Image::class);
    }

}
