<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Orders extends Model
{
    use HasFactory;

    protected $table = "orders";
    public $timestamps = false;

    protected $fillable = [
        'customer_id', 'name', 'phone_number', 'address', 'note',
        'payment', 'payment_status', 'status', 'created_time', 'updated_time'
    ];

    public function details() {
        return $this->hasMany(OrderDetail::class);
    }

    public function customer() {
        return $this->belongsTo(Customer::class, 'customer_id');
    }
}
