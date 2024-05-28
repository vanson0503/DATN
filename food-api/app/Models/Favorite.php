<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Favorite extends Model
{
    use HasFactory;
    protected $table = "favorite";

    public $timestamps = false;

    protected $fillable = ['product_id','customer_id'];

    public function product()
    {
        return $this->belongsTo(Product::class);
    }
}
