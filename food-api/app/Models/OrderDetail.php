<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class OrderDetail extends Model
{
    use HasFactory;

    protected $table = "order_detail";
    public $timestamps = false;
    protected $fillable = ['order_id', 'product_id', 'quantity', 'price'];

    public function order() {
        return $this->belongsTo(Orders::class);
    }

    public function product() {
        return $this->belongsTo(Product::class, 'product_id');
    }
}
