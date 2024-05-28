<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Location extends Model
{
    use HasFactory;

    protected $table = "location";

    protected $fillable = [
        'customer_id',
        'name',
        'phone_number',
        'address',
        'is_default',
    ];

    public $timestamps = false;
}
