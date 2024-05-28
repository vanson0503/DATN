<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Cart extends Model
{
    use HasFactory;
    protected $table = "cart";

    protected $fillable = ['product_id', 'customer_id', 'quantity'];

    public $timestamps = false;
}
